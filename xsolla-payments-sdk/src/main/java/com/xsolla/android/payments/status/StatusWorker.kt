package com.xsolla.android.payments.status

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xsolla.android.payments.BuildConfig
import com.xsolla.android.payments.XPayments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

class StatusWorker(val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val ARG_PROJECT_ID = "PROJECT_ID"
        const val ARG_EXTERNAL_ID = "EXTERNAL_ID"
        const val ARG_START_TIME = "START_TIME"

        private val successStatuses = listOf("done", "lost")
        private val inProgressStatuses = listOf("created", "processing", "authorized")
    }

    private val statusApi by lazy {
        val interceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
                    .addHeader("X-ENGINE", "ANDROID")
                    .addHeader("X-ENGINE-V", Build.VERSION.RELEASE)
                    .addHeader("X-SDK", "PAYMENTS")
                    .addHeader("X-SDK-V", BuildConfig.VERSION_NAME)
                    .url(originalRequest.url().newBuilder()
                            .addQueryParameter("engine", "android")
                            .addQueryParameter("engine_v", Build.VERSION.RELEASE)
                            .addQueryParameter("sdk", "payments")
                            .addQueryParameter("sdk_v", BuildConfig.VERSION_NAME)
                            .build()
                    )
            val newRequest = builder.build()
            chain.proceed(newRequest)
        }

        val httpClient = with(OkHttpClient().newBuilder()) {
            addInterceptor(interceptor)
            build()
        }

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.xsolla.com")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        retrofit.create(StatusApi::class.java)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        coroutineScope {
            val projectId = inputData.getInt(ARG_PROJECT_ID, 0)
            val externalId = inputData.getString(ARG_EXTERNAL_ID)!!
            val startTime = inputData.getLong(ARG_START_TIME, Long.MAX_VALUE)

            try {
                val status = statusApi.getPaymentStatus(projectId, externalId)
                if (successStatuses.contains(status.status)) {
                    broadcastSuccess(context, status)
                    Result.success()
                } else if (inProgressStatuses.contains(status.status)) {
                    if (isRetryAllowed(startTime)) {
                        Result.retry()
                    } else {
                        broadcastError(context, "Timeout")
                        Result.failure()
                    }
                } else {
                    broadcastError(context, "Status: ${status.status}")
                    Result.failure()
                }
            } catch (e1: HttpException) {
                if (e1.code() == 404) {
                    Log.e("XsollaPayments", "HTTP 404 - Transaction not found")
                    if (isRetryAllowed(startTime)) {
                        Result.retry()
                    } else {
                        broadcastError(context, "Timeout")
                        Result.failure()
                    }
                } else if (e1.code() == 403) {
                    Log.e("XsollaPayments", "HTTP 403 - Method is available only for projects with serverless integration")
                    Result.failure()
                } else {
                    broadcastError(context, "HTTP ${e1.code()}")
                    Result.failure()
                }
            } catch (e2: SocketTimeoutException) {
                Result.retry()
            } catch (e: Exception) {
                broadcastError(context, "Exception: ${e.javaClass.name}")
                Result.failure()
            }
        }
    }

    private fun isRetryAllowed(startTime: Long): Boolean =
            System.currentTimeMillis() - startTime < 10 * 60 * 1000

    private fun broadcastSuccess(context: Context, paymentStatus: PaymentStatus) {
        val intent = Intent().apply {
            action = XPayments.ACTION_STATUS
            setPackage(context.packageName)
            putExtra(XPayments.EXTRA_STATUS,
                    XPayments.CheckTransactionResult(
                            XPayments.CheckTransactionResultStatus.SUCCESS,
                            paymentStatus,
                            null
                    )
            )
        }
        context.sendBroadcast(intent)
    }

    private fun broadcastError(context: Context, message: String) {
        val intent = Intent().apply {
            action = XPayments.ACTION_STATUS
            setPackage(context.packageName)
            putExtra(XPayments.EXTRA_STATUS,
                    XPayments.CheckTransactionResult(
                            XPayments.CheckTransactionResultStatus.FAIL,
                            null,
                            message
                    )
            )
        }
        context.sendBroadcast(intent)
    }

}
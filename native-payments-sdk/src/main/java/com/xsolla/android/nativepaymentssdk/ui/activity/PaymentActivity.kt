package com.xsolla.android.nativepaymentssdk.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.xsolla.android.nativepaymentssdk.R
import com.xsolla.android.nativepaymentssdk.XPaystation
import com.xsolla.android.nativepaymentssdk.ui.adapter.SavedCardsAdapter
import com.xsolla.android.nativepaymentssdk.ui.fragment.SavedPaymentBottomSheet
import com.xsolla.android.nativepaymentssdk.vm.VmPayment
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.data.AccessToken

class PaymentActivity : AppCompatActivity(),
    SavedPaymentBottomSheet.CancelListener,
    SavedPaymentBottomSheet.NewCardListener,
    SavedCardsAdapter.CardClickListener {

    companion object {
        const val EXTRA_TOKEN = "token"

        const val RESULT = "result"
    }

    private val vmPayment: VmPayment by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_native_payments_activity_payment)
        vmPayment.token = intent.getStringExtra(EXTRA_TOKEN)
        SavedPaymentBottomSheet.newInstance().show(supportFragmentManager, null)
        vmPayment.result.observe(this) {
            if (it != "") {
                finishWithResult(
                    Activity.RESULT_OK,
                    XPaystation.Result(XPaystation.Status.COMPLETED)
                )
                finish()
            }
        }
    }

    override fun onCancel() {
        finishWithResult(Activity.RESULT_CANCELED, XPaystation.Result(XPaystation.Status.CANCELLED))
        finish()
    }

    override fun onPayWithNewCard() {
        val intent = XPayments.createIntentBuilder(this)
            .accessToken(AccessToken(vmPayment.token!!))
            .isSandbox(false)
            .build()
        startActivityForResult(intent, 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111) {
            val result = XPayments.Result.fromResultIntent(data)
            Toast.makeText(this, result.status.name, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: XPaystation.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }

    override fun onCardClick(cardInfo: VmPayment.CardInfo) {
        showBiometricPrompt(
            title = "Please confirm payment",
            subtitle = "You are paying from your ${cardInfo.psName} ${cardInfo.name.takeLast(5)}",
            activity = this,
            cardId = cardInfo.id
        )
    }

    private fun showBiometricPrompt(
        title: String = "",
        subtitle: String = "",
        activity: AppCompatActivity,
        cardId: Int
    ) {

        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDeviceCredentialAllowed(true)
        val promptInfo = builder.build()

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                println("!!! biometric error")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                println("!!! biometric fail")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                println("!!! biometric success")
                vmPayment.payWithSavedCard(cardId)
            }
        }

        val biometricPrompt =
            BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), callback)

        biometricPrompt.authenticate(promptInfo)
    }
}
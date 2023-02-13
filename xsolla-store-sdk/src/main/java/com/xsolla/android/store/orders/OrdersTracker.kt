package com.xsolla.android.store.orders

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.api.StoreApi
import com.xsolla.android.store.callbacks.OrderStatusListener
import com.xsolla.android.store.entity.response.order.OrderResponse
import com.xsolla.android.store.entity.response.order.WsOrderResponse
import io.github.centrifugal.centrifuge.*
import io.github.centrifugal.centrifuge.EventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

internal class OrdersTracker(
    private val storeApi: StoreApi
) {
    companion object {
        private const val CENTRIFUGO_ENDPOINT = "wss://ws-store.xsolla.com/connection/websocket"
        private const val SHORT_POLLING_TIMEOUT = 3 * 1000L // 3 sec
    }

    private var listeners = mutableMapOf<String, OrderStatusListener>()
    private var centrifugoClient: Client? = null

    fun addToTracking(
        listener: OrderStatusListener,
        orderId: String,
        userId: String,
        accessToken: String,
        projectId: Int
    ) {
        listeners[orderId] = listener

        if (centrifugoClient != null)
            return

        val centrifugeListener: EventListener = object : EventListener() {
            override fun onError(client: Client?, event: ErrorEvent) {
                if (centrifugoClient?.state in listOf( ClientState.CONNECTED, ClientState.CONNECTING )) {
                    event.error.printStackTrace()
                    switchToShortPolling(projectId)
                }
            }

            override fun onPublication(client: Client?, event: ServerPublicationEvent?) {
                val data = event?.data
                if (data != null) {
                    val response = try {
                        Gson().fromJson(String(data), WsOrderResponse::class.java)
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        switchToShortPolling(projectId)
                        return
                    }

                    val listenerKey = response.orderId.toString()
                    val orderListener = listeners[listenerKey]
                    if (orderListener != null) {
                        orderListener.onStatusUpdate(response.status)
                        if (response.status in listOf( OrderResponse.Status.DONE, OrderResponse.Status.CANCELED)) {
                            listeners.remove(listenerKey)
                            onListenersModified()
                        }
                    }
                }
            }
        }

        val centrifugeOptions = Options()
        val centrifugeData = CentrifugeConnectionData(userId, accessToken, projectId)
        centrifugeOptions.data = Gson().toJson(centrifugeData).toByteArray()

        centrifugoClient = Client(CENTRIFUGO_ENDPOINT, centrifugeOptions, centrifugeListener)
        centrifugoClient?.connect()
    }

    private fun onListenersModified(
    ) {
        if (listeners.isEmpty()) {
            centrifugoClient?.close(0)
            centrifugoClient = null
        }
    }

    private fun switchToShortPolling(projectId: Int) {
        listeners.forEach { startOrderShortPolling(it.value, it.key, projectId) }
        listeners.clear()
        onListenersModified()
    }

    private fun startOrderShortPolling(
        listener: OrderStatusListener,
        orderId: String,
        projectId: Int
    ) {
        val delayTimer = Timer()
        lateinit var singleRunTask: Runnable

        singleRunTask = Runnable {
            storeApi.getOrder(projectId, orderId)
                .enqueue(object : Callback<OrderResponse> {
                    override fun onResponse(
                        call: Call<OrderResponse>,
                        response: Response<OrderResponse>
                    ) {
                        val status = response.body()?.status
                        if (response.isSuccessful && status != null) {
                            if (status !in listOf(OrderResponse.Status.DONE, OrderResponse.Status.CANCELED)) {
                                delayTimer.schedule(object : TimerTask() {
                                    override fun run() {
                                        singleRunTask.run()
                                    }
                                }, SHORT_POLLING_TIMEOUT)
                            }
                            listener.onStatusUpdate(status)
                        } else {
                            listener.onFailure()
                        }
                    }

                    override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                        delayTimer.schedule(object : TimerTask() {
                            override fun run() {
                                singleRunTask.run()
                            }
                        }, SHORT_POLLING_TIMEOUT)
                    }
                })
        }
        singleRunTask.run()
    }

    private data class CentrifugeConnectionData(
        @SerializedName("user_external_id")
        val UserId: String,
        @SerializedName("auth")
        val Token: String,
        @SerializedName("project_id")
        val ProjectId: Int
    )
}


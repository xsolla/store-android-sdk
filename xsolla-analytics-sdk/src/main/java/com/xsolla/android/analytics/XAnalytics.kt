package com.xsolla.android.analytics

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.api.core.ApiFuture
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import java.io.InputStream
import java.util.*

/**
 * Entry point for Xsolla Analytics SDK
 */
class XAnalytics private constructor(
    val jsonCredentials: InputStream
) {

    companion object {

        private lateinit var instance: XAnalytics
        private lateinit var publisher: Publisher
        private lateinit var credentials: GoogleCredentials
        private const val projectName = "xsolla-dwh-partners"
        private const val topicName = "in_game_events_data"
        private val calendar: Calendar by lazy {
            Calendar.getInstance()
        }

        /**
         * Initialize SDK
         *
         * @param jsonCredentials      way to credentials.json file, if you don't have this one - please contact your account manager
         */
        fun init(jsonCredentials: InputStream) {

            credentials = GoogleCredentials.fromStream(jsonCredentials)
            val projectTopicName = ProjectTopicName.of(projectName, topicName)
            publisher = Publisher.newBuilder(projectTopicName)
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build()
            instance = XAnalytics(
                jsonCredentials
            )

        }

        /**
         * Sends a message to Xsolla backend
         *
         * @param message String typed message
         */
        fun publish(message: Message) {
            try {
                if (!this::instance.isInitialized) {  //if not initialized
                    throw IllegalStateException("XAnalytics SDK not initialized. Call \"XAnalytics.init()\" in MainActivity.onCreate()")
                } else {
                    //data can be only ByteArray -> Message is JSON formatted file -> then Message.toString()
                    val data = ByteString.copyFromUtf8(message.createMessage().toString())
                    val pubsubMessage = PubsubMessage.newBuilder().setData(data).build()
                    //response from server
                    val messageApiFuture: ApiFuture<String> = publisher.publish(pubsubMessage)!!
                    val messageId = messageApiFuture.get()
                    Log.i("XAnalytics", "message posted with messageId: $messageId")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        /**
         * Gets an unique hardware identifier of current device
         *
         * @return hardware id of current device in String format
         */
        @SuppressLint("HardwareIds")
        fun getHardwareId(context: Context): String {
            return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }

        /**
         * Gets an correctly formatted date
         *
         * @return date
         */
        fun getTime(): Int {
            return (calendar.timeInMillis / 1000).toInt()
        }

    }


}
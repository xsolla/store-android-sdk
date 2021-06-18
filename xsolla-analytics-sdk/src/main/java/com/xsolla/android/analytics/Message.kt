package com.xsolla.android.analytics

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

//Message equal to JSON format expected on backend
data class Message(
    val time: Int, // unix epoch type
    val entity: String,
    val action: String,
    @SerializedName("preauth_user_id")
    val idString: String? = null,
    @SerializedName("permanent_user_id")
    val idStringAuth: String? = null,
    @SerializedName("is_test")
    val isTest: Boolean? = null,
    val customAttr: String? = null
) {
    fun createMessage(): JSONObject { //func to generate JSON from our class
        val obj = JSONObject()
        try {
            obj.put("time", time)
            obj.put("entity", entity)
            obj.put("action", action)
            obj.put("preauth_user_id", idString)
            obj.put("permanent_user_id", idStringAuth)
            obj.put("is_test", isTest)
            obj.put("customAttr", customAttr)
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        return obj
    }

    object Entity {
        //entity tags
        const val Launch = "LAUNCH"
        const val Authentication = "AUTHENTICATION"
        const val StoreEvents = "STORE EVENTS"
        const val CustomEvents = "CUSTOM EVENTS"
    }
}

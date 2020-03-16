package com.xsolla.android.paystation.data

import org.json.JSONObject
import java.net.URLEncoder

class AccessData(
        private val projectId: Int,
        private val userId: String,
        private val isSandbox: Boolean
) {

    fun getUrlencodedString(): String {
        val root = JSONObject()

        val user = JSONObject()
        val settings = JSONObject()

        val id = JSONObject()
        id.put("value", userId)

        user.put("id", id)

        root.put("user", user)

        settings.put("project_id", projectId)
        if (isSandbox) settings.put("mode", "sandbox")

        root.put("settings", settings)

        val jsonString = root.toString()

        return URLEncoder.encode(jsonString, "UTF-8")
    }

    class Builder() {

        private var projectId: Int? = null
        private var userId: String? = null
        private var isSandbox: Boolean = true

        fun projectId(projectId: Int) = apply { this.projectId = projectId }
        fun userId(userId: String) = apply { this.userId = userId }
        fun isSandbox(isSandbox: Boolean) = apply { this.isSandbox = isSandbox }

        fun build(): AccessData {
            val projectId = projectId?: throw IllegalArgumentException()
            val userId = userId?: throw IllegalArgumentException()
            return AccessData(projectId, userId, isSandbox)
        }

    }
}
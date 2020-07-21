package com.xsolla.android.payments.data

import android.os.Build
import com.xsolla.android.payments.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class AccessData(
        private val projectId: Int,
        private val userId: String,
        private val isSandbox: Boolean,
        private val theme: String? = null,
        private val externalId: String?,
        private val virtualItems: List<VirtualItem>?
) {

    data class VirtualItem(
            val sku: String,
            val amount: Int
    )

    fun getUrlencodedString(): String {
        val root = JSONObject()

        val user = JSONObject()
        val id = JSONObject()
        id.put("value", userId)
        user.put("id", id)
        root.put("user", user)

        val settings = JSONObject()
        settings.put("project_id", projectId)
        if (isSandbox) settings.put("mode", "sandbox")
        val ui = JSONObject()
        if (theme != null) {
            ui.put("theme", theme)
        }
        settings.put("ui", ui)
        if (externalId != null) {
            settings.put("external_id", externalId)
        }
        settings.put("xsolla_product_tag", "SDK-payments_ver-${BuildConfig.VERSION_NAME}_integr-simplified_engine-android_enginever-${Build.VERSION.RELEASE}")
        root.put("settings", settings)

        if (virtualItems != null) {
            val purchase = JSONObject()
            val virtual_items = JSONObject()
            val items = JSONArray()
            virtualItems.forEach {
                val itemsItem = JSONObject()
                itemsItem.put("sku", it.sku)
                itemsItem.put("amount", it.amount)
                items.put(itemsItem)
            }
            virtual_items.put("items", items)
            purchase.put("virtual_items", virtual_items)
            root.put("purchase", purchase)
        }

        val jsonString = root.toString()

        return URLEncoder.encode(jsonString, "UTF-8")
    }

    class Builder() {

        private var projectId: Int? = null
        private var userId: String? = null
        private var isSandbox: Boolean = true
        private var theme: String? = null
        private var externalId: String? = null
        private var virtualItems: List<VirtualItem>? = null

        /**
         * Set Project ID from Publisher Account. Simplified integration must be enabled for this project.
         */
        fun projectId(projectId: Int) = apply { this.projectId = projectId }

        /**
         * Set User ID
         */
        fun userId(userId: String) = apply { this.userId = userId }

        /**
         * Set Sandbox mode
         */
        fun isSandbox(isSandbox: Boolean) = apply { this.isSandbox = isSandbox }

        /**
         * Set Pay Station theme
         */
        fun theme(theme: String) = apply { this.theme = theme }

        /**
         * Set transaction external ID. Used to query its status.
         */
        fun externalId(externalId: String) = apply { this.externalId = externalId }

        /**
         * Set a list of Virtual Items to purchase
         */
        fun virtualItems(virtualItems: List<VirtualItem>) = apply { this.virtualItems = virtualItems }

        /**
         * Build AccessData object
         */
        fun build(): AccessData {
            val projectId = projectId?: throw IllegalArgumentException()
            val userId = userId?: throw IllegalArgumentException()
            return AccessData(projectId, userId, isSandbox, theme, externalId, virtualItems)
        }

    }
}
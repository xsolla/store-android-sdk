package com.xsolla.android.storesdkexample.data.store

import android.content.Context
import android.content.Intent
import com.xsolla.android.paystation.XPaystation
import com.xsolla.android.paystation.data.AccessData
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.data.db.DB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

object Store {

    data class Price(
            val currencyId: String,
            val currencyName: String,
            val amount: BigDecimal,
            val amountWithoutDiscount: BigDecimal,
            val isVirtual: Boolean
    )


    @JvmStatic
    fun getVirtualBalance(callback: VirtualBalanceCallback) = GlobalScope.launch {
        val availableCurrencies = HashMap<String, CatalogItem>()
        Catalog.catalog.filter {
            it.type == "virtual_currency_package"
        }.forEach { vcPack ->
            vcPack.bundle_content?.let {
                availableCurrencies[it.currency] = vcPack
            }
        }
        val inventoryCurrencies = DB.db.virtualCurrencyDao().getAll()
        val result = availableCurrencies.map { currency ->
            val balanceStr = inventoryCurrencies.find { it.currency == currency.key }?.amount
            val balance = if (balanceStr != null) BigDecimal(balanceStr) else BigDecimal.ZERO
            VirtualBalance(
                    currency.key,
                    balance.toInt(),
                    currency.key,
                    currency.value.description,
                    currency.value.image_url
            )
        }
        withContext(Dispatchers.Main) {
            callback.onSuccess(result)
        }
    }

    data class VirtualBalance(
            val id: String,
            val amount: Int,
            val name: String? = null,
            val description: String? = null,
            val imageUrl: String? = null
    )

    interface VirtualBalanceCallback {
        fun onSuccess(balances: List<VirtualBalance>)
        fun onFailure(errorMessage: String)
    }


    @JvmStatic
    fun getVirtualCurrencyPacks(callback: VirtualCurrencyPacksCallback) = GlobalScope.launch {
        val virtualCurrencyPacks = Catalog.catalog.filter {
            it.type == "virtual_currency_package"
        }.map {
            VirtualCurrencyPack(
                    it.sku,
                    listOf(Price(it.price.currency, it.price.currency, it.price.amount, it.price.amount, false)),
                    null,
                    it.display_name,
                    it.description,
                    it.image_url
            )
        }
        withContext(Dispatchers.Main) {
            callback.onSuccess(virtualCurrencyPacks)
        }
    }

    data class VirtualCurrencyPack(
            val sku: String,
            val realPrices: List<Price>?,
            val virtualPrices: List<Price>?,
            val name: String? = null,
            val description: String? = null,
            val imageUrl: String? = null
    )

    interface VirtualCurrencyPacksCallback {
        fun onSuccess(virtualCurrencyPacks: List<VirtualCurrencyPack>)
        fun onFailure(errorMessage: String)
    }


    @JvmStatic
    fun createPaystationIntent(context: Context, sku: String, callback: CreatePaystationIntentCallback) = GlobalScope.launch {
        val externalId = XPaystation.generateExternalId()
        val accessData = AccessData.Builder()
                .projectId(BuildConfig.PROJECT_ID)
                .userId(UUID.randomUUID().toString())
                .isSandbox(BuildConfig.IS_SANDBOX)
                .theme("dark")
                .externalId(externalId)
                .virtualItems(listOf(AccessData.VirtualItem(sku, 1)))
                .build()
        val intent = XPaystation.createIntentBuilder(context)
                .accessData(accessData)
                .useWebview(true)
                .isSandbox(BuildConfig.IS_SANDBOX)
                .build()
        withContext(Dispatchers.Main) {
            callback.onSuccess(intent)
        }
    }

    interface CreatePaystationIntentCallback {
        fun onSuccess(intent: Intent)
        fun onFailure(errorMessage: String)
    }


    @JvmStatic
    fun getVirtualItems(callback: VirtualItemsCallback) = GlobalScope.launch {
        val virtualItems = Catalog.catalog.filter {
            it.type == "virtual_good"
        }.map {
            VirtualItem(
                    it.sku,
                    listOf(Price(it.price.currency, it.price.currency, it.price.amount, it.price.amount, false)),
                    null,
                    it.display_name,
                    it.description,
                    it.image_url
            )
        }
        withContext(Dispatchers.Main) {
            callback.onSuccess(virtualItems)
        }
    }

    data class VirtualItem(
            val sku: String,
            val realPrices: List<Price>?,
            val virtualPrices: List<Price>?,
            val name: String? = null,
            val description: String? = null,
            val imageUrl: String? = null
    )

    interface VirtualItemsCallback {
        fun onSuccess(virtualItems: List<VirtualItem>)
        fun onFailure(errorMessage: String)
    }


    @JvmStatic
    fun getInventory(callback: InventoryCallback) = GlobalScope.launch {
        val availableItems = Catalog.catalog.filter {
            it.type == "virtual_good"
        }
        val inventoryItems = DB.db.virtualItemDao().getAll()
        val result = inventoryItems.map { inventoryItem ->
            val catalogItem = availableItems.find { inventoryItem.sku == it.sku }!!
            InventoryItem(
                    inventoryItem.sku,
                    catalogItem.display_name,
                    catalogItem.description,
                    catalogItem.image_url,
                    inventoryItem.amount.toInt(),
                    false
            )
        }
        withContext(Dispatchers.Main) {
            callback.onSuccess(result)
        }
    }

    data class InventoryItem(
            val sku: String,
            val name: String? = null,
            val description: String? = null,
            val imageUrl: String? = null,
            val quantity: Int,
            val isConsumable: Boolean
    )

    interface InventoryCallback {
        fun onSuccess(inventoryItems: List<InventoryItem>)
        fun onFailure(errorMessage: String)
    }

}
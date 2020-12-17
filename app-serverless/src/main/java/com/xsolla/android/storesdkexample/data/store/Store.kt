package com.xsolla.android.storesdkexample.data.store

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.data.AccessData
import com.xsolla.android.payments.status.PaymentStatus
import com.xsolla.android.serverlessexample.BuildConfig
import com.xsolla.android.storesdkexample.data.db.DB
import com.xsolla.android.storesdkexample.data.db.VirtualCurrency
import com.xsolla.android.storesdkexample.data.db.VirtualItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

object Store {

    const val ACTION_VI_UPDATE = "ACTION_VI_UPDATE"
    const val ACTION_VC_UPDATE = "ACTION_VC_UPDATE"

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
        val externalId = XPayments.generateExternalId()
        val accessData = AccessData.Builder()
                .projectId(BuildConfig.PROJECT_ID)
                .userId(UUID.randomUUID().toString())
                .isSandbox(BuildConfig.IS_SANDBOX)
                .theme("dark")
                .externalId(externalId)
                .virtualItems(listOf(AccessData.VirtualItem(sku, 1)))
                .build()
        val intent = XPayments.createIntentBuilder(context)
                .accessData(accessData)
                .useWebview(true)
                .isSandbox(BuildConfig.IS_SANDBOX)
                .build()
        withContext(Dispatchers.Main) {
            callback.onSuccess(intent, externalId)
        }
    }

    interface CreatePaystationIntentCallback {
        fun onSuccess(intent: Intent, externalId: String)
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


    @JvmStatic
    fun addToInventory(context: Context, paymentStatus: PaymentStatus) {
        val sku = paymentStatus.purchase.virtual_items!![0].sku
        if (isItemSku(sku)) {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val viDao = DB.db.virtualItemDao()
                    val viList = viDao.getAll()
                    val item = viList.find { it.sku == sku }
                    if (item == null) {
                        viDao.insertItem(VirtualItem(0, sku, BigDecimal.ONE.toPlainString()))
                    } else {
                        val amount = BigDecimal(item.amount).plus(BigDecimal.ONE).toPlainString()
                        val newItem = VirtualItem(item.id, item.sku, amount)
                        viDao.updateItem(newItem)
                    }
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(Intent().apply {
                                action = ACTION_VI_UPDATE
                            })
                }
            }
        }
        if (isCurrencySku(sku)) {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    val vcPacks = HashMap<String, Pair<String, BigDecimal>>()
                    Catalog.catalog.filter {
                        it.type == "virtual_currency_package"
                    }.forEach {
                        vcPacks[it.sku] = Pair(it.bundle_content!!.currency, it.bundle_content.quantity)
                    }
                    val vcDao = DB.db.virtualCurrencyDao()
                    val vcList = vcDao.getAll()
                    val vcId = vcPacks[sku]!!.first
                    val vcAmount = vcPacks[sku]!!.second
                    val item = vcList.find { it.currency == vcId }
                    if (item == null) {
                        vcDao.insertCurrency(VirtualCurrency(0, vcId, vcAmount.toPlainString()))
                    } else {
                        val amount = BigDecimal(item.amount).plus(vcAmount)
                        val newItem = VirtualCurrency(item.id, item.currency, amount.toPlainString())
                        vcDao.updateCurrency(newItem)
                    }
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(Intent().apply {
                                action = ACTION_VC_UPDATE
                            })
                }
            }
        }
    }

    private fun isItemSku(sku: String) =
            Catalog.catalog.filter {
                it.type == "virtual_good"
            }.map {
                it.sku
            }.contains(sku)

    private fun isCurrencySku(sku: String) =
            Catalog.catalog.filter {
                it.type == "virtual_currency_package"
            }.map {
                it.sku
            }.contains(sku)

}
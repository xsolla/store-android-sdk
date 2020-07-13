package com.xsolla.android.storesdkexample.data.store

import com.playfab.PlayFabClientAPI
import com.playfab.PlayFabClientModels
import com.playfab.PlayFabSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

object Store {

    @JvmStatic
    fun init(playFabTitleId: String, sessionTicket: String) {
        PlayFabSettings.TitleId = playFabTitleId
        PlayFabSettings.ClientSessionTicket = sessionTicket
    }

    data class Price(
            val currencyId: String,
            val currencyName: String,
            val amount: BigDecimal,
            val amountWithoutDiscount: BigDecimal,
            val isVirtual: Boolean
    )


    @JvmStatic
    fun getVirtualBalance(callback: VirtualBalanceCallback) = GlobalScope.launch {
        val inventoryResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.GetUserInventoryRequest()
            PlayFabClientAPI.GetUserInventory(request)
        }
        val catalogResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.GetCatalogItemsRequest()
            PlayFabClientAPI.GetCatalogItems(request)
        }
        withContext(Dispatchers.Main) {
            if (inventoryResult.Error == null && catalogResult.Error == null) {
                val currencies = inventoryResult.Result.VirtualCurrency.entries.map {
                    var imageUrl: String? = null
                    for (item in catalogResult.Result.Catalog) {
                        if (item.ItemId.startsWith(it.key)) {
                            imageUrl = item.ItemImageUrl
                            break
                        }
                    }
                    VirtualBalance(it.key, it.value, imageUrl = imageUrl)
                }
                callback.onSuccess(currencies)
            } else {
                callback.onFailure(inventoryResult.Error?.errorMessage + '\n' + catalogResult.Error?.errorMessage)
            }
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
        val inventoryResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.GetUserInventoryRequest()
            PlayFabClientAPI.GetUserInventory(request)
        }
        val catalogResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.GetCatalogItemsRequest()
            PlayFabClientAPI.GetCatalogItems(request)
        }
        withContext(Dispatchers.Main) {
            if (inventoryResult.Error == null && catalogResult.Error == null) {
                val currencies = inventoryResult.Result.VirtualCurrency.entries.map {
                    it.key
                }
                val currencyPacks = catalogResult.Result.Catalog.filter { catalogItem ->
                    currencies.any {
                        catalogItem.ItemId.startsWith(it)
                    }
                }.map { pack ->
                    VirtualCurrencyPack(
                            pack.ItemId,
                            pack.VirtualCurrencyPrices?.filter {
                                it.key == "RM"
                            }?.map {
                                Price(it.key, it.key, BigDecimal.valueOf(it.value, 2), BigDecimal.valueOf(it.value, 2), false)
                            },
                            pack.VirtualCurrencyPrices?.filter {
                                it.key != "RM"
                            }?.map {
                                Price(it.key, it.key, BigDecimal.valueOf(it.value), BigDecimal.valueOf(it.value), true)
                            },
                            pack.DisplayName,
                            pack.Description,
                            pack.ItemImageUrl
                    )
                }
                callback.onSuccess(currencyPacks)
            } else {
                callback.onFailure(inventoryResult.Error?.errorMessage + '\n' + catalogResult.Error?.errorMessage)
            }
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
    fun buyForVirtualCurrency(itemSku: String, currencyId: String, price: BigDecimal, callback: BuyForVirtualCurrencyCallback) = GlobalScope.launch {
        val purchaseItemResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.PurchaseItemRequest()
            request.ItemId = itemSku
            request.Price = price.intValueExact()
            request.VirtualCurrency = currencyId
            PlayFabClientAPI.PurchaseItem(request)
        }
        withContext(Dispatchers.Main) {
            if (purchaseItemResult.Error == null) {
                callback.onSuccess()
            } else {
                callback.onFailure(purchaseItemResult.Error.errorMessage)
            }
        }
    }

    interface BuyForVirtualCurrencyCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }


    @JvmStatic
    fun getVirtualItems(callback: VirtualItemsCallback) = GlobalScope.launch {
        val inventoryResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.GetUserInventoryRequest()
            PlayFabClientAPI.GetUserInventory(request)
        }
        val catalogResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.GetCatalogItemsRequest()
            PlayFabClientAPI.GetCatalogItems(request)
        }
        withContext(Dispatchers.Main) {
            if (inventoryResult.Error == null && catalogResult.Error == null) {
                val currencies = inventoryResult.Result.VirtualCurrency.entries.map {
                    it.key
                }
                val virtualItems = catalogResult.Result.Catalog.filter { catalogItem ->
                    currencies.none {
                        catalogItem.ItemId.startsWith(it)
                    }
                }.map { item ->
                    VirtualItem(
                            item.ItemId,
                            item.VirtualCurrencyPrices?.filter {
                                it.key == "RM"
                            }?.map {
                                Price(it.key, it.key, BigDecimal.valueOf(it.value, 2), BigDecimal.valueOf(it.value, 2), false)
                            },
                            item.VirtualCurrencyPrices?.filter {
                                it.key != "RM"
                            }?.map {
                                Price(it.key, it.key, BigDecimal.valueOf(it.value), BigDecimal.valueOf(it.value), true)
                            },
                            item.DisplayName,
                            item.Description,
                            item.ItemImageUrl
                    )
                }
                callback.onSuccess(virtualItems)
            } else {
                callback.onFailure(inventoryResult.Error?.errorMessage + '\n' + catalogResult.Error?.errorMessage)
            }
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
        val inventoryResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.GetUserInventoryRequest()
            PlayFabClientAPI.GetUserInventory(request)
        }
        val catalogResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.GetCatalogItemsRequest()
            PlayFabClientAPI.GetCatalogItems(request)
        }
        withContext(Dispatchers.Main) {
            if (inventoryResult.Error == null && catalogResult.Error == null) {
                val inventoryItems = inventoryResult.Result.Inventory.map { inventoryItem ->
                    val catalogItem = catalogResult.Result.Catalog.find { it.ItemId == inventoryItem.ItemId }!!
                    InventoryItem(
                            inventoryItem.ItemId,
                            inventoryItem.ItemInstanceId,
                            inventoryItem.DisplayName,
                            catalogItem.Description,
                            catalogItem.ItemImageUrl,
                            inventoryItem.RemainingUses,
                            catalogItem.Consumable.UsageCount != null
                    )
                }
                callback.onSuccess(inventoryItems)
            } else {
                callback.onFailure(inventoryResult.Error?.errorMessage + '\n' + catalogResult.Error?.errorMessage)
            }
        }
    }

    data class InventoryItem(
            val sku: String,
            val instanceId: String,
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
    fun consumeItem(instanceId: String, quantity: Int, callback: ConsumeCallback) = GlobalScope.launch {
        val consumeResult = withContext(Dispatchers.IO) {
            val request = PlayFabClientModels.ConsumeItemRequest()
            request.ItemInstanceId = instanceId
            request.ConsumeCount = quantity
            PlayFabClientAPI.ConsumeItem(request)
        }
        withContext(Dispatchers.Main) {
            if (consumeResult.Error == null) {
                callback.onSuccess()
            } else {
                callback.onFailure(consumeResult.Error.errorMessage)
            }
        }
    }

    interface ConsumeCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }

}
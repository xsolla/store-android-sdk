package com.xsolla.android.customauth.ui.store

import android.os.Parcelable
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentViBinding
import com.xsolla.android.customauth.ui.BaseFragment
import com.xsolla.android.customauth.ui.adapter.ViPagerAdapter
import com.xsolla.android.customauth.viewmodels.VmInventory
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetSubscriptionsCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.GetVirtualItemsCallback
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.InventoryOption
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


class ViFragment : BaseFragment() {
    private val binding: FragmentViBinding by viewBinding()

    private val inventoryViewModel: VmInventory by activityViewModels()

    override fun getLayout() = R.layout.fragment_vi

    override fun initUI() {
        getInventory()
    }

    private fun getInventory() {
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(data: InventoryResponse) {
                val items = data.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                inventoryViewModel.inventory.value = items
                getVirtualItems(items)
                getSubscriptions()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    private fun getSubscriptions() {
        XInventory.getSubscriptions(object : GetSubscriptionsCallback {
            override fun onSuccess(data: SubscriptionsResponse) {
                inventoryViewModel.subscriptions.value = data.items
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    private fun getVirtualItems(inventory: List<InventoryResponse.Item>) {
        XStore.getVirtualItems(object : GetVirtualItemsCallback {
            override fun onSuccess(response: VirtualItemsResponse) {
                val items = response.items
                val groups = items
                        .flatMap { it.groups }
                        .map { it.name }
                        .distinct()
                        .toMutableList()

                val packOfItems = mutableListOf<List<VirtualItemUiEntity>>().apply {
                    add(items.toUiEntity(inventory))
                }

                groups.forEach { name ->
                    val filteredItems = items.filter { item ->
                        item.groups.map { group -> group.name }.contains(name)
                    }
                    packOfItems.add(filteredItems.toUiEntity(inventory))
                }

                groups.add(0, "ALL")
                binding.viewPager.adapter = ViPagerAdapter(this@ViFragment, packOfItems)

                TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                    tab.text = groups[position]
                }.attach()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    private fun List<VirtualItemsResponse.Item>.toUiEntity(inventory: List<InventoryResponse.Item>): List<VirtualItemUiEntity> {
        val result = mutableListOf<VirtualItemUiEntity>()
        val skuFromInventory = inventory.map { it.sku }
        this.forEach {
            val hasInInventory = it.sku in skuFromInventory
            result.add(
                VirtualItemUiEntity(
                    sku = it.sku,
                    name = it.name,
                    groups = it.groups,
                    attributes = it.attributes,
                    type = it.type,
                    description = it.description,
                    imageUrl = it.imageUrl,
                    isFree = it.isFree,
                    price = it.price,
                    virtualPrices = it.virtualPrices,
                    inventoryOption = it.inventoryOption,
                    hasInInventory = hasInInventory
                )
            )
        }
        return result
    }
}

@Parcelize
data class VirtualItemUiEntity(
    val sku: String? = null,
    val name: String? = null,
    val groups: List<Group> = emptyList(),
    val attributes: @RawValue List<Any> = emptyList(),
    val type: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val isFree: Boolean,
    val price: Price? = null,
    val virtualPrices: List<VirtualPrice> = emptyList(),
    val inventoryOption: InventoryOption? = null,
    val hasInInventory: Boolean
) : Parcelable
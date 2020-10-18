package com.xsolla.android.storesdkexample.ui.fragments.store

import android.os.Parcelable
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.InventoryOption
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice
import com.xsolla.android.store.entity.response.inventory.InventoryResponse
import com.xsolla.android.store.entity.response.inventory.SubscriptionsResponse
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.ViPagerAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmInventory
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlinx.android.synthetic.main.fragment_vi.tabLayout
import kotlinx.android.synthetic.main.fragment_vi.view.viewPager
import kotlinx.android.synthetic.main.fragment_vi.viewPager

class ViFragment : BaseFragment() {

    private val inventoryViewModel: VmInventory by activityViewModels()

    override fun getLayout() = R.layout.fragment_vi

    override fun initUI() {
        getInventory()
    }

    private fun getInventory() {
        XStore.getInventory(object : XStoreCallback<InventoryResponse>() {
            override fun onSuccess(response: InventoryResponse) {
                val items = response.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                inventoryViewModel.inventory.value = items
                getVirtualItems(items)
                getSubscriptions()
            }

            override fun onFailure(errorMessage: String) {
                showSnack(errorMessage)
            }
        })
    }

    private fun getSubscriptions() {
        XStore.getSubscriptions(object : XStoreCallback<SubscriptionsResponse>() {
            override fun onSuccess(response: SubscriptionsResponse) {
                inventoryViewModel.subscriptions.value = response.items
            }

            override fun onFailure(errorMessage: String) {
                showSnack(errorMessage)
            }
        })
    }

    private fun getVirtualItems(inventory: List<InventoryResponse.Item>) {
        XStore.getVirtualItems(object : XStoreCallback<VirtualItemsResponse>() {
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
                rootView.viewPager.adapter = ViPagerAdapter(this@ViFragment, packOfItems)

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = groups[position]
                }.attach()
            }

            override fun onFailure(errorMessage: String) {
                showSnack(errorMessage)
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
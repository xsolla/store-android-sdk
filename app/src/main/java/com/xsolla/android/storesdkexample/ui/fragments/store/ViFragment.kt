package com.xsolla.android.storesdkexample.ui.fragments.store

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.appcore.databinding.FragmentViBinding
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetTimeLimitedItemsCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.TimeLimitedItemsResponse
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.GetBundleListCallback
import com.xsolla.android.store.callbacks.GetVirtualItemsCallback
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.store.entity.response.bundle.BundleListResponse
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.InventoryOption
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.ViPagerAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmInventory
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


class ViFragment : BaseFragment() {
    private val binding: FragmentViBinding by viewBinding()

    private val inventoryViewModel: VmInventory by activityViewModels()

    override fun getLayout() = R.layout.fragment_vi

    override fun initUI() {

    }

    override fun activateUI() {
        getInventory()
    }

    private fun getInventory() {
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(data: InventoryResponse) {
                if (!isAdded) {
                    return
                }

                val items = data.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                inventoryViewModel.inventory.value = items
                getTimeLimitedItems()
                getBundles(items)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    private fun getTimeLimitedItems() {
        XInventory.getTimeLimitedItems(object : GetTimeLimitedItemsCallback {
            override fun onSuccess(data: TimeLimitedItemsResponse) {
                inventoryViewModel.timeLimitedItems.value = data.items
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }

        })
    }

    private fun getBundles(inventory: List<InventoryResponse.Item>) {
        XStore.getBundleList(object : GetBundleListCallback {
            override fun onSuccess(response: BundleListResponse) {
                val bundles = response.items.toUiEntity()
                getVirtualItems(inventory, bundles)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }

        })
    }

    private fun getVirtualItems(inventory: List<InventoryResponse.Item>, bundles: List<VirtualItemUiEntity>) {
        XStore.getVirtualItems(object : GetVirtualItemsCallback {
            override fun onSuccess(response: VirtualItemsResponse) {
                if (!isAdded) {
                    return
                }

                val items = response.items

                val bundleGroup = bundles.firstOrNull()?.groups?.firstOrNull()?.name

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

                if (!groups.contains(bundleGroup)) {
                    packOfItems.add(bundles)
                    groups.add(bundleGroup)
                } else if (groups.contains(bundleGroup)) {
                    val updatedItems = packOfItems[groups.indexOf(bundleGroup)].toMutableList()
                    updatedItems.addAll(bundles)
                    packOfItems[groups.indexOf(bundleGroup)] = updatedItems
                }
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

    private fun List<BundleItem>.toUiEntity(): List<VirtualItemUiEntity> {
        val result = mutableListOf<VirtualItemUiEntity>()
        this.forEach {
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
                    hasInInventory = false
                )
            )
        }
        return result
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
                    virtualItemType = it.virtualItemType,
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

@Keep
@Parcelize
data class VirtualItemUiEntity(
        val sku: String? = null,
        val name: String? = null,
        val groups: List<Group> = emptyList(),
        val attributes: @RawValue List<Any> = emptyList(),
        val type: String? = null,
        val virtualItemType: String? = null,
        val description: String? = null,
        val imageUrl: String? = null,
        val isFree: Boolean,
        val price: Price? = null,
        val virtualPrices: List<VirtualPrice> = emptyList(),
        val inventoryOption: InventoryOption? = null,

        val hasInInventory: Boolean
) : Parcelable
package com.xsolla.android.storesdkexample.ui.fragments.store

import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.VcPagerAdapter
import com.xsolla.android.storesdkexample.adapter.ViPagerAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_vi.*
import kotlinx.android.synthetic.main.fragment_vi.view.*

class VcFragment : BaseFragment() {

    override fun getLayout() = R.layout.fragment_vc

    override fun initUI() {
        getVirtualCurrency()
    }

    private fun getVirtualCurrency() {
        showOrHideToolbarViews(true)
        XStore.getVirtualCurrencyPackage(object : XStoreCallback<VirtualCurrencyPackageResponse>() {
            override fun onSuccess(response: VirtualCurrencyPackageResponse) {
                val items = response.items
                val groups = items
                        .flatMap { it.content }
                        .map { it.name }
                        .distinct()
                        .toMutableList()

                val packOfItems = mutableListOf<List<VirtualCurrencyPackageResponse.Item>>().apply {
                    add(items)
                }

                groups.forEach { name ->
                    val filteredItems = items.filter { item ->
                        item.content.map { content -> content.name }.contains(name)
                    }
                    packOfItems.add(filteredItems)
                }

                groups.add(0, "ALL")
                rootView.viewPager.adapter = VcPagerAdapter(this@VcFragment, packOfItems)

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = groups[position]
                }.attach()
            }

            override fun onFailure(errorMessage: String?) {

            }

        })
    }

    private fun getVirtualItems() {
        XStore.getVirtualItems(object : XStoreCallback<VirtualItemsResponse>() {
            override fun onSuccess(response: VirtualItemsResponse) {
                val items = response.items
                val groups = items
                        .flatMap { it.groups }
                        .map { it.name }
                        .distinct()
                        .toMutableList()


                val packOfItems = mutableListOf<List<VirtualItemsResponse.Item>>().apply {
                    add(items)
                }

                groups.forEach { name ->
                    val filteredItems = items.filter { item ->
                        item.groups.map { group -> group.name }.contains(name)
                    }
                    packOfItems.add(filteredItems)
                }

                groups.add(0, "ALL")
                rootView.viewPager.adapter = ViPagerAdapter(this@VcFragment, packOfItems)

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = groups[position]
                }.attach()

            }

            override fun onFailure(errorMessage: String?) {

            }
        })
    }

}
package com.xsolla.android.storesdkexample.ui.fragments.store

import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.VcPagerAdapter
import com.xsolla.android.appcore.databinding.FragmentVcBinding
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment

class VcFragment : BaseFragment() {
    private val binding: FragmentVcBinding by viewBinding()

    override fun getLayout() = R.layout.fragment_vc

    override fun initUI() {
        getVirtualCurrency()
    }

    private fun getVirtualCurrency() {
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
                binding.viewPager.adapter = VcPagerAdapter(this@VcFragment, packOfItems)

                TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                    tab.text = groups[position]
                }.attach()
            }

            override fun onFailure(errorMessage: String?) {

            }

        })
    }
}
package com.xsolla.android.customauth.ui.store

import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentVcBinding
import com.xsolla.android.customauth.ui.BaseFragment
import com.xsolla.android.customauth.ui.adapter.VcPagerAdapter
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.GetVirtualCurrencyPackageCallback
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse

class VcFragment : BaseFragment() {
    private val binding: FragmentVcBinding by viewBinding()

    override fun getLayout() = R.layout.fragment_vc

    override fun initUI() {
        getVirtualCurrency()
    }

    private fun getVirtualCurrency() {
        XStore.getVirtualCurrencyPackage(object : GetVirtualCurrencyPackageCallback {
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

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }

        })
    }
}
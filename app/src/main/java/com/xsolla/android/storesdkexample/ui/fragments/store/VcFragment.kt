package com.xsolla.android.storesdkexample.ui.fragments.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.VcPagerAdapter
import kotlinx.android.synthetic.main.fragment_vi.tabLayout
import kotlinx.android.synthetic.main.fragment_vi.view.viewPager
import kotlinx.android.synthetic.main.fragment_vi.viewPager

class VcFragment : Fragment() {

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_vc, container, false)
        getVirtualCurrency()
        return rootView
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
                rootView.viewPager.adapter = VcPagerAdapter(this@VcFragment, packOfItems)

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = groups[position]
                }.attach()
            }

            override fun onFailure(errorMessage: String?) {

            }

        })
    }

}
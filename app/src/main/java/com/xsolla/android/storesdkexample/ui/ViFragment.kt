package com.xsolla.android.storesdkexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.CatalogPagerAdapter
import kotlinx.android.synthetic.main.fragment_vi.*
import kotlinx.android.synthetic.main.fragment_vi.view.*

class ViFragment : Fragment() {

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_vi, container, false)
        getVirtualItems()
        return rootView
    }


    private fun getVirtualItems() {
        XStore.getVirtualItems(object : XStoreCallback<VirtualItemsResponse>() {
            override fun onSuccess(response: VirtualItemsResponse) {
                val items = response.items
                val groups = items
                        .flatMap { it.groups }
                        .map { it.name }
                        .distinct()

                val packOfItems = mutableListOf<List<VirtualItemsResponse.Item>>()

                groups.forEach { name ->
                    val filteredItems = items.filter { item ->
                        item.groups.map { group -> group.name }.contains(name)
                    }
                    packOfItems.add(filteredItems)
                }

                rootView.viewPager.adapter = CatalogPagerAdapter(this@ViFragment, packOfItems)

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = groups[position]
                }.attach()

            }

            override fun onFailure(errorMessage: String?) {

            }
        })
    }

}
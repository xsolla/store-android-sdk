package com.xsolla.android.storesdkexample.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.storesdkexample.ui.fragments.store.VcPageFragment

class VcPagerAdapter(
        fragment: Fragment,
        private val items: List<List<VirtualCurrencyPackageResponse.Item>>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return items.count()
    }

    override fun createFragment(position: Int): Fragment {
        val list = ArrayList(items[position])
        return VcPageFragment.getInstance(list)
    }
}
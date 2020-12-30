package com.xsolla.android.customauth.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xsolla.android.customauth.ui.store.VcPageFragment
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse

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
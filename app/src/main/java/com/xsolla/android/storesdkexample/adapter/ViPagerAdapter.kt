package com.xsolla.android.storesdkexample.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.ui.fragments.store.ViFragment
import com.xsolla.android.storesdkexample.ui.fragments.store.ViPageFragment
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity

class ViPagerAdapter(
    fragment: Fragment,
    private val items: List<List<VirtualItemUiEntity>>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return items.count()
    }

    override fun createFragment(position: Int): Fragment {
        val list = ArrayList(items[position])
        return ViPageFragment.getInstance(list)
    }
}
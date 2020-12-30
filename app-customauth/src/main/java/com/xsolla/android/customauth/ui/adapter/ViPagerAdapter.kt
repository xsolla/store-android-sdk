package com.xsolla.android.customauth.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xsolla.android.customauth.ui.store.ViPageFragment
import com.xsolla.android.customauth.ui.store.VirtualItemUiEntity

class ViPagerAdapter(
    fragment: Fragment,
    private val items: List<List<VirtualItemUiEntity>>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        val list = ArrayList(items[position])
        return ViPageFragment.getInstance(list)
    }
}
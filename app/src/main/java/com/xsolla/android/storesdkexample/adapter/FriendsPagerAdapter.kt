package com.xsolla.android.storesdkexample.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xsolla.android.storesdkexample.ui.fragments.friends.FriendsPageFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab

class FriendsPagerAdapter(fragment: Fragment, ) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = FriendsTab.values().size

    override fun createFragment(position: Int): Fragment {
        val tab = FriendsTab.getBy(position)
        return FriendsPageFragment.getInstance(tab)
    }
}
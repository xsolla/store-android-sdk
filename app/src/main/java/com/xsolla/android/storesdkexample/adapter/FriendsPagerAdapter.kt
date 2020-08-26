package com.xsolla.android.storesdkexample.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xsolla.android.storesdkexample.ui.fragments.friends.FriendsPageFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab

class FriendsPagerAdapter(
    fragment: Fragment,
    private val items: List<FriendUiEntity>
) : FragmentStateAdapter(fragment) {

    private companion object {
        private const val TABS_COUNT = 4
    }

    override fun getItemCount() = TABS_COUNT

    override fun createFragment(position: Int): Fragment {
        val tab = FriendsTab.getBy(position)
        val filteredItemsByRelationship = items.filter { it.relationship.tab == tab }
        return FriendsPageFragment.getInstance(filteredItemsByRelationship, tab)
    }
}
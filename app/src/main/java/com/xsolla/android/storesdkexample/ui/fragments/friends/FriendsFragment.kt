package com.xsolla.android.storesdkexample.ui.fragments.friends

import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsPagerAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends
import kotlinx.android.synthetic.main.fragment_friends.tabs
import kotlinx.android.synthetic.main.fragment_friends.viewPager

class FriendsFragment : BaseFragment() {
    private lateinit var pagerAdapter: FriendsPagerAdapter

    private val viewModel: VmFriends by activityViewModels()

    override fun getLayout() = R.layout.fragment_friends

    override fun initUI() {
        if (viewModel.getItems().isEmpty()) {
            viewModel.loadAllFriends()
        }

        pagerAdapter = FriendsPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = FriendsTab.getBy(position).title
        }.attach()

        viewModel.items.observe(viewLifecycleOwner) {
            countItemsByTabs()
        }
    }
    
    private fun countItemsByTabs() {
        val groupedItems = viewModel.getItemsCountByTab().values.toIntArray()
        for (i in 0 until tabs.tabCount) {
            val spannableString = buildSpannedString {
                append(FriendsTab.getBy(i).title)
                color(ContextCompat.getColor(requireContext(), R.color.secondary_color)) { append("   ${groupedItems[i]}") }
            }

            val tab = tabs.getTabAt(i)!!
            tab.text = spannableString
        }
    }
}
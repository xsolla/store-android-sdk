package com.xsolla.android.storesdkexample.ui.fragments.friends

import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsPagerAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends
import kotlinx.android.synthetic.main.fragment_friends.tabs
import kotlinx.android.synthetic.main.fragment_friends.view.tabs
import kotlinx.android.synthetic.main.fragment_friends.view.viewPager
import kotlinx.android.synthetic.main.fragment_friends.viewPager
import java.util.UUID

class FriendsFragment : BaseFragment() {
    private companion object {
        private val mockItems = listOf(
            FriendUiEntity(UUID.randomUUID().toString(), "", false, "nick", VmFriends.Relationship.STANDARD),
            FriendUiEntity(UUID.randomUUID().toString(), "", false, "nick", VmFriends.Relationship.STANDARD),
            FriendUiEntity(UUID.randomUUID().toString(), "", false, "nick", VmFriends.Relationship.STANDARD),
            FriendUiEntity(UUID.randomUUID().toString(), "", false, "nick", VmFriends.Relationship.STANDARD),
        )
    }

    private lateinit var pagerAdapter: FriendsPagerAdapter

    private val viewModel: VmFriends by activityViewModels()

    override fun getLayout() = R.layout.fragment_friends

    override fun initUI() {
        with(rootView) {
            viewModel.updateItems(mockItems)

            pagerAdapter = FriendsPagerAdapter(this@FriendsFragment, viewModel.getItems())
            this.viewPager.adapter = pagerAdapter

            TabLayoutMediator(this.tabs, this.viewPager) { tab, position ->
                tab.text = FriendsTab.getBy(position).title
            }.attach()
        }
    }
}
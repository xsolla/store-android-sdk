package com.xsolla.android.storesdkexample.ui.fragments.friends

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends
import kotlinx.android.synthetic.main.fragment_friends_page.friendsRecycler

class FriendsPageFragment : BaseFragment() {
    companion object {
        private const val EXTRA_ITEMS = "ExtraItems"
        private const val EXTRA_TAB = "ExtraTab"

        fun getInstance(items: List<FriendUiEntity>, tab: FriendsTab): FriendsPageFragment {
            return FriendsPageFragment().apply {
                arguments = bundleOf(EXTRA_ITEMS to items, EXTRA_TAB to tab.position)
            }
        }
    }

    private lateinit var adapter: FriendsAdapter

    private val viewModel: VmFriends by activityViewModels()

    override fun getLayout() = R.layout.fragment_friends_page

    override fun initUI() {
        val items = requireArguments().getParcelableArrayList<FriendUiEntity>(EXTRA_ITEMS)!!
        val tab = FriendsTab.getBy(requireArguments().getInt(EXTRA_TAB))

        adapter = FriendsAdapter(tab)
        friendsRecycler.adapter = adapter
        adapter.submitList(items)
    }
}
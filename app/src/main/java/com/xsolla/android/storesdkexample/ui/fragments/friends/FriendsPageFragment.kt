package com.xsolla.android.storesdkexample.ui.fragments.friends

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends
import kotlinx.android.synthetic.main.fragment_friends_page.friendsRecycler
import kotlinx.android.synthetic.main.fragment_friends_page.noItemsPlaceholder

class FriendsPageFragment : BaseFragment() {
    companion object {
        private const val EXTRA_TAB = "ExtraTab"

        fun getInstance(tab: FriendsTab): FriendsPageFragment {
            return FriendsPageFragment().apply {
                arguments = bundleOf(EXTRA_TAB to tab.position)
            }
        }
    }

    private lateinit var adapter: FriendsAdapter

    private val viewModel: VmFriends by activityViewModels()

    override fun getLayout() = R.layout.fragment_friends_page

    override fun initUI() {
        val tab = FriendsTab.getBy(requireArguments().getInt(EXTRA_TAB))
        noItemsPlaceholder.setText(tab.placeholderText)

        adapter = FriendsAdapter(
            currentTab = tab,
            onDeleteOptionClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.DeleteStrategy) },
            onBlockOptionClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.BlockStrategy) },
            onUnblockOptionClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.UnblockStrategy) },
            onAcceptButtonClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.AcceptStrategy) },
            onDeclineButtonClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.DeclineStrategy) },
            onCancelRequestButtonClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.CancelStrategy) },
            onAddFriendButtonClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.AddStrategy) }
        )
        friendsRecycler.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner) {
            val itemsForTab = viewModel.getItemsByTab(tab)
            setupPlaceholder(itemsForTab)
            adapter.updateList(itemsForTab)
        }

        viewModel.hasError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                showSnack("Failure")
            }
        }

        viewModel.isSearch.observe(viewLifecycleOwner) { isSearch ->
            if (!isSearch) {
                adapter.updateList(viewModel.getItemsByTab(tab))
            }
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            if (viewModel.isSearch.value == false) {
                return@observe
            }

            // Needs optimizations?
            val filteredItems = viewModel.getItemsByTab(viewModel.tab.value!!).filter { it.nickname.startsWith(query, ignoreCase = true) }
            adapter.updateList(filteredItems)
        }
    }

    private fun setupPlaceholder(itemsForTab: List<FriendUiEntity>) {
        if (itemsForTab.isEmpty()) {
            friendsRecycler.isVisible = false
            noItemsPlaceholder.isVisible = true
        } else {
            noItemsPlaceholder.isVisible = false
            friendsRecycler.isVisible = true
        }
    }
}
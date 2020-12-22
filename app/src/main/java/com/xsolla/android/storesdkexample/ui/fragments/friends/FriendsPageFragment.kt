package com.xsolla.android.storesdkexample.ui.fragments.friends

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsAdapter
import com.xsolla.android.storesdkexample.databinding.FragmentFriendsPageBinding
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends

class FriendsPageFragment : BaseFragment() {
    companion object {
        private const val EXTRA_TAB = "ExtraTab"

        fun getInstance(tab: FriendsTab): FriendsPageFragment {
            return FriendsPageFragment().apply {
                arguments = bundleOf(EXTRA_TAB to tab.position)
            }
        }
    }

    private val binding: FragmentFriendsPageBinding by viewBinding()

    private lateinit var adapter: FriendsAdapter

    private lateinit var tab: FriendsTab

    private val viewModel: VmFriends by activityViewModels()

    override fun getLayout() = R.layout.fragment_friends_page

    override val toolbarOption = ToolbarOptions(showBalance = false, showCart = false)

    override fun initUI() {
        tab = FriendsTab.getBy(requireArguments().getInt(EXTRA_TAB))
        binding.noItemsPlaceholder.setText(tab.placeholderText)

        binding.addFriendFlowButton.setOnClickListener {
            findNavController().navigate(R.id.fragment_add_friends)
        }

        adapter = FriendsAdapter(
            currentTab = tab,
            onDeleteOptionClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.DeleteStrategy) },
            onBlockOptionClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.BlockStrategy) },
            onUnblockOptionClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.UnblockStrategy) },
            onAcceptButtonClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.AcceptStrategy) },
            onDeclineButtonClick = { viewModel.updateFriend(it, VmFriends.UpdateFriendStrategy.DeclineStrategy) },
            onCancelRequestButtonClick = { item, from -> viewModel.updateFriend(item, VmFriends.UpdateFriendStrategy.CancelStrategy(from)) },
            onAddFriendButtonClick = { item, from -> viewModel.updateFriend(item, VmFriends.UpdateFriendStrategy.AddStrategy(from)) }
        )
        binding.friendsRecycler.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner) {
            val itemsForTab = viewModel.getItemsByTab(viewModel.tab.value!!)
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
                adapter.updateList(viewModel.getItemsByTab(viewModel.tab.value!!))
            }
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            if (viewModel.isSearch.value == false) {
                return@observe
            }

            val filteredItems = viewModel.getItemsByTab(viewModel.tab.value!!).filter { it.nickname.startsWith(query, ignoreCase = true) }
            adapter.updateList(filteredItems)
        }
    }

    private fun setupPlaceholder(itemsForTab: List<FriendUiEntity>) {
        if (itemsForTab.isEmpty()) {
            binding.friendsRecycler.isVisible = false
            binding.noItemsPlaceholder.isVisible = true
            binding.addFriendFlowButton.isVisible = (viewModel.tab.value == FriendsTab.FRIENDS)
        } else {
            binding.noItemsPlaceholder.isVisible = false
            binding.friendsRecycler.isVisible = true
            binding.addFriendFlowButton.isVisible = false
        }
    }
}
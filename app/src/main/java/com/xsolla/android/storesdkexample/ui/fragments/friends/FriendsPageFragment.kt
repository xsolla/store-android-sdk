package com.xsolla.android.storesdkexample.ui.fragments.friends

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
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

        adapter = FriendsAdapter(tab)
        friendsRecycler.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.getItemsByTab(tab))
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            if (viewState == VmFriends.ViewState.EMPTY) {
                friendsRecycler.isVisible = false
                noItemsPlaceholder.isVisible = true
            } else if (viewState == VmFriends.ViewState.SUCCESS) {
                noItemsPlaceholder.isVisible = false
                friendsRecycler.isVisible = true
            }
        }

    }
}
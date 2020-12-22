package com.xsolla.android.storesdkexample.ui.fragments.friends

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isEmpty
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsPagerAdapter
import com.xsolla.android.storesdkexample.databinding.FragmentFriendsBinding
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends

class FriendsFragment : BaseFragment() {
    private val binding: FragmentFriendsBinding by viewBinding()

    private lateinit var pagerAdapter: FriendsPagerAdapter

    private val viewModel: VmFriends by activityViewModels()

    override fun getLayout() = R.layout.fragment_friends

    override val toolbarOption: ToolbarOptions = ToolbarOptions(showBalance = false, showCart = false)

    override fun initUI() {
        viewModel.clearAllFriends()
        viewModel.loadAllFriends()

        pagerAdapter = FriendsPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = FriendsTab.getBy(position).title
        }.attach()

        viewModel.items.observe(viewLifecycleOwner) {
            countItemsByTabs()
        }
        viewModel.tab.observe(viewLifecycleOwner) {
            viewModel.clearTemporaryRelationship()
            binding.friendsToolbar.menu?.findItem(R.id.search)?.collapseActionView()
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.updateTab(FriendsTab.getBy(position))
            }
        })

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (binding.friendsToolbar.menu.isEmpty()) {
            inflater.inflate(R.menu.friends_menu, binding.friendsToolbar.menu)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val menuItem = binding.friendsToolbar.menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView

        if (viewModel.isSearch.value == true) {
            menuItem.expandActionView()
            searchView.setQuery(viewModel.searchQuery.value, false)
        }

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(false)
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                return true
            }
        })

        searchView.setOnCloseListener {
            viewModel.handleSearchMode(false)
            true
        }
    }
    
    private fun countItemsByTabs() {
        val groupedItems = viewModel.getItemsCountByTab().values.toIntArray()
        for (i in 0 until binding.tabs.tabCount) {
            val spannableString = buildSpannedString {
                append(FriendsTab.getBy(i).title)
                color(ContextCompat.getColor(requireContext(), R.color.light_state_gray_color)) { append("   ${groupedItems[i]}") }
            }

            val tab = binding.tabs.getTabAt(i)!!
            tab.text = spannableString
        }
    }
}
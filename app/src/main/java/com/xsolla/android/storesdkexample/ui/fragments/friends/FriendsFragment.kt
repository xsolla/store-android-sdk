package com.xsolla.android.storesdkexample.ui.fragments.friends

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsPagerAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends
import kotlinx.android.synthetic.main.fragment_friends.friendsToolbar
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
        viewModel.tab.observe(viewLifecycleOwner) {
            friendsToolbar.menu?.findItem(R.id.search)?.collapseActionView()
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.updateTab(FriendsTab.getBy(position))
            }
        })

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.friends_menu, friendsToolbar.menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val menuItem = friendsToolbar.menu.findItem(R.id.search)
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
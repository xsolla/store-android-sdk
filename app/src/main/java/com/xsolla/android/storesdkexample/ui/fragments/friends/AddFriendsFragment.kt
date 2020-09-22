package com.xsolla.android.storesdkexample.ui.fragments.friends

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequestAction
import com.xsolla.android.login.social.SocialNetworkForLinking
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.AddFriendsAdapter
import com.xsolla.android.storesdkexample.adapter.SocialFriendsAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsRelationship
import com.xsolla.android.storesdkexample.ui.vm.VmAddFriends
import com.xsolla.android.storesdkexample.ui.vm.VmSocialFriends
import com.xsolla.android.storesdkexample.util.setRateLimitedClickListener
import kotlinx.android.synthetic.main.fragment_add_friends.*

class AddFriendsFragment : BaseFragment() {

    companion object {
        const val RC_LINKING = 33
    }

    private lateinit var socialNetworksIcons: Map<SocialNetworkForLinking, Triple<Int, Int, ImageView>>

    private val vmAddFriends: VmAddFriends by viewModels()
    private val vmSocialFriends: VmSocialFriends by viewModels()

    private lateinit var searchAdapter: AddFriendsAdapter
    private lateinit var socialFriendsAdapter: SocialFriendsAdapter

    override fun getLayout() = R.layout.fragment_add_friends

    override fun initUI() {
        socialNetworksIcons = mapOf(
                SocialNetworkForLinking.FACEBOOK to Triple(R.drawable.ic_linking_facebook_add, R.drawable.ic_linking_facebook_added, iconFacebook),
                SocialNetworkForLinking.VK to Triple(R.drawable.ic_linking_vk_add, R.drawable.ic_linking_vk_added, iconVk),
                SocialNetworkForLinking.TWITTER to Triple(R.drawable.ic_linking_twitter_add, R.drawable.ic_linking_twitter_added, iconTwitter)
        )
        searchAdapter = AddFriendsAdapter(
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REMOVE) },
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.BLOCK) },
                {},
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REQUEST_APPROVE) },
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REQUEST_DENY) },
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REQUEST_CANCEL) },
                { vmAddFriends.updateFriendship(it, UpdateUserFriendsRequestAction.FRIEND_REQUEST_ADD) }
        )
        socialFriendsAdapter = SocialFriendsAdapter(
                {}, {}, {}, {}, {}, {}, {}
        )
        recycler.layoutManager = LinearLayoutManager(context)
        searchInput.addTextChangedListener {
            vmAddFriends.currentSearchQuery.value = it?.toString() ?: ""
        }
        vmSocialFriends.loadLinkedSocialAccounts()
        vmSocialFriends.loadAllSocialFriends()
        vmSocialFriends.linkedSocialNetworks.observe(viewLifecycleOwner) {
            initSocialButtons(it)
        }
        vmAddFriends.searchResultList.observe(viewLifecycleOwner) { list ->
            searchAdapter.submitList(list)
        }
        vmSocialFriends.socialFriendsList.observe(viewLifecycleOwner) { list ->
            socialFriendsAdapter.submitList(list.map { FriendUiEntity(it.socialNetworkUserId, it.avatar, false, it.name, FriendsRelationship.NONE) })
        }
        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                initRecentSearchScreen()
            } else {
                initSocialScreen()
            }
        }
        vmAddFriends.currentSearchQuery.observe(viewLifecycleOwner) { query ->
            query?.let {
                if (it.isNotEmpty()) {
                    initSearchScreen()
                } else {
                    if (searchInput.hasFocus()) {
                        initRecentSearchScreen()
                    } else {
                        initSocialScreen()
                    }
                }
            }
        }
        initSocialScreen()
        vmAddFriends.hasError.observe(viewLifecycleOwner) { hasError ->
            if (hasError) {
                showSnack("Failure")
            }
        }
        vmAddFriends.loadAllFriends()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_LINKING) {
            if (resultCode == Activity.RESULT_OK) {
                vmSocialFriends.loadLinkedSocialAccounts()
            } else {
                showSnack(getString(R.string.add_friends_linking_error))
            }
        }
    }

    private fun initSocialButtons(list: List<SocialNetworkForLinking?>) {
        for (socialNetwork in SocialNetworkForLinking.values()) {
            val info = socialNetworksIcons.getValue(socialNetwork)
            if (socialNetwork in list) {
                info.third.setRateLimitedClickListener { }
                info.third.setImageResource(info.second)
            } else {
                info.third.setRateLimitedClickListener {
                    startActivityForResult(
                            XLogin.createSocialAccountLinkingIntent(context, socialNetwork),
                            RC_LINKING
                    )
                }
                info.third.setImageResource(info.first)
            }
        }
    }

    private fun initSocialScreen() {
        labelSocialAccounts.text = getString(R.string.add_friends_social_accounts)
        labelSocialAccounts.visibility = View.VISIBLE
        socialButtonsScroll.visibility = View.VISIBLE
        labelListTitle.text = getString(R.string.add_friends_recommended)
        labelListTitle.visibility = View.VISIBLE
        recycler.adapter = socialFriendsAdapter
    }

    private fun initRecentSearchScreen() {
        labelSocialAccounts.text = getString(R.string.add_friends_recent_search)
        labelSocialAccounts.visibility = View.VISIBLE
        socialButtonsScroll.visibility = View.GONE
        labelListTitle.visibility = View.GONE
        recycler.adapter = searchAdapter
    }

    private fun initSearchScreen() {
        labelSocialAccounts.visibility = View.GONE
        socialButtonsScroll.visibility = View.GONE
        labelListTitle.visibility = View.GONE
        recycler.adapter = searchAdapter
    }

}
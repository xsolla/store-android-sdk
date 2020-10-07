package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xsolla.android.login.social.SocialNetworkForLinking
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.vm.VmSocialFriends
import kotlinx.android.synthetic.main.item_friend.view.*

class SocialFriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(
        item: VmSocialFriends.SocialFriendUiEntity,
        onAddFriendButtonClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit
    ) {

        itemView.friendNickname.text = item.nickname
        setupOnline(item)
        setupAvatar(item)

        itemView.addFriendButton.setOnClickListener {
            onAddFriendButtonClick(item)
        }

        configureViewVisibilityAndOptionsButton(item)
        configureFriendsInPlaceholder(item)
    }

    private fun setupAvatar(item: VmSocialFriends.SocialFriendUiEntity) {
        Glide.with(itemView)
            .load(item.imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_xsolla_logo)
            .error(R.drawable.ic_xsolla_logo)
            .into(itemView.friendAvatar)
    }

    private fun setupOnline(item: VmSocialFriends.SocialFriendUiEntity) {
        itemView.icOnline.isVisible = false
        itemView.icOffline.isGone = false
    }

    private fun configureViewVisibilityAndOptionsButton(item: VmSocialFriends.SocialFriendUiEntity) {
        if (item.xsollaId != null) {
            itemView.friendAcceptedText.isVisible = false
            itemView.friendDeclinedText.isVisible = false
            itemView.addFriendButton.isVisible = true
        }
        itemView.friendsOptionsButton.isVisible = false
    }

    private fun configureFriendsInPlaceholder(item: VmSocialFriends.SocialFriendUiEntity) {
        itemView.friendsInPlaceholder.isVisible = true

        val socialIcon = when (item.fromPlatform.first()) {
            SocialNetworkForLinking.FACEBOOK -> R.drawable.ic_facebook
            SocialNetworkForLinking.TWITTER -> R.drawable.ic_twitter
            SocialNetworkForLinking.VK -> R.drawable.ic_vk_small
        }
        itemView.friendsInPlaceholder.setCompoundDrawablesWithIntrinsicBounds(0, 0, socialIcon, 0)
    }
}
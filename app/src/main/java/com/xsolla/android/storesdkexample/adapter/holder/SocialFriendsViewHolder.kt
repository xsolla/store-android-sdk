package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

    private fun configureViewVisibilityAndOptionsButton(
            item: VmSocialFriends.SocialFriendUiEntity
    ) {
        if (item.xsollaId != null) {
            itemView.friendAcceptedText.isVisible = false
            itemView.friendDeclinedText.isVisible = false
            itemView.addFriendButton.isVisible = true
        }
        itemView.friendsOptionsButton.isVisible = false
    }
}
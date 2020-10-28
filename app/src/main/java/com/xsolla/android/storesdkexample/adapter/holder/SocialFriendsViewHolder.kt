package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xsolla.android.login.social.SocialNetworkForLinking
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.vm.FriendsRelationship
import com.xsolla.android.storesdkexample.ui.vm.VmSocialFriends
import kotlinx.android.synthetic.main.item_friend.view.*
import kotlinx.android.synthetic.main.layout_accept_decline_friends.view.*

class SocialFriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(
            item: VmSocialFriends.SocialFriendUiEntity,
            onDeleteOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit,
            onBlockOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit,
            onUnblockOptionsClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit,
            onAcceptButtonClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit,
            onDeclineButtonClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit,
            onCancelRequestButtonClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit,
            onAddFriendButtonClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit
    ) {

        itemView.friendNickname.text = item.nickname
        setupOnline(item)
        setupAvatar(item)

        itemView.friendsAcceptDeclineButtons.friendAcceptButton.setOnClickListener {
            onAcceptButtonClick(item)
        }
        itemView.friendsAcceptDeclineButtons.friendDeclineButton.setOnClickListener {
            onDeclineButtonClick(item)
        }
        itemView.unblockButton.setOnClickListener {
            onUnblockOptionsClick(item)
        }
        itemView.addFriendButton.setOnClickListener {
            onAddFriendButtonClick(item)
        }
        itemView.cancelRequestButton.setOnClickListener {
            onCancelRequestButtonClick(item)
        }

        configureViewVisibilityAndOptionsButton(item, onDeleteOptionClick, onBlockOptionClick)
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

    private fun configureOptionsWithBlock(item: VmSocialFriends.SocialFriendUiEntity, onBlockOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit) {
        itemView.friendsOptionsButton.setOnClickListener {
            AlertDialog.Builder(itemView.context)
                    .setTitle(itemView.context.getString(R.string.friends_options_dialog_title, item.nickname))
                    .setItems(arrayOf(itemView.context.getString(R.string.friends_option_block))) { _, _  ->
                        configureBlock(item, onBlockOptionClick)
                    }
                    .show()
        }
    }

    private fun configureDelete(item: VmSocialFriends.SocialFriendUiEntity, onDeleteOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit) {
        AlertDialog.Builder(itemView.context)
                .setTitle(itemView.context.getString(R.string.friends_option_delete_title, itemView.friendNickname.text))
                .setPositiveButton(R.string.friends_options_delete_button) { _, _ ->
                    onDeleteOptionClick(item)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun configureBlock(item: VmSocialFriends.SocialFriendUiEntity, onBlockOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit) {
        AlertDialog.Builder(itemView.context)
                .setTitle(itemView.context.getString(R.string.friends_option_block_title, itemView.friendNickname.text))
                .setPositiveButton(R.string.friends_options_block_button) { _, _ ->
                    onBlockOptionClick(item)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun configureViewVisibilityAndOptionsButton(
            item: VmSocialFriends.SocialFriendUiEntity,
            onDeleteOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit,
            onBlockOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit
    ) {
        itemView.friendsAcceptDeclineButtons.isVisible = false
        itemView.cancelRequestButton.isVisible = false
        itemView.addFriendButton.isVisible = false
        itemView.unblockButton.isVisible = false
        itemView.friendsOptionsButton.isVisible = false
        itemView.friendAcceptedText.isVisible = false
        itemView.friendDeclinedText.isVisible = false
        if (item.xsollaId == null) {
            return
        }
        if (item.relationship == FriendsRelationship.NONE) {
            itemView.addFriendButton.isVisible = true
            itemView.friendsOptionsButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.REQUESTED) {
            itemView.cancelRequestButton.isVisible = true
            itemView.friendsOptionsButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.PENDING) {
            itemView.friendsAcceptDeclineButtons.isVisible = true
            itemView.friendsOptionsButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.STANDARD) {
            itemView.friendsOptionsButton.isVisible = true
            itemView.friendsOptionsButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                        .setTitle(itemView.context.getString(R.string.friends_options_dialog_title, item.nickname))
                        .setItems(arrayOf(itemView.context.getString(R.string.friends_option_delete), itemView.context.getString(R.string.friends_option_block))) { _, which ->
                            if (which == 0) {
                                configureDelete(item, onDeleteOptionClick)
                            } else {
                                configureBlock(item, onBlockOptionClick)
                            }
                        }
                        .show()
            }
        }
    }

    private fun configureFriendsInPlaceholder(item: VmSocialFriends.SocialFriendUiEntity) {
        itemView.friendsInPlaceholder.isVisible = true

        itemView.friendsInIconFacebook.isVisible = item.fromPlatform.contains(SocialNetworkForLinking.FACEBOOK)
        itemView.friendsInIconTwitter.isVisible = item.fromPlatform.contains(SocialNetworkForLinking.TWITTER)
        itemView.friendsInIconVk.isVisible = item.fromPlatform.contains(SocialNetworkForLinking.VK)

    }
}
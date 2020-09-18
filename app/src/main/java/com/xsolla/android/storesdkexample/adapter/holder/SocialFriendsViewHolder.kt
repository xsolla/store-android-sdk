package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import kotlinx.android.synthetic.main.item_friend.view.*
import kotlinx.android.synthetic.main.layout_accept_decline_friends.view.*

class SocialFriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(
            item: FriendUiEntity,
            onDeleteOptionClick: (user: FriendUiEntity) -> Unit,
            onBlockOptionClick: (user: FriendUiEntity) -> Unit,
            onUnblockOptionsClick: (user: FriendUiEntity) -> Unit,
            onAcceptButtonClick: (user: FriendUiEntity) -> Unit,
            onDeclineButtonClick: (user: FriendUiEntity) -> Unit,
            onCancelRequestButtonClick: (user: FriendUiEntity) -> Unit,
            onAddFriendButtonClick: (user: FriendUiEntity) -> Unit
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
    }

    private fun setupAvatar(item: FriendUiEntity) {
        Glide.with(itemView)
                .load(item.imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_xsolla_logo)
                .error(R.drawable.ic_xsolla_logo)
                .into(itemView.friendAvatar)
    }

    private fun setupOnline(item: FriendUiEntity) {
        itemView.icOnline.isVisible = item.isOnline
        itemView.icOffline.isGone = item.isOnline
    }

    private fun configureOptionsWithBlock(item: FriendUiEntity, onBlockOptionClick: (user: FriendUiEntity) -> Unit) {
        itemView.friendsOptionsButton.setOnClickListener {
            AlertDialog.Builder(itemView.context)
                    .setTitle(itemView.context.getString(R.string.friends_options_dialog_title, item.nickname))
                    .setItems(arrayOf(itemView.context.getString(R.string.friends_option_block))) { _, _  ->
                        configureBlock(item, onBlockOptionClick)
                    }
                    .show()
        }
    }

    private fun configureDelete(item: FriendUiEntity, onDeleteOptionClick: (user: FriendUiEntity) -> Unit) {
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

    private fun configureBlock(item: FriendUiEntity, onBlockOptionClick: (user: FriendUiEntity) -> Unit) {
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
            item: FriendUiEntity,
            onDeleteOptionClick: (user: FriendUiEntity) -> Unit,
            onBlockOptionClick: (user: FriendUiEntity) -> Unit
    ) {
//        if (currentTab == FriendsTab.FRIENDS || item.temporaryRelationship in FriendsTab.FRIENDS.temporaryRelationships) {
//            itemView.friendAcceptedText.isVisible = false
//            itemView.friendDeclinedText.isVisible = false
//
//            itemView.friendsOptionsButton.setOnClickListener {
//                AlertDialog.Builder(itemView.context)
//                        .setTitle(itemView.context.getString(R.string.friends_options_dialog_title, item.nickname))
//                        .setItems(arrayOf(itemView.context.getString(R.string.friends_option_delete), itemView.context.getString(R.string.friends_option_block))) { _, which ->
//                            if (which == 0) {
//                                configureDelete(item, onDeleteOptionClick)
//                            }
//                            else {
//                                configureBlock(item, onBlockOptionClick)
//                            }
//                        }
//                        .show()
//            }
//        }
//        if (currentTab == FriendsTab.PENDING || item.temporaryRelationship in FriendsTab.PENDING.temporaryRelationships) {
//
//            when (item.temporaryRelationship) {
//                TemporaryFriendRelationship.REQUEST_ACCEPTED -> {
//                    itemView.friendAcceptedText.isVisible = true
//                    itemView.friendsAcceptDeclineButtons.isVisible = false
//                }
//                TemporaryFriendRelationship.REQUEST_DENIED -> {
//                    itemView.friendDeclinedText.isVisible = true
//                    itemView.friendsAcceptDeclineButtons.isVisible = false
//                }
//                else -> {
//                    itemView.friendsAcceptDeclineButtons.isVisible = true
//                    itemView.friendAcceptedText.isVisible = false
//                    itemView.friendDeclinedText.isVisible = false
//                }
//            }
//
//            configureOptionsWithBlock(item, onBlockOptionClick)
//        }
//        if (currentTab == FriendsTab.REQUESTED || item.temporaryRelationship in FriendsTab.REQUESTED.temporaryRelationships) {
//
//            itemView.cancelRequestButton.isGone = (item.temporaryRelationship == TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST)
//            itemView.addFriendButton.isVisible = (item.temporaryRelationship == TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST)
//
//            configureOptionsWithBlock(item, onBlockOptionClick)
//        }
//        if (currentTab == FriendsTab.BLOCKED || item.temporaryRelationship in FriendsTab.BLOCKED.temporaryRelationships) {
//
//            when (item.temporaryRelationship) {
//                null -> {
//                    itemView.friendsOptionsButton.isVisible = false
//                    itemView.unblockButton.isVisible = true
//                    itemView.addFriendButton.isVisible = false
//                    itemView.cancelRequestButton.isVisible = false
//                }
//                TemporaryFriendRelationship.UNBLOCKED -> {
//                    itemView.friendsOptionsButton.isVisible = true
//                    itemView.unblockButton.isVisible = false
//                    itemView.addFriendButton.isVisible = true
//                    itemView.cancelRequestButton.isVisible = false
//                }
//                TemporaryFriendRelationship.UNBLOCKED_AND_REQUEST_FRIEND -> {
//                    itemView.friendsOptionsButton.isVisible = true
//                    itemView.unblockButton.isVisible = false
//                    itemView.addFriendButton.isVisible = false
//                    itemView.cancelRequestButton.isVisible = true
//                }
//                else -> {
//                    throw IllegalArgumentException("${currentTab.name} available only this temporary relationships: ${currentTab.temporaryRelationships}")
//                }
//            }
//
//            configureOptionsWithBlock(item, onBlockOptionClick)
//        }
    }
}
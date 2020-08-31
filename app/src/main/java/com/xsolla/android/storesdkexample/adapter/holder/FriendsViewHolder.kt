package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsAdapter
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsRelationship
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.TemporaryFriendRelationship
import kotlinx.android.synthetic.main.item_friend.view.addFriendButton
import kotlinx.android.synthetic.main.item_friend.view.cancelRequestButton
import kotlinx.android.synthetic.main.item_friend.view.divider
import kotlinx.android.synthetic.main.item_friend.view.friendAcceptedText
import kotlinx.android.synthetic.main.item_friend.view.friendAvatar
import kotlinx.android.synthetic.main.item_friend.view.friendDeclinedText
import kotlinx.android.synthetic.main.item_friend.view.friendNickname
import kotlinx.android.synthetic.main.item_friend.view.friendsAcceptDeclineButtons
import kotlinx.android.synthetic.main.item_friend.view.friendsOptionsButton
import kotlinx.android.synthetic.main.item_friend.view.icOffline
import kotlinx.android.synthetic.main.item_friend.view.icOnline
import kotlinx.android.synthetic.main.item_friend.view.unblockButton
import kotlinx.android.synthetic.main.layout_accept_decline_friends.view.friendAcceptButton
import kotlinx.android.synthetic.main.layout_accept_decline_friends.view.friendDeclineButton

class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // TODO: Refactoring
    fun bind(
        item: FriendUiEntity,
        itemsCount: Int,
        currentTab: FriendsTab,
        onDeleteOptionClick: (user: FriendUiEntity) -> Unit,
        onBlockOptionClick: (user: FriendUiEntity) -> Unit,
        onUnblockOptionsClick: (user: FriendUiEntity) -> Unit,
        onAcceptButtonClick: (user: FriendUiEntity) -> Unit,
        onDeclineButtonClick: (user: FriendUiEntity) -> Unit,
        onCancelRequestButtonClick: (user: FriendUiEntity, from: FriendsTab) -> Unit,
        onAddFriendButtonClick: (user: FriendUiEntity, from: FriendsTab) -> Unit
    ) {
        if (itemViewType == FriendsAdapter.ViewType.ADD_FRIEND_BUTTON.value) {
            itemView.setOnClickListener {  } // TODO: go to add friend flow
            return
        }

        itemView.divider.isGone = (adapterPosition == itemsCount - 1)

        Glide.with(itemView)
            .load(item.imageUrl)
            .apply(circleCropTransform())
            .placeholder(R.drawable.ic_xsolla_logo)
            .error(R.drawable.ic_xsolla_logo)
            .into(itemView.friendAvatar)

        itemView.friendNickname.text = item.nickname

        itemView.icOnline.isVisible = item.isOnline
        itemView.icOffline.isGone = item.isOnline

        if (item.relationship == FriendsRelationship.STANDARD) {
            itemView.friendsOptionsButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("${itemView.friendNickname.text} options")
                    .setItems(arrayOf("Delete friend", "Block user")) { _, which ->
                        if (which == 0) {
                            configureDelete(item, onDeleteOptionClick)
                        }
                        else {
                            configureBlock(item, onBlockOptionClick)
                        }
                    }
                    .show()
            }
        }
        if (item.relationship == FriendsRelationship.PENDING || item.temporaryRelationship == TemporaryFriendRelationship.REQUEST_ACCEPTED || item.temporaryRelationship == TemporaryFriendRelationship.REQUEST_DENIED) {

            when (item.temporaryRelationship) {
                TemporaryFriendRelationship.REQUEST_ACCEPTED -> {
                    itemView.friendAcceptedText.isVisible = true
                    itemView.friendsAcceptDeclineButtons.isVisible = false
                }
                TemporaryFriendRelationship.REQUEST_DENIED -> {
                    itemView.friendDeclinedText.isVisible = true
                    itemView.friendsAcceptDeclineButtons.isVisible = false
                }
                else -> {
                    itemView.friendsAcceptDeclineButtons.isVisible = true
                    itemView.friendAcceptedText.isVisible = false
                    itemView.friendDeclinedText.isVisible = false
                }
            }

            itemView.friendsAcceptDeclineButtons.friendAcceptButton.setOnClickListener {
                onAcceptButtonClick(item)
            }
            itemView.friendsAcceptDeclineButtons.friendDeclineButton.setOnClickListener {
                onDeclineButtonClick(item)
            }

            itemView.friendsOptionsButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("${item.nickname} options")
                    .setItems(arrayOf("Block user")) { _, _ ->
                        configureBlock(item, onBlockOptionClick)
                    }
                    .show()
            }
        }
        if (item.relationship == FriendsRelationship.REQUESTED || item.temporaryRelationship == TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST) {

            itemView.cancelRequestButton.isGone = (item.temporaryRelationship == TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST)
            itemView.addFriendButton.isVisible = (item.temporaryRelationship == TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST)

            itemView.cancelRequestButton.setOnClickListener {
                onCancelRequestButtonClick(item, currentTab)
            }
            itemView.addFriendButton.setOnClickListener {
                onAddFriendButtonClick(item, currentTab)
            }

            itemView.friendsOptionsButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("${item.nickname} options")
                    .setItems(arrayOf("Block user")) { _, _ ->
                        configureBlock(item, onBlockOptionClick)
                    }
                    .show()
            }
        }
        if (item.relationship == FriendsRelationship.BLOCKED || item.temporaryRelationship == TemporaryFriendRelationship.UNBLOCKED || item.temporaryRelationship == TemporaryFriendRelationship.UNBLOCKED_AND_REQUEST_FRIEND) {

            when (item.temporaryRelationship) {
                null -> {
                    itemView.friendsOptionsButton.isVisible = false
                    itemView.unblockButton.isVisible = true
                    itemView.addFriendButton.isVisible = false
                    itemView.cancelRequestButton.isVisible = false

                    itemView.unblockButton.setOnClickListener {
                        onUnblockOptionsClick(item)
                    }
                }
                TemporaryFriendRelationship.UNBLOCKED -> {
                    itemView.friendsOptionsButton.isVisible = true
                    itemView.unblockButton.isVisible = false
                    itemView.addFriendButton.isVisible = true
                    itemView.cancelRequestButton.isVisible = false

                    itemView.addFriendButton.setOnClickListener {
                        onAddFriendButtonClick(item, currentTab)
                    }
                }
                TemporaryFriendRelationship.UNBLOCKED_AND_REQUEST_FRIEND -> {
                    itemView.friendsOptionsButton.isVisible = true
                    itemView.unblockButton.isVisible = false
                    itemView.addFriendButton.isVisible = false
                    itemView.cancelRequestButton.isVisible = true

                    itemView.cancelRequestButton.setOnClickListener {
                        onCancelRequestButtonClick(item, currentTab)
                    }
                }
                else -> {
                    throw IllegalArgumentException("${currentTab.name} available only this temporary relationships: ${currentTab.temporaryRelationships}")
                }
            }

            if (itemView.friendsOptionsButton.isVisible) {
                itemView.friendsOptionsButton.setOnClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("${item.nickname} options")
                        .setItems(arrayOf("Block user")) { _, _  ->
                            configureBlock(item, onBlockOptionClick)
                        }
                        .show()
                }
            }
        }
    }

    private fun configureDelete(item: FriendUiEntity, onDeleteOptionClick: (user: FriendUiEntity) -> Unit) {
        AlertDialog.Builder(itemView.context)
            .setTitle("Remove ${itemView.friendNickname.text} from the friends list?")
            .setPositiveButton("Remove") { _, _ ->
                onDeleteOptionClick(item)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun configureBlock(item: FriendUiEntity, onBlockOptionClick: (user: FriendUiEntity) -> Unit) {
        AlertDialog.Builder(itemView.context)
            .setTitle("Block ${itemView.friendNickname.text}?")
            .setPositiveButton("Block") { _, _ ->
                onBlockOptionClick(item)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
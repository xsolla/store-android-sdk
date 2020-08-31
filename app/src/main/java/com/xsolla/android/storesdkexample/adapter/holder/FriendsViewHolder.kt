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
import com.xsolla.android.storesdkexample.ui.vm.TemporaryFriendRelationship
import kotlinx.android.synthetic.main.item_friend.view.divider
import kotlinx.android.synthetic.main.item_friend.view.friendAcceptedText
import kotlinx.android.synthetic.main.item_friend.view.friendAvatar
import kotlinx.android.synthetic.main.item_friend.view.friendDeclinedText
import kotlinx.android.synthetic.main.item_friend.view.friendNickname
import kotlinx.android.synthetic.main.item_friend.view.friendsAcceptDeclineButtons
import kotlinx.android.synthetic.main.item_friend.view.friendsOptionsButton
import kotlinx.android.synthetic.main.item_friend.view.icOffline
import kotlinx.android.synthetic.main.item_friend.view.icOnline
import kotlinx.android.synthetic.main.layout_accept_decline_friends.view.friendAcceptButton
import kotlinx.android.synthetic.main.layout_accept_decline_friends.view.friendDeclineButton

class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // TODO: Refactoring
    fun bind(
        item: FriendUiEntity,
        itemsCount: Int,
        onDeleteOptionClick: (user: FriendUiEntity) -> Unit,
        onBlockOptionClick: (user: FriendUiEntity) -> Unit,
        onUnblockOptionsClick: (user: FriendUiEntity) -> Unit,
        onAcceptButtonClick: (user: FriendUiEntity) -> Unit,
        onDeclineButtonClick: (user: FriendUiEntity) -> Unit
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
        if (item.relationship == FriendsRelationship.PENDING || item.temporaryRelationship == TemporaryFriendRelationship.REQUEST_ACCEPTED || item.temporaryRelationship == TemporaryFriendRelationship.REQUEST_CANCELED) {

            when (item.temporaryRelationship) {
                TemporaryFriendRelationship.REQUEST_ACCEPTED -> {
                    itemView.friendAcceptedText.isVisible = true
                    itemView.friendsAcceptDeclineButtons.isVisible = false
                }
                TemporaryFriendRelationship.REQUEST_CANCELED -> {
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
        if (item.relationship == FriendsRelationship.REQUESTED) {
            itemView.friendsOptionsButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("${item.nickname} options")
                    .setItems(arrayOf("Block user")) { _, _ ->
                        configureBlock(item, onBlockOptionClick)
                    }
                    .show()
            }
        }
        if (item.relationship == FriendsRelationship.BLOCKED) {
            itemView.friendsOptionsButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("${item.nickname} options")
                    .setItems(arrayOf("Block user")) { _, _ ->
                        configureBlock(item, onBlockOptionClick)
                    }
                    .show()
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

    private fun configureUnblock(item: FriendUiEntity, onUnblockOptionsClick: (user: FriendUiEntity) -> Unit) {
        itemView.friendsOptionsButton.setOnClickListener {
            AlertDialog.Builder(itemView.context)
                .setTitle("${itemView.friendNickname.text} options")
                .setItems(arrayOf("Unblock user")) { _, _ ->
                    AlertDialog.Builder(itemView.context)
                        .setTitle("Unblock ${item.nickname}?")
                        .setPositiveButton("Unblock") { _, _ ->
                            onUnblockOptionsClick(item)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                .show()
        }
    }
}
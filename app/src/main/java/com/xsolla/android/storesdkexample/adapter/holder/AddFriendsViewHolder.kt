package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.databinding.ItemFriendBinding
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsRelationship

class AddFriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemFriendBinding.bind(view)

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

        binding.friendNickname.text = item.nickname
        setupOnline(item)
        setupAvatar(item)

        binding.friendsAcceptDeclineButtons.friendAcceptButton.setOnClickListener {
            onAcceptButtonClick(item)
        }
        binding.friendsAcceptDeclineButtons.friendDeclineButton.setOnClickListener {
            onDeclineButtonClick(item)
        }
        binding.unblockButton.setOnClickListener {
            onUnblockOptionsClick(item)
        }
        binding.addFriendButton.setOnClickListener {
            onAddFriendButtonClick(item)
        }
        binding.cancelRequestButton.setOnClickListener {
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
                .into(binding.friendAvatar)
    }

    private fun setupOnline(item: FriendUiEntity) {
        binding.icOnline.isVisible = item.isOnline
        binding.icOffline.isGone = item.isOnline
    }

    private fun configureOptionsWithBlock(item: FriendUiEntity, onBlockOptionClick: (user: FriendUiEntity) -> Unit) {
        binding.friendsOptionsButton.setOnClickListener {
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
                .setTitle(itemView.context.getString(R.string.friends_option_delete_title, binding.friendNickname.text))
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
                .setTitle(itemView.context.getString(R.string.friends_option_block_title, binding.friendNickname.text))
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

        if (item.relationship == FriendsRelationship.NONE) {
            binding.friendAcceptedText.isVisible = false
            binding.friendDeclinedText.isVisible = false
            binding.addFriendButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.REQUESTED) {
            binding.friendAcceptedText.isVisible = false
            binding.friendDeclinedText.isVisible = false
            binding.cancelRequestButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.PENDING) {
            binding.friendAcceptedText.isVisible = false
            binding.friendDeclinedText.isVisible = false
            binding.friendsAcceptDeclineButtons.friendAcceptButton.isVisible = true
            binding.friendsAcceptDeclineButtons.friendDeclineButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.STANDARD) {
            binding.friendAcceptedText.isVisible = false
            binding.friendDeclinedText.isVisible = false
            binding.friendsOptionsButton.setOnClickListener {
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
}
package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.FriendsAdapter
import com.xsolla.android.appcore.databinding.ItemAddFriendButtonBinding
import com.xsolla.android.appcore.databinding.ItemFriendBinding
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.TemporaryFriendRelationship

class FriendsViewHolder(view: View, viewType: FriendsAdapter.ViewType) : RecyclerView.ViewHolder(view) {
    private val binding: ViewBinding = if (viewType == FriendsAdapter.ViewType.ITEM) {
        ItemFriendBinding.bind(view)
    } else {
        ItemAddFriendButtonBinding.bind(view)
    }

    fun bind(
        item: FriendUiEntity,
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
            binding as ItemAddFriendButtonBinding
            binding.addFriendItem.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.fragment_add_friends))
            return
        }
        binding as ItemFriendBinding

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
            onAddFriendButtonClick(item, currentTab)
        }
        binding.cancelRequestButton.setOnClickListener {
            onCancelRequestButtonClick(item, currentTab)
        }

        configureViewVisibilityAndOptionsButton(currentTab, item, onDeleteOptionClick, onBlockOptionClick)
    }

    private fun setupAvatar(item: FriendUiEntity) {
        binding as ItemFriendBinding

        Glide.with(itemView)
            .load(item.imageUrl)
            .apply(circleCropTransform())
            .placeholder(R.drawable.ic_xsolla_logo)
            .error(R.drawable.ic_xsolla_logo)
            .into(binding.friendAvatar)
    }

    private fun setupOnline(item: FriendUiEntity) {
        binding as ItemFriendBinding

        binding.icOnline.isVisible = item.isOnline
        binding.icOffline.isGone = item.isOnline
    }

    private fun configureOptionsWithBlock(item: FriendUiEntity, onBlockOptionClick: (user: FriendUiEntity) -> Unit) {
        binding as ItemFriendBinding

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
        binding as ItemFriendBinding

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
        binding as ItemFriendBinding

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
        currentTab: FriendsTab,
        item: FriendUiEntity,
        onDeleteOptionClick: (user: FriendUiEntity) -> Unit,
        onBlockOptionClick: (user: FriendUiEntity) -> Unit
    ) {
        binding as ItemFriendBinding

        if (currentTab == FriendsTab.FRIENDS || item.temporaryRelationship in FriendsTab.FRIENDS.temporaryRelationships) {
            binding.friendAcceptedText.isVisible = false
            binding.friendDeclinedText.isVisible = false

            binding.friendsOptionsButton.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle(itemView.context.getString(R.string.friends_options_dialog_title, item.nickname))
                    .setItems(arrayOf(itemView.context.getString(R.string.friends_option_delete), itemView.context.getString(R.string.friends_option_block))) { _, which ->
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
        if (currentTab == FriendsTab.PENDING || item.temporaryRelationship in FriendsTab.PENDING.temporaryRelationships) {

            when (item.temporaryRelationship) {
                TemporaryFriendRelationship.REQUEST_ACCEPTED -> {
                    binding.friendAcceptedText.isVisible = true
                    binding.friendsAcceptDeclineButtons.root.isVisible = false
                }
                TemporaryFriendRelationship.REQUEST_DENIED -> {
                    binding.friendDeclinedText.isVisible = true
                    binding.friendsAcceptDeclineButtons.root.isVisible = false
                }
                else -> {
                    binding.friendsAcceptDeclineButtons.root.isVisible = true
                    binding.friendAcceptedText.isVisible = false
                    binding.friendDeclinedText.isVisible = false
                }
            }

            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (currentTab == FriendsTab.REQUESTED || item.temporaryRelationship in FriendsTab.REQUESTED.temporaryRelationships) {

            binding.cancelRequestButton.isGone = (item.temporaryRelationship == TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST)
            binding.addFriendButton.isVisible = (item.temporaryRelationship == TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST)

            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (currentTab == FriendsTab.BLOCKED || item.temporaryRelationship in FriendsTab.BLOCKED.temporaryRelationships) {

            when (item.temporaryRelationship) {
                null -> {
                    binding.friendsOptionsButton.isVisible = false
                    binding.unblockButton.isVisible = true
                    binding.addFriendButton.isVisible = false
                    binding.cancelRequestButton.isVisible = false
                }
                TemporaryFriendRelationship.UNBLOCKED -> {
                    binding.friendsOptionsButton.isVisible = true
                    binding.unblockButton.isVisible = false
                    binding.addFriendButton.isVisible = true
                    binding.cancelRequestButton.isVisible = false
                }
                TemporaryFriendRelationship.UNBLOCKED_AND_REQUEST_FRIEND -> {
                    binding.friendsOptionsButton.isVisible = true
                    binding.unblockButton.isVisible = false
                    binding.addFriendButton.isVisible = false
                    binding.cancelRequestButton.isVisible = true
                }
                else -> {
                    throw IllegalArgumentException("${currentTab.name} available only this temporary relationships: ${currentTab.temporaryRelationships}")
                }
            }

            configureOptionsWithBlock(item, onBlockOptionClick)
        }
    }
}
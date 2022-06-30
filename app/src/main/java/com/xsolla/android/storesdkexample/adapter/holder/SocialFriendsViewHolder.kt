package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.appcore.databinding.ItemFriendBinding
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.storesdkexample.ui.vm.FriendsRelationship
import com.xsolla.android.storesdkexample.ui.vm.VmSocialFriends

class SocialFriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemFriendBinding.bind(view)

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
        configureFriendsInPlaceholder(item)
    }

    private fun setupAvatar(item: VmSocialFriends.SocialFriendUiEntity) {
        Glide.with(itemView)
            .load(item.imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_xsolla_logo)
            .error(R.drawable.ic_xsolla_logo)
            .into(binding.friendAvatar)
    }

    private fun setupOnline(item: VmSocialFriends.SocialFriendUiEntity) {
        binding.icOnline.isVisible = false
        binding.icOffline.isGone = false
    }

    private fun configureOptionsWithBlock(item: VmSocialFriends.SocialFriendUiEntity, onBlockOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit) {
        binding.friendsOptionsButton.setOnClickListener {
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
                .setTitle(itemView.context.getString(R.string.friends_option_delete_title, binding.friendNickname.text))
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
            item: VmSocialFriends.SocialFriendUiEntity,
            onDeleteOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit,
            onBlockOptionClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit
    ) {
        binding.friendsAcceptDeclineButtons.root.isVisible = false
        binding.cancelRequestButton.isVisible = false
        binding.addFriendButton.isVisible = false
        binding.unblockButton.isVisible = false
        binding.friendsOptionsButton.isVisible = false
        binding.friendAcceptedText.isVisible = false
        binding.friendDeclinedText.isVisible = false
        if (item.xsollaId == null) {
            return
        }
        if (item.relationship == FriendsRelationship.NONE) {
            binding.addFriendButton.isVisible = true
            binding.friendsOptionsButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.REQUESTED) {
            binding.cancelRequestButton.isVisible = true
            binding.friendsOptionsButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.PENDING) {
            binding.friendsAcceptDeclineButtons.root.isVisible = true
            binding.friendsOptionsButton.isVisible = true
            configureOptionsWithBlock(item, onBlockOptionClick)
        }
        if (item.relationship == FriendsRelationship.STANDARD) {
            binding.friendsOptionsButton.isVisible = true
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

    private fun configureFriendsInPlaceholder(item: VmSocialFriends.SocialFriendUiEntity) {
        binding.friendsInPlaceholder.isVisible = true

        binding.friendsInIconFacebook.isVisible = item.fromPlatform.contains(SocialNetwork.FACEBOOK)
        binding.friendsInIconTwitter.isVisible = item.fromPlatform.contains(SocialNetwork.TWITTER)
        binding.friendsInIconVk.isVisible = item.fromPlatform.contains(SocialNetwork.VK)

    }
}
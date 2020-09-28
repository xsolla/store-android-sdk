package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.holder.SocialFriendsViewHolder
import com.xsolla.android.storesdkexample.ui.vm.VmSocialFriends

class SocialFriendsAdapter(
        private val onAddFriendButtonClick: (user: VmSocialFriends.SocialFriendUiEntity) -> Unit
) : ListAdapter<VmSocialFriends.SocialFriendUiEntity, SocialFriendsViewHolder>(SocialFriendsDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialFriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return SocialFriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SocialFriendsViewHolder, position: Int) {
        holder.bind(getItem(position), onAddFriendButtonClick)
    }

}

class SocialFriendsDiffUtilCallback : DiffUtil.ItemCallback<VmSocialFriends.SocialFriendUiEntity>() {
    override fun areItemsTheSame(oldItem: VmSocialFriends.SocialFriendUiEntity, newItem: VmSocialFriends.SocialFriendUiEntity): Boolean {
        return oldItem.xsollaId == newItem.xsollaId && oldItem.socialId == newItem.socialId
    }

    override fun areContentsTheSame(oldItem: VmSocialFriends.SocialFriendUiEntity, newItem: VmSocialFriends.SocialFriendUiEntity): Boolean {
        return oldItem == newItem
    }
}
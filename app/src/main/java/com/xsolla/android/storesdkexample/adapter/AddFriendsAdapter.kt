package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.holder.AddFriendsViewHolder
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity

class AddFriendsAdapter(
        private val onDeleteOptionClick: (user: FriendUiEntity) -> Unit,
        private val onBlockOptionClick: (user: FriendUiEntity) -> Unit,
        private val onUnblockOptionClick: (user: FriendUiEntity) -> Unit,
        private val onAcceptButtonClick: (user: FriendUiEntity) -> Unit,
        private val onDeclineButtonClick: (user: FriendUiEntity) -> Unit,
        private val onCancelRequestButtonClick: (user: FriendUiEntity) -> Unit,
        private val onAddFriendButtonClick: (user: FriendUiEntity) -> Unit
) : ListAdapter<FriendUiEntity, AddFriendsViewHolder>(FriendsDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return AddFriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddFriendsViewHolder, position: Int) {
        holder.bind(getItem(position), onDeleteOptionClick, onBlockOptionClick, onUnblockOptionClick, onAcceptButtonClick, onDeclineButtonClick, onCancelRequestButtonClick, onAddFriendButtonClick)
    }

}
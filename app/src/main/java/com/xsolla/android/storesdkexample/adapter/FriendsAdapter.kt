package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.holder.FriendsViewHolder
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsRelationship
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab

class FriendsAdapter(
    private val currentTab: FriendsTab,
    private val onDeleteOptionClick: (user: FriendUiEntity) -> Unit,
    private val onBlockOptionClick: (user: FriendUiEntity) -> Unit,
    private val onUnblockOptionClick: (user: FriendUiEntity) -> Unit,
    private val onAcceptButtonClick: (user: FriendUiEntity) -> Unit,
    private val onDeclineButtonClick: (user: FriendUiEntity) -> Unit,
    private val onCancelRequestButtonClick: (user: FriendUiEntity) -> Unit,
    private val onAddFriendButtonClick: (user: FriendUiEntity) -> Unit
) : ListAdapter<FriendUiEntity, FriendsViewHolder>(FriendsDiffUtilCallback()) {
    private companion object {
        private const val ADD_BUTTON_ID = "AddButtonId"
    }

    override fun getItemViewType(position: Int): Int {
        if (currentTab != FriendsTab.FRIENDS) {
            return ViewType.ITEM.value
        }
        return if (getItem(position).id == ADD_BUTTON_ID) {
            ViewType.ADD_FRIEND_BUTTON.value
        } else {
            ViewType.ITEM.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = if (ViewType.getBy(viewType) == ViewType.ADD_FRIEND_BUTTON) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_friend_button, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        }
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        holder.bind(getItem(position), itemCount, onDeleteOptionClick, onBlockOptionClick, onUnblockOptionClick, onAcceptButtonClick, onDeclineButtonClick, onCancelRequestButtonClick, onAddFriendButtonClick)
    }

    enum class ViewType(val value: Int) {
        ADD_FRIEND_BUTTON(1),
        ITEM(2);

        companion object {
            fun getBy(value: Int): ViewType {
                return values().find { it.value == value } ?: throw IllegalArgumentException()
            }
        }
    }

    fun updateList(list: List<FriendUiEntity>) {
        if (currentTab == FriendsTab.FRIENDS) { // It seems hard, but it was easiest solution
            val listWithButton = list.toMutableList().apply {
                add(0, FriendUiEntity(ADD_BUTTON_ID, null, false, "", FriendsRelationship.STANDARD))
            }
            submitList(listWithButton)
        } else {
            submitList(list)
        }
    }
}

class FriendsDiffUtilCallback : DiffUtil.ItemCallback<FriendUiEntity>() {
    override fun areItemsTheSame(oldItem: FriendUiEntity, newItem: FriendUiEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FriendUiEntity, newItem: FriendUiEntity): Boolean {
        return oldItem == newItem
    }
}
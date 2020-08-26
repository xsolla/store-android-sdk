package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab

class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: FriendUiEntity, tab: FriendsTab) {
        if (isAddFriendsButton(tab)) {
            itemView.setOnClickListener {  } // TODO: go to add friend flow
        } else {
            item.id
            item.imageUrl
            item.isOnline
            item.nickname
            item.relationship
        }
    }

    private fun isAddFriendsButton(tab: FriendsTab) = tab == FriendsTab.FRIENDS && adapterPosition == 0
}
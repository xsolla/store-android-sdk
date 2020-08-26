package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends
import kotlinx.android.synthetic.main.item_friend.view.divider
import kotlinx.android.synthetic.main.item_friend.view.friendAvatar
import kotlinx.android.synthetic.main.item_friend.view.friendNickname

class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: FriendUiEntity, tab: FriendsTab, itemsCount: Int) {
        if (isAddFriendsButton(tab)) {
            itemView.setOnClickListener {  } // TODO: go to add friend flow
        } else {
            if (adapterPosition == itemsCount - 1) {
                itemView.divider.isVisible = false
            }

            Glide.with(itemView)
                .load(item.imageUrl)
                .apply(circleCropTransform())
                .placeholder(R.drawable.ic_xsolla_logo)
                .error(R.drawable.ic_xsolla_logo)
                .into(itemView.friendAvatar)

            itemView.friendNickname.text = item.nickname

            // TODO
            when (item.relationship) {
                VmFriends.Relationship.STANDARD -> {

                }
                VmFriends.Relationship.PENDING -> {

                }
                VmFriends.Relationship.BLOCKED -> {

                }
                VmFriends.Relationship.WAITING -> {

                }
            }

            item.isOnline // TODO
        }
    }

    private fun isAddFriendsButton(tab: FriendsTab) = tab == FriendsTab.FRIENDS && adapterPosition == 0
}
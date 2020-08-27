package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.vm.FriendUiEntity
import com.xsolla.android.storesdkexample.ui.vm.FriendsRelationship
import com.xsolla.android.storesdkexample.ui.vm.FriendsTab
import com.xsolla.android.storesdkexample.ui.vm.VmFriends
import kotlinx.android.synthetic.main.item_friend.view.divider
import kotlinx.android.synthetic.main.item_friend.view.friendAvatar
import kotlinx.android.synthetic.main.item_friend.view.friendNickname
import kotlinx.android.synthetic.main.item_friend.view.friendsOptionsButton

class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(
        item: FriendUiEntity,
        tab: FriendsTab,
        itemsCount: Int,
        onDeleteOptionClick: (user: FriendUiEntity) -> Unit,
        onBlockOptionClick: (user: FriendUiEntity) -> Unit,
        onUnblockOptionsClick: (user: FriendUiEntity) -> Unit
    ) {
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
                FriendsRelationship.STANDARD -> {
                    itemView.friendsOptionsButton.setOnClickListener {
                        AlertDialog.Builder(itemView.context) // TODO: Styling
                            .setTitle("${itemView.friendNickname.text} options")
                            .setItems(arrayOf("Delete friend", "Block user")) { _, which ->
                                if (which == 0) {
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
                                else {
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
                            .show()
                    }
                }
                FriendsRelationship.PENDING -> {
                    // Кнопки принять отклонить
                }
                FriendsRelationship.REQUESTED -> {
                    // Кнопка отзыва
                }
                FriendsRelationship.BLOCKED -> {
                    itemView.friendsOptionsButton.setOnClickListener {
                        AlertDialog.Builder(itemView.context)
                            .setTitle("${itemView.friendNickname.text} options")
                            .setItems(arrayOf("Unblock user")) { _, _ ->
                                onUnblockOptionsClick(item)
                            }
                            .show()
                    }
                }
            }

            item.isOnline // TODO
        }
    }

    private fun isAddFriendsButton(tab: FriendsTab) = tab == FriendsTab.FRIENDS && adapterPosition == 0
}
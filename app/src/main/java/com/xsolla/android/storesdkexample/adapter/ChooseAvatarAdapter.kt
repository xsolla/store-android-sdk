package com.xsolla.android.storesdkexample.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.data.local.PrefManager
import com.xsolla.android.storesdkexample.util.extensions.dpToPx
import kotlinx.android.synthetic.main.item_avatar.view.avatarForChoosing
import kotlinx.android.synthetic.main.item_avatar.view.avatarOverlay
import kotlinx.android.synthetic.main.item_avatar.view.icCheck

class ChooseAvatarAdapter(
    private val items: List<AvatarItem>,
    private val userId: String,
    private val onAvatarClickListener: (resource: Int) -> Unit
) : RecyclerView.Adapter<ChooseAvatarAdapter.ChooseAvatarViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAvatarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar, parent, false)
        return ChooseAvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseAvatarViewHolder, position: Int) {
        holder.bind(items[position], userId, onAvatarClickListener)
    }

    class ChooseAvatarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: AvatarItem, userId: String, onAvatarClickListener: (resource: Int) -> Unit) {
            Glide.with(itemView)
                .load(item.resource)
                .circleCrop()
                .into(itemView.avatarForChoosing)

            val currentAvatar = PrefManager.getAvatar(userId)
            itemView.avatarOverlay.isVisible = item.resource == currentAvatar
            itemView.icCheck.isVisible = item.resource == currentAvatar

            itemView.setOnClickListener { onAvatarClickListener(item.resource) }
        }
    }
}

data class AvatarItem(@DrawableRes val resource: Int)

class AvatarsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.left = 8.dpToPx()

        val position = parent.getChildAdapterPosition(view)
        if (position >= 3) {
            outRect.top = 8.dpToPx()
        }
    }
}
package com.xsolla.android.storesdkexample.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.storesdkexample.R
import kotlinx.android.synthetic.main.item_avatar.view.avatarForChoosing

class ChooseAvatarAdapter(
    private val items: List<AvatarItem>,
    private val onAvatarClickListener: (resource: Int) -> Unit
) : RecyclerView.Adapter<ChooseAvatarAdapter.ChooseAvatarViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAvatarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar, parent, false)
        return ChooseAvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseAvatarViewHolder, position: Int) {
        holder.bind(items[position], onAvatarClickListener)
    }

    class ChooseAvatarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: AvatarItem, onAvatarClickListener: (resource: Int) -> Unit) {
            Glide.with(itemView)
                .load(item.resource)
                .circleCrop()
                .into(itemView.avatarForChoosing)

            itemView.setOnClickListener { onAvatarClickListener(item.resource) }
        }
    }
}

data class AvatarItem(@DrawableRes val resource: Int)

class AvatarsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.left = 14 // TODO

        val position = parent.getChildAdapterPosition(view)
        if (position >= 3) {
            outRect.top = 14
        }
    }
}
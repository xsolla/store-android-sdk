package com.xsolla.android.storesdkexample.adapter

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.UserAttributesAdapter.Companion.FOOTER_VIEW_TYPE
import com.xsolla.android.storesdkexample.adapter.UserAttributesAdapter.Companion.ITEM_VIEW_TYPE
import com.xsolla.android.storesdkexample.adapter.holder.UserAttributeViewHolder
import com.xsolla.android.storesdkexample.ui.vm.UserAttributeUiEntity

class UserAttributesAdapter(
    private val onEditOptionClick: (item: UserAttributeItem.Item) -> Unit,
    private val onDeleteOptionClick: (item: UserAttributeItem.Item) -> Unit,
    private val onAddAttributeButtonClick: () -> Unit,
    private val onDocumentationClick: () -> Unit,
    private val onDeleteOptionClickByPosition: (position: Int) -> Unit
) : ListAdapter<UserAttributeItem, UserAttributeViewHolder>(UserAttributeDiffUtilCallback()) {
    companion object {
        const val ITEM_VIEW_TYPE = 1
        const val FOOTER_VIEW_TYPE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAttributeViewHolder {
        val view = if (viewType == ITEM_VIEW_TYPE) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_attribute, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_attribute_footer, parent, false)
        }

        return UserAttributeViewHolder(view, onEditOptionClick, onDeleteOptionClick, onAddAttributeButtonClick, onDocumentationClick, onDeleteOptionClickByPosition)
    }

    override fun onBindViewHolder(holder: UserAttributeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun toAdapterEntitiesWithFooter(items: List<UserAttributeUiEntity>, readOnly: Boolean): List<UserAttributeItem> {
        if (items.isEmpty()) return emptyList()

        val footer = UserAttributeItem.Footer(readOnly)

        val list = mutableListOf<UserAttributeItem>()
        items.forEach { list.add(UserAttributeItem.Item(it, readOnly)) }
        list.add(footer)
        return list
    }
}

sealed class UserAttributeItem(val id: String, open val readOnly: Boolean) {
    abstract val viewType: Int

    data class Item(val item: UserAttributeUiEntity, override val readOnly: Boolean) : UserAttributeItem(item.key, readOnly) {
        override val viewType = ITEM_VIEW_TYPE
    }
    data class Footer(override val readOnly: Boolean) : UserAttributeItem("FooterId", readOnly) {
        override val viewType = FOOTER_VIEW_TYPE
    }
}

class UserAttributeDiffUtilCallback : DiffUtil.ItemCallback<UserAttributeItem>() {
    override fun areItemsTheSame(oldItem: UserAttributeItem, newItem: UserAttributeItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserAttributeItem, newItem: UserAttributeItem): Boolean {
        return oldItem == newItem
    }
}

class DeleteSwipeCallback(
    private val icon: Drawable,
    private val background: ColorDrawable
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder.itemViewType == FOOTER_VIEW_TYPE) {
            return 0
        }
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (viewHolder is UserAttributeViewHolder) {
            viewHolder.onDeleteOptionClickByPosition(position)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val backgroundCornerOffset = 20
        val itemView = viewHolder.itemView

        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if (dX > 0) { // Swiping to the right
            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + icon.intrinsicWidth

            if (dX > iconLeft) {
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            } else {
                icon.setBounds(0, 0, 0, 0)
            }

            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom)
        } else {
            icon.setBounds(0, 0, 0, 0)
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c)
        icon.draw(c)
    }
}
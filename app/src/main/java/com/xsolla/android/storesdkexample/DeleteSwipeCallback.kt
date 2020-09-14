package com.xsolla.android.storesdkexample

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.adapter.UserAttributesAdapter.Companion.FOOTER_VIEW_TYPE
import com.xsolla.android.storesdkexample.adapter.holder.UserAttributeViewHolder

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
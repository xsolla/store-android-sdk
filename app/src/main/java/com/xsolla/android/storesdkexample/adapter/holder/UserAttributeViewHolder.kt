package com.xsolla.android.storesdkexample.adapter.holder

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.adapter.UserAttributeItem
import com.xsolla.android.storesdkexample.util.extensions.setClickableSpan
import kotlinx.android.synthetic.main.item_user_attribute.view.editButton
import kotlinx.android.synthetic.main.item_user_attribute.view.key
import kotlinx.android.synthetic.main.item_user_attribute.view.value
import kotlinx.android.synthetic.main.item_user_attribute_footer.view.editableFooter
import kotlinx.android.synthetic.main.item_user_attribute_footer.view.readOnlyFooter

class UserAttributeViewHolder(
    view: View,
    private val onEditOptionClick: (item: UserAttributeItem.Item) -> Unit,
    private val onDeleteOptionClick: (item: UserAttributeItem.Item) -> Unit,
    private val onAddAttributeButtonClick: () -> Unit,
    private val onDocumentationClick: () -> Unit,
    val onDeleteOptionClickByPosition: (position: Int) -> Unit
) : RecyclerView.ViewHolder(view) {
    fun bind(item: UserAttributeItem) =
        when (item) {
            is UserAttributeItem.Item -> { bindItem(item) }
            is UserAttributeItem.Footer -> { bindFooter(item) }
        }

    private fun bindItem(item: UserAttributeItem.Item) {
        itemView.key.text = item.item.key
        itemView.value.text = item.item.value
        itemView.editButton.isGone = item.readOnly
        itemView.editButton.setOnClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("${item.item.key} options")
                .setItems(arrayOf("Edit", "Delete")) { _, option ->
                    if (option == 0) {
                        onEditOptionClick(item)
                    } else if (option == 1) {
                        onDeleteOptionClick(item)
                    }
                }
                .show()
        }
    }

    private fun bindFooter(footer: UserAttributeItem.Footer) {
        itemView.readOnlyFooter.isVisible = footer.readOnly
        itemView.editableFooter.isGone = footer.readOnly

        itemView.readOnlyFooter.setClickableSpan(
            isUnderlineText = true,
            startIndex = itemView.readOnlyFooter.text.indexOf("see"),
            endIndex = itemView.readOnlyFooter.text.length,
            onClick = { onDocumentationClick() }
        )
        itemView.editableFooter.setOnClickListener {
            onAddAttributeButtonClick()
        }
    }
}
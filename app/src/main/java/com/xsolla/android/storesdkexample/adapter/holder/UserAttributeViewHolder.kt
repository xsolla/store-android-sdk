package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.adapter.UserAttributeItem
import com.xsolla.android.storesdkexample.databinding.ItemUserAttributeBinding
import com.xsolla.android.storesdkexample.databinding.ItemUserAttributeFooterBinding
import com.xsolla.android.storesdkexample.util.extensions.setClickableSpan

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
        val binding = ItemUserAttributeBinding.bind(itemView)

        binding.key.text = item.item.key
        binding.value.text = item.item.value
        binding.editButton.isGone = item.readOnly
        binding.editButton.setOnClickListener {
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
        val binding = ItemUserAttributeFooterBinding.bind(itemView)

        binding.readOnlyFooter.isVisible = footer.readOnly
        binding.editableFooter.isGone = footer.readOnly

        binding.readOnlyFooter.setClickableSpan(
            isUnderlineText = true,
            startIndex = binding.readOnlyFooter.text.indexOf("See"),
            endIndex = binding.readOnlyFooter.text.lastIndexOf("documentation") + "documentation".length,
            onClick = { onDocumentationClick() }
        )
        binding.editableFooter.setOnClickListener {
            onAddAttributeButtonClick()
        }
    }
}
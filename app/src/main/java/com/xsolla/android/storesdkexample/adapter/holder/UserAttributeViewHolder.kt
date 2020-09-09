package com.xsolla.android.storesdkexample.adapter.holder

import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.adapter.UserAttributeItem
import kotlinx.android.synthetic.main.item_user_attribute.view.editButton
import kotlinx.android.synthetic.main.item_user_attribute.view.key
import kotlinx.android.synthetic.main.item_user_attribute.view.value
import kotlinx.android.synthetic.main.item_user_attribute_footer.view.editableFooter
import kotlinx.android.synthetic.main.item_user_attribute_footer.view.readOnlyFooter

class UserAttributeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
            Toast.makeText(it.context, "Soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindFooter(footer: UserAttributeItem.Footer) {
        itemView.readOnlyFooter.isVisible = footer.readOnly
        itemView.editableFooter.isGone = footer.readOnly
    }
}
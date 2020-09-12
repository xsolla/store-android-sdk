package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
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

        return UserAttributeViewHolder(view, onEditOptionClick, onDeleteOptionClick, onAddAttributeButtonClick, onDocumentationClick)
    }

    override fun onBindViewHolder(holder: UserAttributeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun toAdapterEntitiesWithFooter(items: List<UserAttributeUiEntity>, readOnly: Boolean): List<UserAttributeItem> {
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
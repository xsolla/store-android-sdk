package com.xsolla.android.samples.display.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.samples.display.adapter.holder.InventoryItemViewHolder
import com.xsolla.android.storesdkexample.R

class InventoryItemsAdapter(private val items: List<InventoryResponse.Item>) :
    RecyclerView.Adapter<InventoryItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryItemViewHolder {
        return InventoryItemViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: InventoryItemViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.view).load(item.imageUrl).into(holder.itemImage)
        holder.itemName.text = item.name
        holder.itemDescription.text = item.description
        holder.itemQuantity.text = item.quantity.toString()
    }

    override fun getItemCount() = items.size
}
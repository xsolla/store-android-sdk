package com.xsolla.android.samples.display.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.R

class InventoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val view: View = itemView
    val itemName: TextView = itemView.findViewById(R.id.inventory_item_name)
    val itemQuantity: TextView = itemView.findViewById(R.id.inventory_item_quantity)
    val itemDescription: TextView = itemView.findViewById(R.id.inventory_item_description)
    val itemImage: ImageView = itemView.findViewById(R.id.inventory_item_image)
}
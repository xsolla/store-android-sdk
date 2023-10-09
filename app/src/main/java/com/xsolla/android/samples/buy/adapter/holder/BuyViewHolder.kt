package com.xsolla.android.samples.buy.adapter.holder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.R

class BuyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val view: View = itemView
    val itemName: TextView = itemView.findViewById(R.id.buy_item_name)
    val itemPrice: TextView = itemView.findViewById(R.id.buy_item_price)
    val itemDescription: TextView = itemView.findViewById(R.id.buy_item_description)
    val itemImage: ImageView = itemView.findViewById(R.id.buy_item_image)
    val itemButton: Button = itemView.findViewById(R.id.buy_item_button)
}
package com.xsolla.android.samples.display.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.R

class BundleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val bundleView: View = itemView
    val itemName: TextView = itemView.findViewById(R.id.bundle_item_name)
    val itemPrice: TextView = itemView.findViewById(R.id.bundle_item_price)
    val itemDescription: TextView = itemView.findViewById(R.id.bundle_item_description)
    val itemContentDescription: TextView = itemView.findViewById(R.id.bundle_item_content_description)
    val itemImage: ImageView = itemView.findViewById(R.id.bundle_item_image)
}


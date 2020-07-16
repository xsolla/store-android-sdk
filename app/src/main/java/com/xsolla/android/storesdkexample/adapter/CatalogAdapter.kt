package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R

class CatalogAdapter(
        private val items: List<VirtualItemsResponse.Item> //,
        // private val addToCartListener: AddToCartListener
) : RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CatalogViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    class CatalogViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
    ) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_catalog, parent, false)) {

        private var itemIcon: ImageView = itemView.findViewById(R.id.item_icon)
        private var itemName: TextView = itemView.findViewById(R.id.item_name)
        private var itemPrice: TextView = itemView.findViewById(R.id.item_price)

        fun bind(item: VirtualItemsResponse.Item) {
            Glide.with(itemView).load(item.imageUrl).into(itemIcon)
            itemName.text = item.name
        }

    }

}
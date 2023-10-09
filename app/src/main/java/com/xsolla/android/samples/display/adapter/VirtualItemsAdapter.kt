package com.xsolla.android.samples.display.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.samples.display.adapter.holder.VirtualItemViewHolder
import com.xsolla.android.storesdkexample.R

class VirtualItemsAdapter(private val items: List<VirtualItemsResponse.Item>) :
    RecyclerView.Adapter<VirtualItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VirtualItemViewHolder {
        return VirtualItemViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.virtual_item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: VirtualItemViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.view).load(item.imageUrl).into(holder.itemImage)
        holder.itemName.text = item.name
        holder.itemDescription.text = item.description
        var priceText: String
        if(item.virtualPrices.isNotEmpty()) {
            priceText = item.virtualPrices[0].getAmountRaw() + " " + item.virtualPrices[0].name
        } else {
            priceText = item.price?.getAmountRaw() + " " + item.price?.currency.toString()
        }

        holder.itemPrice.text = priceText
    }

    override fun getItemCount() = items.size
}
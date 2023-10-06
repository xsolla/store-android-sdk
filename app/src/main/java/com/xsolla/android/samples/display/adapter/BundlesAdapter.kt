package com.xsolla.android.samples.display.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.samples.display.adapter.holder.BundleViewHolder
import com.xsolla.android.storesdkexample.R

class BundlesAdapter(private val items: List<BundleItem>) :
    RecyclerView.Adapter<BundleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BundleViewHolder {
        return BundleViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.bundle_item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: BundleViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.bundleView).load(item.imageUrl).into(holder.itemImage)
        holder.itemName.text = item.name
        holder.itemDescription.text = item.description
        holder.itemContentDescription.text = "This bundle includes " + item.content.size + " items: " + item.content.map { it.name }.toMutableList().joinToString(", ")
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
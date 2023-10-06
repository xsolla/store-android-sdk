package com.xsolla.android.samples.display.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.samples.display.adapter.holder.CurrencyPackageViewHolder
import com.xsolla.android.storesdkexample.R

class CurrencyPackagesAdapter(private val items: List<VirtualCurrencyPackageResponse.Item>) :
    RecyclerView.Adapter<CurrencyPackageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyPackageViewHolder {
        return CurrencyPackageViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_package_item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: CurrencyPackageViewHolder, position: Int) {
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
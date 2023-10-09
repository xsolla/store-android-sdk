package com.xsolla.android.samples.display.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse
import com.xsolla.android.samples.display.adapter.holder.CurrencyBalanceViewHolder
import com.xsolla.android.storesdkexample.R

class CurrencyBalanceAdapter(private val items: List<VirtualBalanceResponse.Item>) :
    RecyclerView.Adapter<CurrencyBalanceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyBalanceViewHolder {
        return CurrencyBalanceViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_balance_item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: CurrencyBalanceViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.view).load(item.imageUrl).into(holder.itemImage)
        holder.itemName.text = item.name
        holder.itemQuantity.text = item.amount.toString()
    }

    override fun getItemCount() = items.size
}
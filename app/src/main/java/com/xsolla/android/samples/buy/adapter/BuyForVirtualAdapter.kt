package com.xsolla.android.samples.buy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.samples.buy.adapter.holder.BuyViewHolder
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.CreateOrderByVirtualCurrencyCallback
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.storesdkexample.R

class BuyForVirtualAdapter(private val items: List<VirtualItemsResponse.Item>) :
    RecyclerView.Adapter<BuyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyViewHolder {
        return BuyViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.buy_item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: BuyViewHolder, position: Int) {
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

        holder.itemButton.setOnClickListener {
            it.isEnabled = false;
            XStore.createOrderByVirtualCurrency(
                object : CreateOrderByVirtualCurrencyCallback {
                    override fun onSuccess(response: CreateOrderByVirtualCurrencyResponse) {
                        it.isEnabled = true
                        Snackbar.make(holder.view, "success purchase!", Snackbar.LENGTH_LONG).show()
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        val message = errorMessage ?: throwable?.javaClass?.name ?: "Error"
                        Snackbar.make(holder.view, message, Snackbar.LENGTH_LONG).show()
                        it.isEnabled = true
                    }
                },
                item.sku!!, item.virtualPrices[0].sku!!
            )
        }
    }

    override fun getItemCount() = items.size
}
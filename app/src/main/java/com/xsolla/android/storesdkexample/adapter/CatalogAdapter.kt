package com.xsolla.android.storesdkexample.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.entity.response.common.ExpirationPeriod
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.util.AmountUtils
import kotlinx.android.synthetic.main.item_catalog.view.*

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

        fun bind(item: VirtualItemsResponse.Item) {
            Glide.with(itemView).load(item.imageUrl).into(itemView.itemIcon)
            itemView.itemName.text = item.name

            if (item.virtualPrices.isNotEmpty()) {
                bindItemVirtualPrice(item)
            }

            item.price?.let { bindItemPrice(item) }

            item.inventoryOption.expirationPeriod?.let { binExpirationPeriod(it) }
        }

        private fun bindItemPrice(item: VirtualItemsResponse.Item) {
            val price = item.price
            if (price.amountDecimal == price.amountWithoutDiscountDecimal) {
                itemView.itemPrice.text = AmountUtils.prettyPrint(price.amountDecimal, price.currency)
                itemView.itemOldPrice.visibility = View.INVISIBLE
                itemView.itemSaleLabel.visibility = View.INVISIBLE
            } else {
                itemView.itemPrice.text = AmountUtils.prettyPrint(price.amountDecimal, price.currency)
                itemView.itemOldPrice.text = AmountUtils.prettyPrint(price.amountWithoutDiscountDecimal)
                itemView.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                itemView.itemOldPrice.visibility = View.VISIBLE
                itemView.itemSaleLabel.visibility = View.VISIBLE
            }
        }

        private fun bindItemVirtualPrice(item: VirtualItemsResponse.Item) {
            val price = item.virtualPrices[0]

            itemView.itemVirtualPriceIcon.visibility = View.VISIBLE
            Glide.with(itemView.context).load(price.imageUrl).into(itemView.itemVirtualPriceIcon)
            if (price.amountDecimal == price.amountWithoutDiscountDecimal) {
                itemView.itemPrice.text = AmountUtils.prettyPrint(price.amountDecimal)
                itemView.itemOldPrice.visibility = View.INVISIBLE
                itemView.itemSaleLabel.visibility = View.INVISIBLE
            } else {
                itemView.itemPrice.text = AmountUtils.prettyPrint(price.amountDecimal)
                itemView.itemOldPrice.text = AmountUtils.prettyPrint(price.amountWithoutDiscountDecimal)
                itemView.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                itemView.itemOldPrice.visibility = View.VISIBLE
                itemView.itemSaleLabel.visibility = View.VISIBLE
            }
        }

        private fun binExpirationPeriod(expirationPeriod: ExpirationPeriod) {
            itemView.itemExpiration.visibility = View.VISIBLE
            val sb = StringBuilder()
            sb.append("Expiration: ")
            sb.append(expirationPeriod.value)
            sb.append(' ')
            sb.append(expirationPeriod.type.name.toLowerCase())
            if (expirationPeriod.value != 1) {
                sb.append('s')
            }
            itemView.itemExpiration.text = sb
        }

    }

}
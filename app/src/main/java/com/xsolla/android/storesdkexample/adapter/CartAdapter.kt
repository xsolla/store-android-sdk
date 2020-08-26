package com.xsolla.android.storesdkexample.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.common.ExpirationPeriod
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.CartChangeListener
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.util.AmountUtils
import kotlinx.android.synthetic.main.item_cart.view.*

class CartAdapter(
        val items: MutableList<CartResponse.Item>,
        private val vmCart: VmCart,
        private val cartChangeListener: CartChangeListener
)
    : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CartViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class CartViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup
    ) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_cart, parent, false)) {

        fun bind(item: CartResponse.Item) {
            Glide.with(itemView).load(item.imageUrl).into(itemView.itemIcon)
            itemView.itemName.text = item.name

            bindCounter(item)

            if (item.virtualPrices.isNotEmpty()) {
                bindItemVirtualPrice(item)
            }

            item.price?.let { bindItemPrice(item) }

            item.inventoryOption?.expirationPeriod?.let { binExpirationPeriod(it) }
        }

        private fun bindItemPrice(item: CartResponse.Item) {
            val price = item.price
            if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal()) {
                itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!, price.currency!!)
                itemView.itemOldPrice.visibility = View.INVISIBLE
                itemView.itemSaleLabel.visibility = View.INVISIBLE
            } else {
                itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!, price.currency!!)
                itemView.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal()!!)
                itemView.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                itemView.itemOldPrice.visibility = View.VISIBLE
                itemView.itemSaleLabel.visibility = View.VISIBLE
            }
        }

        private fun bindItemVirtualPrice(item: CartResponse.Item) {
            val price = item.virtualPrices[0]

            itemView.itemVirtualPriceIcon.visibility = View.VISIBLE
            Glide.with(itemView.context).load(price.imageUrl).into(itemView.itemVirtualPriceIcon)
            if (price.getAmountDecimal() == price.getAmountWithoutDiscountDecimal()) {
                itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!)
                itemView.itemOldPrice.visibility = View.INVISIBLE
                itemView.itemSaleLabel.visibility = View.INVISIBLE
            } else {
                itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!)
                itemView.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal()!!)
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

        private fun bindCounter(item: CartResponse.Item) {
            itemView.itemTextCount.text = item.quantity.toString()
            if (item.quantity > 1) {
                itemView.itemButtonMinus.setImageResource(R.drawable.ic_cart_minus)
            } else {
                itemView.itemButtonMinus.setImageResource(R.drawable.ic_cart_delete)
            }
            itemView.itemButtonMinus.setOnClickListener {
                vmCart.changeItemCount(item, -1) { result -> cartChangeListener.onChange(result) }
            }
            itemView.itemButtonPlus.setOnClickListener {
                vmCart.changeItemCount(item, 1) { result -> cartChangeListener.onChange(result) }
            }
        }

    }

}
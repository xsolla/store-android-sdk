package com.xsolla.android.storesdkexample.adapter.holder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.util.AmountUtils
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.item_vi_real_price.view.*

class VcRealPriceViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        private val vmCart: VmCart,
        private val purchaseListener: PurchaseListener
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_vi_real_price, parent, false)) {

    fun bind(item: VirtualCurrencyPackageResponse.Item) {
        Glide.with(itemView).load(item.imageUrl).into(itemView.itemIcon)
        itemView.itemName.text = item.name
        itemView.itemAdditionalInfo.text = item.description
        bindItemPrice(item)
    }

    private fun bindItemPrice(item: VirtualCurrencyPackageResponse.Item) {
        val price = item.price
        if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal()) {
            itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
            itemView.itemOldPrice.visibility = View.INVISIBLE
            itemView.itemSaleLabel.visibility = View.INVISIBLE
        } else {
            val currentPrice = price.getAmountDecimal()
            val priceWithoutDiscount = price.getAmountWithoutDiscountDecimal()
            val discount = 100 - ((currentPrice!!.toInt() * 100) / priceWithoutDiscount!!.toInt())

            itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
            itemView.itemSaleDiscount.text = "-${discount}%"
            itemView.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
            itemView.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            itemView.itemOldPrice.visibility = View.VISIBLE
            itemView.itemSaleLabel.visibility = View.VISIBLE
        }

        itemView.addToCartButton.setOnClickListener { v ->
            ViewUtils.disable(v)
            val cartContent = vmCart.cartContent.value
            val quantity = cartContent?.find { it.sku == item.sku }?.quantity ?: 0
            XStore.updateItemFromCurrentCart(item.sku, quantity + 1, object : XStoreCallback<Void>() {
                override fun onSuccess(response: Void?) {
                    vmCart.updateCart()
                    ViewUtils.enable(v)
                }

                override fun onFailure(errorMessage: String) {
                    purchaseListener.onFailure(errorMessage)
                    ViewUtils.enable(v)
                }

            })
        }
    }

}

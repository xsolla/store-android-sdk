package com.xsolla.android.storesdkexample.adapter.holder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.common.ExpirationPeriod
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.item_vi_real_price.view.*
import kotlinx.android.synthetic.main.item_vi_virtual_price.view.buyButton

class ViRealPriceViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        private val vmCart: VmCart,
        private val purchaseListener: PurchaseListener
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_vi_real_price, parent, false)) {

    fun bind(item: VirtualItemUiEntity) {
        Glide.with(itemView).load(item.imageUrl).into(itemView.itemIcon)
        itemView.itemName.text = item.name
        bindPurchasedPlaceholderIfNeed(item)
        bindItemPrice(item)
        bindExpirationPeriod(item.inventoryOption?.expirationPeriod)
    }

    private fun bindPurchasedPlaceholderIfNeed(item: VirtualItemUiEntity) {
        if (item.hasInInventory) {
            if (item.inventoryOption?.consumable == null && item.inventoryOption?.expirationPeriod == null) {
                itemView.purchasedPlaceholder.isVisible = true
                itemView.buyButton?.isVisible = false
                itemView.addToCartButton?.isVisible = false
                itemView.itemPrice.isVisible = false
                itemView.itemOldPrice.isVisible = false
                itemView.itemSaleLabel.isVisible = false
            } else {
                itemView.purchasedPlaceholder.isVisible = false
                itemView.buyButton?.isVisible = true
                itemView.addToCartButton?.isVisible = true
                itemView.itemPrice.isVisible = true
                itemView.itemOldPrice.isVisible = true
                itemView.itemSaleLabel.isVisible = true
            }
        } else {
            itemView.purchasedPlaceholder.isVisible = false
            itemView.buyButton?.isVisible = true
            itemView.addToCartButton?.isVisible = true
            itemView.itemPrice.isVisible = true
            itemView.itemOldPrice.isVisible = true
            itemView.itemSaleLabel.isVisible = true
        }
    }

    private fun bindItemPrice(item: VirtualItemUiEntity) {
        val price = item.price
        if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal() || price.getAmountWithoutDiscountDecimal() == null) {
            itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
            itemView.itemOldPrice.visibility = View.INVISIBLE
            itemView.itemSaleLabel.visibility = View.INVISIBLE
        } else {
            if (itemView.itemOldPrice.isVisible && itemView.itemSaleLabel.isVisible) {
                val discount = AmountUtils.calculateDiscount(price.getAmountDecimal()!!, price.getAmountWithoutDiscountDecimal()!!)

                itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
                itemView.itemSaleDiscount.text = "-${discount}%"
                itemView.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
                itemView.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        itemView.addToCartButton.setOnClickListener { v ->
            ViewUtils.disable(v)
            val cartContent = vmCart.cartContent.value
            val quantity = cartContent?.find { it.sku == item.sku }?.quantity ?: 0
            XStore.updateItemFromCurrentCart(item.sku, quantity + 1, object : XStoreCallback<Void>() {
                override fun onSuccess(response: Void?) {
                    vmCart.updateCart()
                    purchaseListener.onSuccess()
                    ViewUtils.enable(v)
                }

                override fun onFailure(errorMessage: String) {
                    purchaseListener.onFailure(errorMessage)
                    ViewUtils.enable(v)
                }
            })
        }
    }

    private fun bindExpirationPeriod(expirationPeriod: ExpirationPeriod?) {
        if (expirationPeriod == null) {
            itemView.itemAdditionalInfo.visibility = View.GONE
        } else {
            itemView.itemAdditionalInfo.visibility = View.VISIBLE
            val sb = StringBuilder()
            sb.append("Expiration in ")
            sb.append(expirationPeriod.value)
            sb.append(' ')
            sb.append(expirationPeriod.type.name.toLowerCase())
            if (expirationPeriod.value != 1) {
                sb.append('s')
            }
            itemView.itemAdditionalInfo.text = sb
        }
    }

}

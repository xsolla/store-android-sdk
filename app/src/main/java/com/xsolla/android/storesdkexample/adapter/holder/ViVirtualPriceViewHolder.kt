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
import com.xsolla.android.store.entity.response.common.VirtualPrice
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.item_vi_virtual_price.view.*

class ViVirtualPriceViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    private val vmBalance: VmBalance,
    private val purchaseListener: PurchaseListener
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_vi_virtual_price, parent, false)) {

    fun bind(item: VirtualItemUiEntity) {
        val price = item.virtualPrices[0]
        Glide.with(itemView).load(item.imageUrl).into(itemView.itemIcon)
        itemView.itemName.text = item.name
        bindPurchasedPlaceholderIfNeed(item)
        bindItemPrice(price)
        bindExpirationPeriod(item.inventoryOption?.expirationPeriod)
        initBuyButton(item, price)
    }

    private fun bindPurchasedPlaceholderIfNeed(item: VirtualItemUiEntity) {
        if (item.hasInInventory) {
            if (item.inventoryOption?.consumable == null && item.inventoryOption?.expirationPeriod == null) {
                itemView.purchasedPlaceholder.isVisible = true
                itemView.buyButton?.isVisible = false
                itemView.itemPrice.isVisible = false
                itemView.itemOldPrice.isVisible = false
                itemView.itemSaleLabel.isVisible = false
                itemView.itemVirtualPriceIcon.isVisible = false
            } else {
                itemView.purchasedPlaceholder.isVisible = false
                itemView.buyButton?.isVisible = true
                itemView.itemPrice.isVisible = true
                itemView.itemOldPrice.isVisible = true
                itemView.itemSaleLabel.isVisible = true
                itemView.itemVirtualPriceIcon.isVisible = true
            }
        } else {
            itemView.purchasedPlaceholder.isVisible = false
            itemView.buyButton?.isVisible = true
            itemView.itemPrice.isVisible = true
            itemView.itemOldPrice.isVisible = true
            itemView.itemSaleLabel.isVisible = true
            itemView.itemVirtualPriceIcon.isVisible = true
        }
    }

    private fun bindItemPrice(price: VirtualPrice) {
        Glide.with(itemView.context).load(price.imageUrl).into(itemView.itemVirtualPriceIcon)

        if (price.getAmountDecimal() == price.getAmountWithoutDiscountDecimal() || price.calculatedPrice?.amountWithoutDiscount == null) {
            itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal())
            itemView.itemOldPrice.visibility = View.INVISIBLE
            itemView.itemSaleLabel.visibility = View.INVISIBLE
        } else {
            val discount = AmountUtils.calculateDiscount(price.getAmountDecimal()!!, price.getAmountWithoutDiscountDecimal()!!)

            itemView.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal())
            itemView.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
            itemView.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            itemView.itemOldPrice.visibility = View.VISIBLE
            itemView.itemSaleLabel.visibility = View.VISIBLE
            itemView.itemSaleDiscount.text = "-${discount}%"
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

    private fun initBuyButton(item: VirtualItemUiEntity, virtualPrice: VirtualPrice) {
        itemView.buyButton.setOnClickListener { v ->
            ViewUtils.disable(v)
            XStore.createOrderByVirtualCurrency(item.sku, virtualPrice.sku, object : XStoreCallback<CreateOrderByVirtualCurrencyResponse?>() {
                override fun onSuccess(response: CreateOrderByVirtualCurrencyResponse?) {
                    vmBalance.updateVirtualBalance()
                    purchaseListener.showMessage("Purchased by Virtual currency")
                    ViewUtils.enable(v)
                }

                override fun onFailure(errorMessage: String) {
                    purchaseListener.showMessage(errorMessage)
                    ViewUtils.enable(v)
                }
            })
        }
    }
}
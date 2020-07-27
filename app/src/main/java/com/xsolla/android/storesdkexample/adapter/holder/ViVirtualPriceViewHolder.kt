package com.xsolla.android.storesdkexample.adapter.holder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.common.ExpirationPeriod
import com.xsolla.android.store.entity.response.common.VirtualPrice
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.util.AmountUtils
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.item_vi_virtual_price.view.*

class ViVirtualPriceViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        private val vmBalance: VmBalance,
        private val purchaseListener: PurchaseListener
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_vi_virtual_price, parent, false)) {

    fun bind(item: VirtualItemsResponse.Item) {
        val price = item.virtualPrices[0]
        Glide.with(itemView).load(item.imageUrl).into(itemView.itemIcon)
        itemView.itemName.text = item.name
        bindItemPrice(price)
        item.inventoryOption.expirationPeriod?.let { binExpirationPeriod(it) }
        initBuyButton(item, price)
    }

    private fun bindItemPrice(price: VirtualPrice) {
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
        itemView.itemAdditionalInfo.visibility = View.VISIBLE
        val sb = StringBuilder()
        sb.append("Expiration: ")
        sb.append(expirationPeriod.value)
        sb.append(' ')
        sb.append(expirationPeriod.type.name.toLowerCase())
        if (expirationPeriod.value != 1) {
            sb.append('s')
        }
        itemView.itemAdditionalInfo.text = sb
    }

    private fun initBuyButton(item: VirtualItemsResponse.Item, virtualPrice: VirtualPrice) {
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
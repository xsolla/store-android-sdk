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
import com.xsolla.android.appcore.databinding.ItemViVirtualPriceBinding
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.util.ViewUtils

class ViVirtualPriceViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    private val vmBalance: VmBalance,
    private val purchaseListener: PurchaseListener
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_vi_virtual_price, parent, false)) {
    private val binding = ItemViVirtualPriceBinding.bind(itemView)

    fun bind(item: VirtualItemUiEntity) {
        val price = item.virtualPrices[0]
        Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
        binding.itemName.text = item.name
        bindPurchasedPlaceholderIfNeed(item)
        bindItemPrice(price)
        bindExpirationPeriod(item.inventoryOption?.expirationPeriod)
        initBuyButton(item, price)
    }

    private fun bindPurchasedPlaceholderIfNeed(item: VirtualItemUiEntity) {
        if (item.hasInInventory) {
            if (item.inventoryOption?.consumable == null && item.inventoryOption?.expirationPeriod == null) {
                binding.purchasedPlaceholder.isVisible = true
                binding.buyButton.isVisible = false
                binding.itemPrice.isVisible = false
                binding.itemOldPrice.isVisible = false
                binding.itemSaleLabel.isVisible = false
                binding.itemVirtualPriceIcon.isVisible = false
            } else {
                binding.purchasedPlaceholder.isVisible = false
                binding.buyButton.isVisible = true
                binding.itemPrice.isVisible = true
                binding.itemOldPrice.isVisible = true
                binding.itemSaleLabel.isVisible = true
                binding.itemVirtualPriceIcon.isVisible = true
            }
        } else {
            binding.purchasedPlaceholder.isVisible = false
            binding.buyButton.isVisible = true
            binding.itemPrice.isVisible = true
            binding.itemOldPrice.isVisible = true
            binding.itemSaleLabel.isVisible = true
            binding.itemVirtualPriceIcon.isVisible = true
        }
    }

    private fun bindItemPrice(price: VirtualPrice) {
        Glide.with(itemView.context).load(price.imageUrl).into(binding.itemVirtualPriceIcon)

        if (price.getAmountDecimal() == price.getAmountWithoutDiscountDecimal() || price.calculatedPrice?.amountWithoutDiscount == null) {
            binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal())
            binding.itemOldPrice.visibility = View.INVISIBLE
            binding.itemSaleLabel.visibility = View.INVISIBLE
        } else {
            val discount = AmountUtils.calculateDiscount(price.getAmountDecimal()!!, price.getAmountWithoutDiscountDecimal()!!)

            binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal())
            binding.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
            binding.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.itemOldPrice.visibility = View.VISIBLE
            binding.itemSaleLabel.visibility = View.VISIBLE
            binding.itemSaleDiscount.text = "-${discount}%"
        }
    }

    private fun bindExpirationPeriod(expirationPeriod: ExpirationPeriod?) {
        if (expirationPeriod == null) {
            binding.itemAdditionalInfo.visibility = View.GONE
        } else {
            binding.itemAdditionalInfo.visibility = View.VISIBLE
            val sb = StringBuilder()
            sb.append("Expiration in ")
            sb.append(expirationPeriod.value)
            sb.append(' ')
            sb.append(expirationPeriod.type.name.toLowerCase())
            if (expirationPeriod.value != 1) {
                sb.append('s')
            }
            binding.itemAdditionalInfo.text = sb
        }
    }

    private fun initBuyButton(item: VirtualItemUiEntity, virtualPrice: VirtualPrice) {
        binding.buyButton.setOnClickListener { v ->
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
package com.xsolla.android.storesdkexample.adapter.holder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.databinding.ItemViRealPriceBinding
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.util.ViewUtils

class VcRealPriceViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        private val vmCart: VmCart,
        private val purchaseListener: PurchaseListener
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_vi_real_price, parent, false)) {
    private val binding = ItemViRealPriceBinding.bind(itemView)

    fun bind(item: VirtualCurrencyPackageResponse.Item) {
        Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
        binding.itemName.text = item.name
        binding.itemAdditionalInfo.text = item.description
        bindItemPrice(item)
    }

    private fun bindItemPrice(item: VirtualCurrencyPackageResponse.Item) {
        val price = item.price
        if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal()) {
            binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
            binding.itemOldPrice.visibility = View.INVISIBLE
            binding.itemSaleLabel.visibility = View.INVISIBLE
        } else {
            val discount = AmountUtils.calculateDiscount(price.getAmountDecimal()!!, price.getAmountWithoutDiscountDecimal()!!)

            binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
            binding.itemSaleDiscount.text = "-${discount}%"
            binding.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
            binding.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.itemOldPrice.visibility = View.VISIBLE
            binding.itemSaleLabel.visibility = View.VISIBLE
        }

        binding.addToCartButton.setOnClickListener { v ->
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

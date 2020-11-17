package com.xsolla.android.customauth.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.ItemViRealPriceBinding
import com.xsolla.android.customauth.databinding.ItemViVirtualPriceBinding
import com.xsolla.android.customauth.ui.store.PurchaseListener
import com.xsolla.android.customauth.ui.store.VirtualItemUiEntity
import com.xsolla.android.customauth.viewmodels.VmBalance
import com.xsolla.android.customauth.viewmodels.VmCart
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.common.ExpirationPeriod
import com.xsolla.android.store.entity.response.common.VirtualPrice
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse

class ViAdapter(
    private val items: List<VirtualItemUiEntity>,
    private val vmCart: VmCart,
    private val vmBalance: VmBalance,
    private val purchaseListener: PurchaseListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val REAL_PRICE = 0
        private const val VIRTUAL_PRICE = 1
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item.virtualPrices.isNotEmpty()) {
            return VIRTUAL_PRICE
        }
        return REAL_PRICE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            REAL_PRICE -> ViRealPriceViewHolder(vmCart, purchaseListener, inflater.inflate(R.layout.item_vi_real_price, parent, false))
            else -> ViVirtualPriceViewHolder(vmBalance, purchaseListener, inflater.inflate(R.layout.item_vi_virtual_price, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder.itemViewType) {
            REAL_PRICE -> (holder as ViRealPriceViewHolder).bind(item)
            VIRTUAL_PRICE -> (holder as ViVirtualPriceViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size
}

class ViRealPriceViewHolder(
    private val vmCart: VmCart,
    private val purchaseListener: PurchaseListener,
    view: View
) : RecyclerView.ViewHolder(view) {

    private val binding: ItemViRealPriceBinding = ItemViRealPriceBinding.bind(view)

    fun bind(item: VirtualItemUiEntity) {
        Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
        binding.itemName.text = item.name
        bindPurchasedPlaceholderIfNeed(item)
        bindItemPrice(item)
        bindExpirationPeriod(item.inventoryOption?.expirationPeriod)
    }

    private fun bindPurchasedPlaceholderIfNeed(item: VirtualItemUiEntity) {
        if (item.hasInInventory) {
            if (item.inventoryOption?.consumable == null && item.inventoryOption?.expirationPeriod == null) {
                binding.purchasedPlaceholder.isVisible = true
                binding.addToCartButton.isVisible = false
                binding.itemPrice.isVisible = false
                binding.itemOldPrice.isVisible = false
                binding.itemSaleLabel.isVisible = false
            } else {
                binding.purchasedPlaceholder.isVisible = false
                binding.addToCartButton.isVisible = true
                binding.itemPrice.isVisible = true
                binding.itemOldPrice.isVisible = true
                binding.itemSaleLabel.isVisible = true
            }
        } else {
            binding.purchasedPlaceholder.isVisible = false
            binding.addToCartButton.isVisible = true
            binding.itemPrice.isVisible = true
            binding.itemOldPrice.isVisible = true
            binding.itemSaleLabel.isVisible = true
        }
    }

    private fun bindItemPrice(item: VirtualItemUiEntity) {
        val price = item.price
        if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal() || price.getAmountWithoutDiscountDecimal() == null) {
            binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
            binding.itemOldPrice.visibility = View.INVISIBLE
            binding.itemSaleLabel.visibility = View.INVISIBLE
        } else {
            if (binding.itemOldPrice.isVisible && binding.itemSaleLabel.isVisible) {
                binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
                binding.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
                binding.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        binding.addToCartButton.setOnClickListener { v ->
            v.isEnabled = false
            val cartContent = vmCart.cartContent.value
            val quantity = cartContent?.find { it.sku == item.sku }?.quantity ?: 0
            XStore.updateItemFromCurrentCart(item.sku, quantity + 1, object : XStoreCallback<Void>() {
                override fun onSuccess(response: Void?) {
                    vmCart.updateCart()
                    purchaseListener.onSuccess()
                    v.isEnabled = true
                }

                override fun onFailure(errorMessage: String) {
                    purchaseListener.onFailure(errorMessage)
                    v.isEnabled = true
                }
            })
        }
    }

    private fun bindExpirationPeriod(expirationPeriod: ExpirationPeriod?) {
        if (expirationPeriod == null) {
            binding.itemAdditionalInfo.visibility = View.GONE
        } else {
            binding.itemAdditionalInfo.visibility = View.VISIBLE
            val sb = StringBuilder()
            sb.append("Expiration: ")
            sb.append(expirationPeriod.value)
            sb.append(' ')
            sb.append(expirationPeriod.type.name.toLowerCase())
            if (expirationPeriod.value != 1) {
                sb.append('s')
            }
            binding.itemAdditionalInfo.text = sb
        }
    }

}

class ViVirtualPriceViewHolder(
    private val vmBalance: VmBalance,
    private val purchaseListener: PurchaseListener,
    view: View
) : RecyclerView.ViewHolder(view) {

    private val binding: ItemViVirtualPriceBinding = ItemViVirtualPriceBinding.bind(view)

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
            binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal())
            binding.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
            binding.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.itemOldPrice.visibility = View.VISIBLE
            binding.itemSaleLabel.visibility = View.VISIBLE
        }
    }

    private fun bindExpirationPeriod(expirationPeriod: ExpirationPeriod?) {
        if (expirationPeriod == null) {
            binding.itemAdditionalInfo.visibility = View.GONE
        } else {
            binding.itemAdditionalInfo.visibility = View.VISIBLE
            val sb = StringBuilder()
            sb.append("Expiration: ")
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
            v.isEnabled = false
            XStore.createOrderByVirtualCurrency(item.sku, virtualPrice.sku, object : XStoreCallback<CreateOrderByVirtualCurrencyResponse?>() {
                override fun onSuccess(response: CreateOrderByVirtualCurrencyResponse?) {
                    vmBalance.updateVirtualBalance()
                    purchaseListener.showMessage("Purchased by Virtual currency")
                    v.isEnabled = true
                }

                override fun onFailure(errorMessage: String) {
                    purchaseListener.showMessage(errorMessage)
                    v.isEnabled = true
                }
            })
        }
    }
}
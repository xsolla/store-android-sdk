package com.xsolla.android.customauth.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.ui.vm.VmPurchase
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.customauth.BuildConfig
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.ItemViRealPriceBinding
import com.xsolla.android.customauth.databinding.ItemViVirtualPriceBinding
import com.xsolla.android.customauth.ui.store.PurchaseListener
import com.xsolla.android.customauth.viewmodels.VmBalance
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.CreateOrderByVirtualCurrencyCallback
import com.xsolla.android.store.entity.response.common.VirtualPrice
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse

class VcAdapter(
    private val items: List<VirtualCurrencyPackageResponse.Item>,
    private val vmPurchase: VmPurchase,
    private val vmBalance: VmBalance,
    private val purchaseListener: PurchaseListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
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
            REAL_PRICE -> VcRealPriceViewHolder(
                vmPurchase,
                purchaseListener,
                inflater.inflate(R.layout.item_vi_real_price, parent, false)
            )
            else -> VcVirtualPriceViewHolder(
                vmBalance,
                purchaseListener,
                inflater.inflate(R.layout.item_vi_virtual_price, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder.itemViewType) {
            REAL_PRICE -> (holder as VcRealPriceViewHolder).bind(item)
            VIRTUAL_PRICE -> (holder as VcVirtualPriceViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

}

class VcRealPriceViewHolder(
    private val vmPurchase: VmPurchase,
    private val purchaseListener: PurchaseListener,
    view: View
) : RecyclerView.ViewHolder(view) {

    private val binding: ItemViRealPriceBinding = ItemViRealPriceBinding.bind(view)

    fun bind(item: VirtualCurrencyPackageResponse.Item) {
        Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
        binding.itemName.text = item.name
        binding.itemAdditionalInfo.text = item.description
        bindItemPrice(item)
    }

    private fun bindItemPrice(item: VirtualCurrencyPackageResponse.Item) {
        val price = item.price
        if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal()) {
            binding.itemPrice.text =
                AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
            binding.itemOldPrice.visibility = View.INVISIBLE
            binding.itemSaleLabel.visibility = View.INVISIBLE
        } else {
            binding.itemPrice.text =
                AmountUtils.prettyPrint(price.getAmountDecimal(), price.currency)
            binding.itemOldPrice.text =
                AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
            binding.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.itemOldPrice.visibility = View.VISIBLE
            binding.itemSaleLabel.visibility = View.VISIBLE
        }

        binding.addToCartButton.setOnClickListener { view ->
            view.isEnabled = false
            vmPurchase.startPurchase(BuildConfig.IS_SANDBOX, item.sku!!, 1) {
                view.isEnabled = true
            }
        }
    }
}

class VcVirtualPriceViewHolder(
    private val vmBalance: VmBalance,
    private val purchaseListener: PurchaseListener,
    view: View
) : RecyclerView.ViewHolder(view) {

    private val binding: ItemViVirtualPriceBinding = ItemViVirtualPriceBinding.bind(view)

    fun bind(item: VirtualCurrencyPackageResponse.Item) {
        val price = item.virtualPrices[0]
        Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
        binding.itemName.text = item.name
        binding.itemAdditionalInfo.text = item.description
        bindItemPrice(price)
        initBuyButton(item, price)
    }

    private fun bindItemPrice(price: VirtualPrice) {
        Glide.with(itemView.context).load(price.imageUrl).into(binding.itemVirtualPriceIcon)

        if (price.getAmountDecimal() == price.getAmountWithoutDiscountDecimal() || price.calculatedPrice?.amountWithoutDiscount == null) {
            binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal())
            binding.itemOldPrice.visibility = View.INVISIBLE
            binding.itemSaleLabel.visibility = View.INVISIBLE
        } else {
            binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal())
            binding.itemOldPrice.text =
                AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal())
            binding.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.itemOldPrice.visibility = View.VISIBLE
            binding.itemSaleLabel.visibility = View.VISIBLE
        }
    }

    private fun initBuyButton(
        item: VirtualCurrencyPackageResponse.Item,
        virtualPrice: VirtualPrice
    ) {
        binding.buyButton.setOnClickListener { v ->
            v.isEnabled = false
            XStore.createOrderByVirtualCurrency(object : CreateOrderByVirtualCurrencyCallback {

                override fun onSuccess(response: CreateOrderByVirtualCurrencyResponse) {
                    vmBalance.updateVirtualBalance()
                    purchaseListener.showMessage("Purchased by Virtual currency")
                    v.isEnabled = true
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    purchaseListener.showMessage(
                        errorMessage ?: throwable?.javaClass?.name ?: "Error"
                    )
                    v.isEnabled = true
                }
            }, item.sku!!, virtualPrice.sku!!)
        }
    }

}
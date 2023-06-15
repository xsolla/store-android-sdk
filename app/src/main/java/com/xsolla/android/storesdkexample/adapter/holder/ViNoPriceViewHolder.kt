package com.xsolla.android.storesdkexample.adapter.holder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.databinding.ItemViNoPriceBinding
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.CreateFreeOrderCallback
import com.xsolla.android.store.entity.response.common.ExpirationPeriod
import com.xsolla.android.store.entity.response.payment.CreateFreeOrderResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.store.ViFragmentDirections
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity
import java.util.*

class ViNoPriceViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    private val purchaseListener: PurchaseListener
) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_vi_no_price, parent, false)) {
    private val binding = ItemViNoPriceBinding.bind(itemView)

    fun bind(item: VirtualItemUiEntity) {
        Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
        binding.itemName.text = item.name
        bindPurchasedPlaceholderIfNeed(item)
        initBuyButton(item)
        bindExpirationPeriod(item.inventoryOption?.expirationPeriod)
        bindBundlePlaceholder(item)
    }

    private fun bindBundlePlaceholder(item: VirtualItemUiEntity) {
        if (item.sku == "premium_pack" || item.sku == "starter_pack" ||
            item.sku == "lootbox_pack_1" || item.sku == "lootbox_pack_2"
        ) {
            binding.preview.visibility = View.VISIBLE
            binding.preview.setOnClickListener {
                it.findNavController()
                    .navigate(ViFragmentDirections.actionNavViToBundleFragment(item))
            }
        } else {
            binding.preview.visibility = View.INVISIBLE
        }
    }

    private fun bindPurchasedPlaceholderIfNeed(item: VirtualItemUiEntity) {
        if (item.hasInInventory) {
            if (item.inventoryOption?.consumable == null && item.inventoryOption?.expirationPeriod == null) {
                binding.purchasedPlaceholder.isVisible = true
                binding.buyForFreeButton.isVisible = false
                binding.itemSaleLabel.isVisible = false
            } else {
                binding.purchasedPlaceholder.isVisible = false
                binding.buyForFreeButton.isVisible = true
                binding.itemSaleLabel.isVisible = true
            }
        } else {
            binding.purchasedPlaceholder.isVisible = false
            binding.buyForFreeButton.isVisible = true
            binding.itemSaleLabel.isVisible = true
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
            sb.append(expirationPeriod.type.name.lowercase(Locale.getDefault()))
            if (expirationPeriod.value != 1) {
                sb.append('s')
            }
            binding.itemAdditionalInfo.text = sb
        }
    }

    private fun initBuyButton(item: VirtualItemUiEntity) {
        binding.buyForFreeButton.setOnClickListener { v ->
            v.isEnabled = false
            XStore.createOrderWithSpecifiedFreeItem(object : CreateFreeOrderCallback {
                override fun onSuccess(response: CreateFreeOrderResponse) {
                    purchaseListener.showMessage("Purchased free item")
                    v.isEnabled = true
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    purchaseListener.showMessage(errorMessage!!)
                    v.isEnabled = true
                }
            }, item.sku!!, 1)
        }
    }

}

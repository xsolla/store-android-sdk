package com.xsolla.android.storesdkexample.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.databinding.ItemCartBinding
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.common.ExpirationPeriod
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.CartChangeListener
import com.xsolla.android.storesdkexample.ui.vm.VmCart

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
        private val binding = ItemCartBinding.bind(itemView)

        fun bind(item: CartResponse.Item) {
            Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
            binding.itemName.text = item.name

            bindCounter(item)

            item.price?.let { bindItemPrice(item) }

            item.inventoryOption?.expirationPeriod?.let { bindExpirationPeriod(it) }
        }

        private fun bindItemPrice(item: CartResponse.Item) {
            val price = item.price
            if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal()) {
                binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!, price.currency!!)
                binding.itemOldPrice.visibility = View.INVISIBLE
                binding.itemSaleLabel.visibility = View.INVISIBLE }
            else {
                binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!, price.currency!!)
                binding.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal()!!)
                binding.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.itemOldPrice.visibility = View.VISIBLE
                binding.itemSaleLabel.visibility = View.VISIBLE
            }
        }

        private fun bindExpirationPeriod(expirationPeriod: ExpirationPeriod) {
            binding.itemExpiration.visibility = View.VISIBLE
            val sb = StringBuilder()
            sb.append("Expiration in ")
            sb.append(expirationPeriod.value)
            sb.append(' ')
            sb.append(expirationPeriod.type.name.toLowerCase())
            if (expirationPeriod.value != 1) {
                sb.append('s')
            }
            binding.itemExpiration.text = sb
        }

        private fun bindCounter(item: CartResponse.Item) {
            binding.itemTextCount.text = item.quantity.toString()
            if (item.quantity > 1) {
                binding.itemButtonMinus.setImageResource(R.drawable.ic_cart_minus)
            } else {
                binding.itemButtonMinus.setImageResource(R.drawable.ic_cart_delete)
            }

            binding.priceLayout.isGone = item.price == null
            binding.itemButtonMinus.isGone = item.price == null
            binding.itemButtonPlus.isGone = item.price == null

            if (item.price == null) {
                binding.itemExpiration.isVisible = true
                binding.itemExpiration.text = itemView.resources.getString(R.string.promocode_bonus_item_placeholder)
            }

            binding.itemButtonMinus.setOnClickListener {
                vmCart.changeItemCount(item, -1) { result -> cartChangeListener.onChange(result) }
            }
            binding.itemButtonPlus.setOnClickListener {
                vmCart.changeItemCount(item, 1) { result -> cartChangeListener.onChange(result) }
            }
        }

    }

}
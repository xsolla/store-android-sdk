package com.xsolla.android.customauth.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.ItemCartBinding
import com.xsolla.android.customauth.ui.store.CartChangeListener
import com.xsolla.android.customauth.viewmodels.VmCart
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.common.ExpirationPeriod

class CartAdapter(
    val items: MutableList<CartResponse.Item>,
    private val vmCart: VmCart,
    private val cartChangeListener: CartChangeListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding: ItemCartBinding = ItemCartBinding.bind(view)

        fun bind(item: CartResponse.Item) {
            Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
            binding.itemName.text = item.name

            bindCounter(item)


            item.price?.let { bindItemPrice(item) }

            item.inventoryOption?.expirationPeriod?.let { binExpirationPeriod(it) }
        }

        private fun bindItemPrice(item: CartResponse.Item) {
            val price = item.price
            if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal()) {
                binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!, price.currency!!)
                binding.itemOldPrice.visibility = View.INVISIBLE
                binding.itemSaleLabel.visibility = View.INVISIBLE
            } else {
                binding.itemPrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!, price.currency!!)
                binding.itemOldPrice.text = AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal()!!)
                binding.itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.itemOldPrice.visibility = View.VISIBLE
                binding.itemSaleLabel.visibility = View.VISIBLE
            }
        }

        private fun binExpirationPeriod(expirationPeriod: ExpirationPeriod) {
            binding.itemExpiration.visibility = View.VISIBLE
            val sb = StringBuilder()
            sb.append("Expiration: ")
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
            binding.itemButtonMinus.setOnClickListener {
                vmCart.changeItemCount(item, -1) { result -> cartChangeListener.onChange(result) }
            }
            binding.itemButtonPlus.setOnClickListener {
                vmCart.changeItemCount(item, 1) { result -> cartChangeListener.onChange(result) }
            }
        }
    }
}
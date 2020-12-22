package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.store.entity.response.items.RedeemCouponResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.databinding.ItemReceivedFromCouponBinding

class RedeemCouponItemsAdapter(
    private val items: List<RedeemCouponResponse.Item>
) : RecyclerView.Adapter<RedeemCouponItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_from_coupon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemReceivedFromCouponBinding.bind(view)

        fun bind(item: RedeemCouponResponse.Item) {
            Glide.with(itemView.context).load(item.imageUrl).into(binding.picture)
            binding.name.text = item.name
            binding.quantity.text = item.quantity.toString()

            val expirationPeriod = item.inventoryOption?.expirationPeriod
            if (expirationPeriod != null) {
                binding.expirationPeriod.isVisible = true
                binding.expirationPeriod.text = itemView.context.getString(R.string.coupon_received_item_exp_period, expirationPeriod.value, expirationPeriod.type.name.toLowerCase())
            } else {
                binding.expirationPeriod.isGone = true
            }

            configureConstraints(binding.expirationPeriod.isVisible)
        }

        private fun configureConstraints(hasExpPeriod: Boolean) {
            val constraintLayout = itemView as ConstraintLayout
            val constraintSet = ConstraintSet().apply { clone(constraintLayout) }

            if (hasExpPeriod) {
                constraintSet.connect(binding.divider.id, ConstraintSet.TOP, binding.expirationPeriod.id, ConstraintSet.BOTTOM)
            } else {
                constraintSet.connect(binding.divider.id, ConstraintSet.TOP, binding.picture.id, ConstraintSet.BOTTOM)
            }

            constraintSet.applyTo(constraintLayout)
        }
    }
}
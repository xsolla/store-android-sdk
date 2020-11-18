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
import kotlinx.android.synthetic.main.item_received_from_coupon.view.divider
import kotlinx.android.synthetic.main.item_received_from_coupon.view.expirationPeriod
import kotlinx.android.synthetic.main.item_received_from_coupon.view.name
import kotlinx.android.synthetic.main.item_received_from_coupon.view.picture
import kotlinx.android.synthetic.main.item_received_from_coupon.view.quantity

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
        fun bind(item: RedeemCouponResponse.Item) {
            Glide.with(itemView.context).load(item.imageUrl).into(itemView.picture)
            itemView.name.text = item.name
            itemView.quantity.text = item.quantity.toString()

            val expirationPeriod = item.inventoryOption?.expirationPeriod
            if (expirationPeriod != null) {
                itemView.expirationPeriod.isVisible = true
                itemView.expirationPeriod.text = itemView.context.getString(R.string.coupon_received_item_exp_period, expirationPeriod.value, expirationPeriod.type.name.toLowerCase())
            } else {
                itemView.expirationPeriod.isGone = true
            }

            configureConstraints(itemView.expirationPeriod.isVisible)
        }

        private fun configureConstraints(hasExpPeriod: Boolean) {
            val constraintLayout = itemView as ConstraintLayout
            val constraintSet = ConstraintSet().apply { clone(constraintLayout) }

            if (hasExpPeriod) {
                constraintSet.connect(itemView.divider.id, ConstraintSet.TOP, itemView.expirationPeriod.id, ConstraintSet.BOTTOM)
            } else {
                constraintSet.connect(itemView.divider.id, ConstraintSet.TOP, itemView.picture.id, ConstraintSet.BOTTOM)
            }

            constraintSet.applyTo(constraintLayout)
        }
    }
}
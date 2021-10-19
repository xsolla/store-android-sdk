package com.xsolla.android.customauth.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.ui.vm.VmPurchase
import com.xsolla.android.customauth.BuildConfig
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.ItemInventoryBinding
import com.xsolla.android.customauth.ui.store.ConsumeListener
import com.xsolla.android.customauth.ui.store.PurchaseListener
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import java.text.SimpleDateFormat
import java.util.*

class InventoryAdapter(
    var items: List<InventoryResponse.Item>,
    private val consumeListener: ConsumeListener,
    private val purchaseListener: PurchaseListener,
    private val vmPurchase: VmPurchase
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    private var subscriptions: List<SubscriptionsResponse.Item>? = null

    fun setSubscriptions(subscriptions: List<SubscriptionsResponse.Item>?) {
        this.subscriptions = subscriptions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size


    inner class ViewHolder(
        inflater: LayoutInflater,
        val parent: ViewGroup
    ) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_inventory, parent, false)) {
        private val binding = ItemInventoryBinding.bind(itemView)

        fun bind(item: InventoryResponse.Item) {
            Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
            binding.itemName.text = item.name
            binding.itemQuantity.text = item.quantity.toString()
            if (item.virtualItemType == InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION) {
                binding.itemQuantity.visibility = View.INVISIBLE
            } else {
                binding.itemQuantity.visibility = View.VISIBLE
            }

            val subscription =
                if (item.virtualItemType == InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION) {
                    subscriptions?.find { it.sku == item.sku }
                } else {
                    null
                }

            binding.itemExpiration.text = getExpirationText(subscription)

            binding.consumeButton.visibility =
                if (item.remainingUses == null || item.remainingUses == 0L) View.INVISIBLE else View.VISIBLE
            binding.consumeButton.setOnClickListener { consumeListener.onConsume(item) }

            binding.buyAgainButton.isVisible =
                subscription != null && subscription.status != SubscriptionsResponse.Item.Status.ACTIVE
            binding.buyAgainButton.setOnClickListener { view ->
                view.isEnabled = false
                vmPurchase.startPurchase(BuildConfig.IS_SANDBOX, item.sku!!, 1) {
                    view.isEnabled = true
                }
            }
        }

        private fun getExpirationText(sub: SubscriptionsResponse.Item?): String? {
            if (sub == null) return null
            return if (sub.status == SubscriptionsResponse.Item.Status.ACTIVE) {
                val date = Date(sub.expiredAt?.times(1000)!!)  //date *1000
                val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
                val formattedDate = sdf.format(date)
                "Active until: $formattedDate"
            } else {
                "Expired"
            }
        }

    }
}
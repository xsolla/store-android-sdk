package com.xsolla.android.customauth.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.ItemInventoryBinding
import com.xsolla.android.customauth.ui.store.PurchaseListener
import com.xsolla.android.customauth.viewmodels.VmCart
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback

class InventoryAdapter(
        private var subscriptions: List<SubscriptionsResponse.Item>? = null,
        private val consumeListener: ConsumeListener,
        private val purchaseListener: PurchaseListener,
        private val vmCart: VmCart
) : ListAdapter<InventoryResponse.Item, InventoryViewHolder>(InventoryDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return InventoryViewHolder(inflater, parent, purchaseListener, vmCart)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(subscriptions, getItem(position), consumeListener)
    }

    fun setSubscriptions(subscriptions: List<SubscriptionsResponse.Item>) {
        this.subscriptions = subscriptions
        notifyDataSetChanged()
    }
}

class InventoryViewHolder(inflater: LayoutInflater,
                          parent: ViewGroup,
                          private val purchaseListener: PurchaseListener,
                          private val vmCart: VmCart) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_inventory, parent, false)) {

    val binding = ItemInventoryBinding.bind(itemView)


    fun bind(subscriptions: List<SubscriptionsResponse.Item>?, item: InventoryResponse.Item, consumeListener: ConsumeListener) {
        Glide.with(itemView).load(item.imageUrl).into(binding.itemIcon)
        binding.itemName.text = item.name
        binding.itemQuantity.text = item.quantity.toString()
        binding.itemQuantity.isInvisible = item.virtualItemType == InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION
        binding.itemExpiration.text = getExpirationText(subscriptions, item)
        binding.consumeButton.isInvisible = item.remainingUses == 0L
        binding.consumeButton.setOnClickListener { consumeListener.onConsume(item) }
    }

    private fun getExpirationText(subscriptions: List<SubscriptionsResponse.Item>?, item: InventoryResponse.Item): String? {
        if (item.virtualItemType != InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION) return null

        subscriptions?.find { it.sku == item.sku }?.let {
            return if (it.status == SubscriptionsResponse.Item.Status.ACTIVE) {
                binding.buyAgainButton.visibility = View.GONE
                val date = java.util.Date(it.expiredAt * 1000)
                val sdf = java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", java.util.Locale.US)
                val formattedDate = sdf.format(date)
                "Active until: $formattedDate"
            } else {
                buyAgainExpiredSubscription(item)

                "Expired"
            }
        }

        return null
    }

    private fun buyAgainExpiredSubscription(item: InventoryResponse.Item) {
        binding.buyAgainButton.visibility = View.VISIBLE
        binding.buyAgainButton.setOnClickListener { view ->
            val cartContent = vmCart.cartContent.value
            val quantity = cartContent?.find { item1 -> item1.sku == item.sku }?.quantity
                    ?: 0
            // check if there is item in cart, if not - set quantity to 0
            XStore.updateItemFromCurrentCart(item.sku, quantity + 1, object : XStoreCallback<Void>() {
                override fun onSuccess(response: Void?) {
                    vmCart.updateCart()
                    binding.buyAgainButton.visibility = View.GONE

                }

                override fun onFailure(errorMessage: String?) {
                    purchaseListener.onFailure(errorMessage!!)
                }

            })
        }
    }
}

class InventoryDiffUtilCallback : DiffUtil.ItemCallback<InventoryResponse.Item>() {
    override fun areItemsTheSame(oldItem: InventoryResponse.Item, newItem: InventoryResponse.Item): Boolean {
        return oldItem.sku == newItem.sku
    }

    override fun areContentsTheSame(oldItem: InventoryResponse.Item, newItem: InventoryResponse.Item): Boolean {
        return oldItem == newItem
    }
}

interface ConsumeListener {
    fun onConsume(item: InventoryResponse.Item)
    fun onSuccess()
    fun onFailure(errorMessage: String)
}
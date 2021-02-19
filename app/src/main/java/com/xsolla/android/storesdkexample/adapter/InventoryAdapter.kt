package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.ViewUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.databinding.ItemInventoryBinding
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.ConsumeListener
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.util.ViewUtils
import java.text.SimpleDateFormat
import java.util.*

class InventoryAdapter(
        var items: List<InventoryResponse.Item>,
        private val consumeListener: ConsumeListener,
        private val purchaseListener: PurchaseListener,
        private val vmCart: VmCart
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    private var subscriptions: List<SubscriptionsResponse.Item>? = null

    fun setSubscriptions(subscriptions: List<SubscriptionsResponse.Item>?) {
        this.subscriptions = subscriptions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent, vmCart)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size


    inner class ViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup,
            private val vmCart: VmCart
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

            binding.itemExpiration.text = getExpirationText(item)

            binding.consumeButton.visibility = if (item.remainingUses == 0L) View.INVISIBLE else View.VISIBLE
            binding.consumeButton.setOnClickListener { consumeListener.onConsume(item) }
        }

        private fun getExpirationText(item: InventoryResponse.Item): String? {
            if (item.virtualItemType != InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION) return null

            subscriptions?.find { it.sku == item.sku }?.let {
                return if (it.status == SubscriptionsResponse.Item.Status.ACTIVE) {
                    binding.buyAgainButton.visibility = View.GONE
                    val date = Date(it.expiredAt * 1000)
                    val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
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

                ViewUtils.disable(view)
                val cartContent = vmCart.cartContent.value
                val quantity = cartContent?.find { item1 -> item1.sku == item.sku }?.quantity
                        ?: 0
                // check if there is item in cart, if not - set quantity to 0
                XStore.updateItemFromCurrentCart(item.sku, quantity + 1, object : XStoreCallback<Void>() {
                    override fun onSuccess(response: Void?) {
                        vmCart.updateCart()
                        ViewUtils.enable(view)
                        binding.buyAgainButton.visibility = View.GONE
                        //enable button after process are done
                    }

                    override fun onFailure(errorMessage: String?) {
                        purchaseListener.onFailure(errorMessage!!)
                        ViewUtils.enable(view)
                    }


                })
            }
        }

    }
}
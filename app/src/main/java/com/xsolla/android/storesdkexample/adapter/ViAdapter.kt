package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.storesdkexample.adapter.holder.ViRealPriceViewHolder
import com.xsolla.android.storesdkexample.adapter.holder.ViVirtualPriceViewHolder
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmCart

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
            REAL_PRICE -> ViRealPriceViewHolder(inflater, parent, vmCart, purchaseListener)
            else -> ViVirtualPriceViewHolder(inflater, parent, vmBalance, purchaseListener)
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
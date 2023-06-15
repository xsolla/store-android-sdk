package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.appcore.ui.vm.VmPurchase
import com.xsolla.android.googleplay.StoreUtils
import com.xsolla.android.storesdkexample.App
import com.xsolla.android.storesdkexample.adapter.holder.ViGooglePlayViewHolder
import com.xsolla.android.storesdkexample.adapter.holder.ViNoPriceViewHolder
import com.xsolla.android.storesdkexample.adapter.holder.ViRealPriceViewHolder
import com.xsolla.android.storesdkexample.adapter.holder.ViVirtualPriceViewHolder
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmGooglePlay

class ViAdapter(
    private val items: List<VirtualItemUiEntity>,
    private val vmPurchase: VmPurchase,
    private val vmBalance: VmBalance,
    private val vmGooglePay: VmGooglePlay,
    private val purchaseListener: PurchaseListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val REAL_PRICE = 0
        private const val VIRTUAL_PRICE = 1
        private const val GOOGLE_PLAY = 2
        private const val FREE_ITEM = 3
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item.virtualPrices.isNotEmpty()) {
            return VIRTUAL_PRICE
        }
        if (item.isFree) {
            return FREE_ITEM
        }
        if (StoreUtils.isAppInstalledFromGooglePlay(App.applicationContext())) {
            return GOOGLE_PLAY
        }
        return REAL_PRICE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            GOOGLE_PLAY -> ViGooglePlayViewHolder(inflater, parent, vmGooglePay)
            REAL_PRICE -> ViRealPriceViewHolder(inflater, parent, vmPurchase, purchaseListener)
            FREE_ITEM -> ViNoPriceViewHolder(inflater, parent, purchaseListener)
            else -> ViVirtualPriceViewHolder(inflater, parent, vmBalance, purchaseListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder.itemViewType) {
            GOOGLE_PLAY -> (holder as ViGooglePlayViewHolder).bind(item)
            REAL_PRICE -> (holder as ViRealPriceViewHolder).bind(item)
            FREE_ITEM -> (holder as ViNoPriceViewHolder).bind(item)
            VIRTUAL_PRICE -> (holder as ViVirtualPriceViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size
}
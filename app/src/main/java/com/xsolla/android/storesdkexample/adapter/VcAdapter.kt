package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.appcore.ui.vm.VmPurchase
import com.xsolla.android.googleplay.StoreUtils
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.storesdkexample.App
import com.xsolla.android.storesdkexample.adapter.holder.VcGooglePlayViewHolder
import com.xsolla.android.storesdkexample.adapter.holder.VcRealPriceViewHolder
import com.xsolla.android.storesdkexample.adapter.holder.VcVirtualPriceViewHolder
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmGooglePlay

class VcAdapter(
    private val items: List<VirtualCurrencyPackageResponse.Item>,
    private val vmPurchase: VmPurchase,
    private val vmBalance: VmBalance,
    private val vmGooglePlay: VmGooglePlay,
    private val purchaseListener: PurchaseListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val REAL_PRICE = 0
        private const val VIRTUAL_PRICE = 1
        private const val GOOGLE_PLAY = 2
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item.virtualPrices.isNotEmpty()) {
            return VIRTUAL_PRICE
        }
        if (StoreUtils.isAppInstalledFromGooglePlay(App.applicationContext())) {
            return GOOGLE_PLAY
        }
        return REAL_PRICE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            GOOGLE_PLAY -> VcGooglePlayViewHolder(inflater, parent, vmGooglePlay)
            REAL_PRICE -> VcRealPriceViewHolder(inflater, parent, vmPurchase, purchaseListener)
            else -> VcVirtualPriceViewHolder(inflater, parent, vmBalance, purchaseListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder.itemViewType) {
            GOOGLE_PLAY -> (holder as VcGooglePlayViewHolder).bind(item)
            REAL_PRICE -> (holder as VcRealPriceViewHolder).bind(item)
            VIRTUAL_PRICE -> (holder as VcVirtualPriceViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

}
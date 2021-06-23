package com.xsolla.android.storesdkexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xsolla.android.store.entity.response.bundle.BundleContent
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.storesdkexample.adapter.holder.BundleViewHolder
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmCart
class BundleAdapter(val items: MutableList<BundleContent>) : RecyclerView.Adapter<BundleViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BundleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BundleViewHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: BundleViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

}
package com.xsolla.android.storesdkexample.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.appcore.databinding.ItemBundleBinding
import com.xsolla.android.store.entity.response.bundle.BundleContent
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.store.VirtualItemUiEntity
import com.xsolla.android.storesdkexample.ui.vm.VmBalance

class BundleViewHolder(inflater: LayoutInflater,
                       parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_bundle, parent, false)) {

    private val binding = ItemBundleBinding.bind(itemView)

    fun bind(item : BundleContent) {
        Glide.with(itemView).load(item.imageUrl).into(binding.ivBundleItem)
        binding.tvBundleItemName.text = item.name
        binding.tvBundleItemDescription.text = item.description
        binding.tvBundleItemAmount.text = item.quantity.toString()
    }
}
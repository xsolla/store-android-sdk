package com.xsolla.android.customauth.ui.store

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentInventoryBinding
import com.xsolla.android.customauth.ui.BaseFragment
import com.xsolla.android.customauth.ui.adapter.ConsumeListener
import com.xsolla.android.customauth.ui.adapter.InventoryAdapter
import com.xsolla.android.customauth.viewmodels.VmInventory
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.inventory.InventoryResponse

class InventoryFragment : BaseFragment(), ConsumeListener {
    private val binding: FragmentInventoryBinding by viewBinding()
    private val viewModel: VmInventory by viewModels()

    private lateinit var adapter: InventoryAdapter

    override fun getLayout() = R.layout.fragment_inventory

    override fun initUI() {
        adapter = InventoryAdapter(listOf(), this)
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            ContextCompat.getDrawable(requireContext(), R.drawable.item_divider)?.let { setDrawable(it) }
        })
        binding.goToStoreButton.setOnClickListener { findNavController().navigate(R.id.nav_vi) }

        viewModel.inventory.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.subscriptions.observe(viewLifecycleOwner) { adapter.setSubscriptions(it) }
        viewModel.getItems { showSnack(it) }
        viewModel.getSubscriptions { showSnack(it) }
    }

    override fun onConsume(item: InventoryResponse.Item) {
        consume(item)
    }

    override fun onSuccess() {
        showSnack("Item Consumed")
    }

    override fun onFailure(errorMessage: String) {
        showSnack(errorMessage)
    }

    private fun consume(item: InventoryResponse.Item) {
        XStore.consumeItem(item.sku, 1, null, object : XStoreCallback<Void>() {
            override fun onSuccess(response: Void?) {
                XStore.getInventory(object : XStoreCallback<InventoryResponse>() {
                    override fun onSuccess(response: InventoryResponse) {
                        val items = response.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                        adapter.submitList(items.toList())
                        showSnack("Item consumed")
                    }

                    override fun onFailure(errorMessage: String) {
                        showSnack(errorMessage)
                    }
                })
            }

            override fun onFailure(errorMessage: String) {
                showSnack(errorMessage)
            }
        })
    }
}
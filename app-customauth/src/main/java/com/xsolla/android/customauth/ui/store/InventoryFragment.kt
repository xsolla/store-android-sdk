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
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse

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
        XInventory.consumeItem(item.sku!!, 1, null, object : ConsumeItemCallback {
            override fun onSuccess() {
                XInventory.getInventory(object : GetInventoryCallback {
                    override fun onSuccess(data: InventoryResponse) {
                        val items = data.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                        adapter.submitList(items.toList())
                        showSnack("Item consumed")
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                    }
                })
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }
}
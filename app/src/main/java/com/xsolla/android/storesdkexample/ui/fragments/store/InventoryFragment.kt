package com.xsolla.android.storesdkexample.ui.fragments.store

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentInventoryBinding
import com.xsolla.android.appcore.ui.vm.VmPurchase
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.InventoryAdapter
import com.xsolla.android.storesdkexample.listener.ConsumeListener
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmGooglePlay
import com.xsolla.android.storesdkexample.ui.vm.VmInventory

class InventoryFragment : BaseFragment(), ConsumeListener, PurchaseListener {
    private val binding: FragmentInventoryBinding by viewBinding()

    private val viewModel: VmInventory by activityViewModels()
    private val vmBalance: VmBalance by activityViewModels()
    private val vmPurchase: VmPurchase by activityViewModels()
    private val vmGooglePlay: VmGooglePlay by activityViewModels()
    private lateinit var inventoryAdapter: InventoryAdapter

    override fun getLayout() = R.layout.fragment_inventory

    override fun initUI() {
        with(binding.recycler) {
            val linearLayoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    linearLayoutManager.orientation
                ).apply {
                    ContextCompat.getDrawable(context, R.drawable.item_divider)
                        ?.let { setDrawable(it) }
                })
            layoutManager = linearLayoutManager
        }
        binding.refreshButton.setOnClickListener { loadInventory() }
        binding.goToStoreButton.setOnClickListener { findNavController().navigate(R.id.nav_vi) }

        inventoryAdapter = InventoryAdapter(listOf(), this, this, vmPurchase, vmGooglePlay)
        binding.recycler.adapter = inventoryAdapter

        viewModel.inventory.observe(viewLifecycleOwner) {
            inventoryAdapter.items = it
            inventoryAdapter.notifyDataSetChanged()
            setupPlaceholderVisibility()
        }
        viewModel.timeLimitedItems.observe(viewLifecycleOwner) {
            inventoryAdapter.setTimeLimitedItems(it)
            setupPlaceholderVisibility()
        }

        loadInventory()
    }

    private fun loadInventory() {
        viewModel.getItems { showSnack(it) }
        viewModel.getTimeLimitedItems { showSnack(it) }
        vmBalance.updateVirtualBalance()
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

    override fun showMessage(message: String) {
        showSnack(message)
    }

    private fun consume(item: InventoryResponse.Item) {
        XInventory.consumeItem(item.sku!!, 1, null, object : ConsumeItemCallback {
            override fun onSuccess() {
                XInventory.getInventory(object : GetInventoryCallback {
                    override fun onSuccess(data: InventoryResponse) {
                        inventoryAdapter.items =
                            data.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                        inventoryAdapter.notifyDataSetChanged()
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

    private fun setupPlaceholderVisibility() {
        binding.noItemsPlaceholder.isVisible = viewModel.inventorySize == 0
        binding.recycler.isVisible = viewModel.inventorySize != 0
    }
}
package com.xsolla.android.storesdkexample.ui.fragments.store

import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.inventory.InventoryResponse
import com.xsolla.android.store.entity.response.inventory.SubscriptionsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.InventoryAdapter
import com.xsolla.android.storesdkexample.listener.ConsumeListener
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmInventory
import kotlinx.android.synthetic.main.fragment_inventory.goToStoreButton
import kotlinx.android.synthetic.main.fragment_inventory.recycler

class InventoryFragment : BaseFragment(), ConsumeListener {

    private val viewModel: VmInventory by activityViewModels()
    private lateinit var inventoryAdapter: InventoryAdapter

    override fun getLayout() = R.layout.fragment_inventory

    override fun initUI() {
        with(recycler) {
            val linearLayoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation).apply {
                ContextCompat.getDrawable(context, R.drawable.item_divider)?.let { setDrawable(it) }
            })
            layoutManager = linearLayoutManager
            goToStoreButton.setOnClickListener { findNavController().navigate(R.id.nav_vi) }
        }

        inventoryAdapter = InventoryAdapter(listOf(), this)
        recycler.adapter = inventoryAdapter

        viewModel.inventory.observe(viewLifecycleOwner) {
            inventoryAdapter.items = it
            inventoryAdapter.notifyDataSetChanged()
        }
        viewModel.subscriptions.observe(viewLifecycleOwner) {
            inventoryAdapter.setSubscriptions(it)
        }

        getItems()
        getSubscriptions()
    }

    private fun getItems() {
        XStore.getInventory(object : XStoreCallback<InventoryResponse>() {
            override fun onSuccess(response: InventoryResponse) {
                val virtualItems = response.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                viewModel.inventory.value = virtualItems
            }

            override fun onFailure(errorMessage: String) {
                showSnack(errorMessage)
            }
        })
    }

    private fun getSubscriptions() {
        XStore.getSubscriptions(object : XStoreCallback<SubscriptionsResponse>() {
            override fun onSuccess(response: SubscriptionsResponse) {
                viewModel.subscriptions.value = response.items
            }

            override fun onFailure(errorMessage: String) {
                showSnack(errorMessage)
            }
        })
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
                        inventoryAdapter.items = response.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                        inventoryAdapter.notifyDataSetChanged()
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
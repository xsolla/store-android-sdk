package com.xsolla.android.inventorysdkexample.ui.fragments.store

import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentInventoryBinding
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetSubscriptionsCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.adapter.InventoryAdapter
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.inventorysdkexample.ui.vm.VmInventory
import com.xsolla.android.inventorysdkexample.util.extensions.openInBrowser
import com.xsolla.android.login.XLogin

class InventoryFragment : BaseFragment(), ConsumeListener {

    private val binding: FragmentInventoryBinding by viewBinding()

    private val viewModel: VmInventory by activityViewModels()
    private lateinit var inventoryAdapter: InventoryAdapter

    override fun getLayout() = R.layout.fragment_inventory

    override fun initUI() {
        with(binding.recycler) {
            val linearLayoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation).apply {
                ContextCompat.getDrawable(context, R.drawable.item_divider)?.let { setDrawable(it) }
            })
            layoutManager = linearLayoutManager
            binding.goToStoreButton.setOnClickListener { openWebStore() }
        }

        inventoryAdapter = InventoryAdapter(listOf(), this)
        binding.recycler.adapter = inventoryAdapter

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

    private fun openWebStore() =
            "https://sitebuilder.xsolla.com/game/sdk-web-store-android/?token=${XLogin.token}&remember_me=false"
                    .toUri()
                    .openInBrowser(requireContext())

    private fun getItems() {
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(data: InventoryResponse) {
                val virtualItems = data.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                viewModel.inventory.value = virtualItems
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    private fun getSubscriptions() {
        XInventory.getSubscriptions(object : GetSubscriptionsCallback {
            override fun onSuccess(data: SubscriptionsResponse) {
                viewModel.subscriptions.value = data.items
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
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
        XInventory.consumeItem(item.sku!!, 1, null, object : ConsumeItemCallback {
            override fun onSuccess() {
                XInventory.getInventory(object : GetInventoryCallback {
                    override fun onSuccess(data: InventoryResponse) {
                        inventoryAdapter.items = data.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
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
}

interface ConsumeListener {
    fun onConsume(item: InventoryResponse.Item)
    fun onSuccess()
    fun onFailure(errorMessage: String)
}
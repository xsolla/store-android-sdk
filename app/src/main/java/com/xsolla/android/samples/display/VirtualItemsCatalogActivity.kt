package com.xsolla.android.samples.display

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.GetVirtualItemsCallback
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.samples.display.adapter.VirtualItemsAdapter
import com.xsolla.android.storesdkexample.R

class VirtualItemsCatalogActivity : AppCompatActivity() {
    private lateinit var itemsView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.virtual_items_catalog_sample)

        XStore.init(77640, "")

        initUI()
        loadVirtualItems()
    }

    private fun initUI() {
        itemsView = findViewById(R.id.items_catalog_recycler_view)
        itemsView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadVirtualItems() {
        XStore.getVirtualItems(object : GetVirtualItemsCallback {
            override fun onSuccess(response: VirtualItemsResponse) {
               itemsView.adapter = VirtualItemsAdapter(response.items)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}
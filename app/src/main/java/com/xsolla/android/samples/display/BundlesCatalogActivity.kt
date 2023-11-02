package com.xsolla.android.samples.display

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.GetBundleListCallback
import com.xsolla.android.store.entity.response.bundle.BundleListResponse
import com.xsolla.android.samples.display.adapter.BundlesAdapter
import com.xsolla.android.storesdkexample.R

class BundlesCatalogActivity : AppCompatActivity() {

    private lateinit var itemsView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bundles_catalog_sample)

        XStore.init(77640, "")

        initUI()
        loadBundles()
    }

    private fun initUI() {
        itemsView = findViewById(R.id.bundles_catalog_recycler_view)
        itemsView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadBundles() {
        XStore.getBundleList(object : GetBundleListCallback {
            override fun onSuccess(response: BundleListResponse) {
                itemsView.adapter = BundlesAdapter(response.items)
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
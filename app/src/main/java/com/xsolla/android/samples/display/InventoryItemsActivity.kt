package com.xsolla.android.samples.display

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.samples.display.adapter.InventoryItemsAdapter

import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.storesdkexample.R

class InventoryItemsActivity : AppCompatActivity() {

    private lateinit var itemsView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inventory_items_sample)

        initUI()
        demoLogin()
    }

    private fun initUI() {
        itemsView = findViewById(R.id.inventory_items_recycler_view)
        itemsView.layoutManager = LinearLayoutManager(this)
    }

    private fun demoLogin() {
        XLogin.login("xsolla", "xsolla", object : AuthCallback {
            override fun onSuccess() {
                XInventory.init(77640, XLogin.token!!)
                loadInventoryItems()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }

        })
    }

    private fun loadInventoryItems() {
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(response: InventoryResponse) {
                itemsView.adapter = InventoryItemsAdapter(response.items)
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
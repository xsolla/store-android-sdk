package com.xsolla.android.samples.buy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.samples.buy.adapter.BuyForVirtualAdapter
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.GetVirtualItemsCallback
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R

class BuyForVirtualActivity : AppCompatActivity() {

    private lateinit var itemsView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buy_sample)

        XStore.init(77640, "")

        initUI()
        demoLogin()
    }

    private fun initUI() {
        itemsView = findViewById(R.id.buy_recycler_view)
        itemsView.layoutManager = LinearLayoutManager(this)
    }

    private fun demoLogin() {
        XLogin.login("xsolla", "xsolla", object : AuthCallback {
            override fun onSuccess() {
                XStore.init(77640, XLogin.token!!)
                loadVirtualItems()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }

        })
    }
    private fun loadVirtualItems() {
        XStore.getVirtualItems(object : GetVirtualItemsCallback {
            override fun onSuccess(response: VirtualItemsResponse) {
                itemsView.adapter = BuyForVirtualAdapter(response.items.filter { item -> item.virtualPrices.isNotEmpty() && !item.isFree })
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
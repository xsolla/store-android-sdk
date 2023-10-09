package com.xsolla.android.samples.display

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.GetVirtualBalanceCallback
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.samples.display.adapter.CurrencyBalanceAdapter
import com.xsolla.android.storesdkexample.R

class CurrencyBalanceActivity : AppCompatActivity() {

    private lateinit var itemsView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.currency_balance_sample)

        initUI()
        demoLogin()
    }

    private fun initUI() {
        itemsView = findViewById(R.id.currency_balance_recycler_view)
        itemsView.layoutManager = LinearLayoutManager(this)
    }

    private fun demoLogin() {
        XLogin.login("xsolla", "xsolla", object : AuthCallback {
            override fun onSuccess() {
                XInventory.init(77640, XLogin.token!!)
                loadCurrencies()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }

        })
    }

    private fun loadCurrencies() {
        XInventory.getVirtualBalance(object : GetVirtualBalanceCallback {
            override fun onSuccess(response: VirtualBalanceResponse) {
                itemsView.adapter = CurrencyBalanceAdapter(response.items)
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
package com.xsolla.android.samples.buy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.payments.XPayments
import com.xsolla.android.samples.buy.adapter.BuyForRealAdapter
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.GetVirtualItemsCallback
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R
class BuyForRealActivity : AppCompatActivity() {

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
        val parentActivity = this
        XStore.getVirtualItems(object : GetVirtualItemsCallback {
            override fun onSuccess(response: VirtualItemsResponse) {
                itemsView.adapter = BuyForRealAdapter(parentActivity, response.items.filter { item -> item.virtualPrices.isEmpty() && !item.isFree })
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val (status, _) = XPayments.Result.fromResultIntent(data)
            when (status) {
                XPayments.Status.COMPLETED -> showSnack(getString(R.string.payment_completed))
                XPayments.Status.CANCELLED -> showSnack(getString(R.string.payment_cancelled))
                XPayments.Status.UNKNOWN -> showSnack(getString(R.string.payment_unknown))
            }
        }
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}
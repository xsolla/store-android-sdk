package com.xsolla.android.storesdkexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.inventory.VirtualBalanceResponse
import com.xsolla.android.storesdkexample.vm.VmCart
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.item_balance.view.*
import kotlinx.android.synthetic.main.layout_drawer.*

class StoreActivity : AppCompatActivity() {

    private val vmCart: VmCart by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)
//        val token = XLogin.getToken()
        val token = "eyJhbGc..."
        XStore.init(BuildConfig.PROJECT_ID, token)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        initNavController()
        initDrawer()
        getBalance() // TODO update in onResume
    }

    override fun onResume() {
        super.onResume()
        vmCart.updateCart()
    }

    private fun getBalance() {
        XStore.getVirtualBalance(object : XStoreCallback<VirtualBalanceResponse>() {
            override fun onSuccess(response: VirtualBalanceResponse) {
                updateBalanceContainer(response.items)
            }

            override fun onFailure(errorMessage: String) {}
        })
    }

    private fun updateBalanceContainer(items: List<VirtualBalanceResponse.Item>) {
        val balanceContainer: LinearLayout = findViewById(R.id.balanceContainer)

        items.forEach { item ->
            val balanceView = LayoutInflater.from(this).inflate(R.layout.item_balance, null)
            Glide.with(this).load(item.imageUrl).into(balanceView.balanceIcon)
            balanceView.balanceAmount.text = item.amount.toString()
            balanceContainer.addView(balanceView, 0)
        }

        chargeBalanceButton.setOnClickListener { findNavController(R.id.nav_host_fragment).navigate(R.id.nav_vc) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        vmCart.cartContent.observe(this, Observer {
            val cartCounter = menu.findItem(R.id.action_cart).actionView.findViewById<TextView>(R.id.cart_badge)
            val count = it.size
            cartCounter.text = count.toString()
            if (count == 0) {
                cartCounter.visibility = View.GONE
            } else {
                cartCounter.visibility = View.VISIBLE
            }
        })
        return true
    }

    private fun initNavController() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_vi, R.id.nav_vc, R.id.nav_inventory), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navController = findNavController(R.id.nav_host_fragment)
        textInventory.setOnClickListener {
            navController.navigate(R.id.nav_inventory)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        textVirtualItems.setOnClickListener {
            navController.navigate(R.id.nav_vi)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        textVirtualCurrency.setOnClickListener {
            navController.navigate(R.id.nav_vc)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        textCart.setOnClickListener {
            navController.navigate(R.id.nav_cart)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        textEmail.text = "a.nikonova@xsolla.com" // TODO take from XLogin
        textUsername.text = "usikpusik" // TODO take from XLogin
        vmCart.cartContent.observe(this, Observer {
            val count = it.sumBy { item -> item.quantity }
            textCartCounter.text = count.toString()
            if (count == 0) {
                bgCartCounter.visibility = View.GONE
                textCartCounter.visibility = View.GONE
            } else {
                bgCartCounter.visibility = View.VISIBLE
                textCartCounter.visibility = View.VISIBLE
            }
        })
    }
}
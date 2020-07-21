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
import com.xsolla.android.storesdkexample.vm.VmBalance
import com.xsolla.android.storesdkexample.vm.VmCart
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.item_balance.view.*
import kotlinx.android.synthetic.main.layout_drawer.*

class StoreActivity : AppCompatActivity() {

    private val vmCart: VmCart by viewModels()
    private val vmBalance: VmBalance by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)
//        val token = XLogin.getToken()
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Imxlb25pZC5yYXRhbm9mZkBnbWFpbC5jb20iLCJleHAiOjE1OTUzMzEwMTYsImdyb3VwcyI6W3siaWQiOjY0ODEsIm5hbWUiOiJkZWZhdWx0IiwiaXNfZGVmYXVsdCI6dHJ1ZX1dLCJpYXQiOjE1OTUyNDQ2MTYsImlzX21hc3RlciI6dHJ1ZSwiaXNzIjoiaHR0cHM6Ly9sb2dpbi54c29sbGEuY29tIiwicHJvbW9fZW1haWxfYWdyZWVtZW50Ijp0cnVlLCJwdWJsaXNoZXJfaWQiOjEzNjU5Mywic3ViIjoiMGVjYTNkMzctZmM3Ni00MWFkLWJiYTItYzFiODE4ZTQ4YWFhIiwidHlwZSI6Inhzb2xsYV9sb2dpbiIsInVzZXJuYW1lIjoicmF0YW5vZmYiLCJ4c29sbGFfbG9naW5fYWNjZXNzX2tleSI6InBSazU5UV9HM3RuMml0Y0VxSFhTcklIX2dxMDNxbVNzV2t0d1Nub3g4S2ciLCJ4c29sbGFfbG9naW5fcHJvamVjdF9pZCI6IjAyNjIwMWUzLTdlNDAtMTFlYS1hODViLTQyMDEwYWE4MDAwNCJ9.fdE2DNwrhlH2vg11qI7dyIPOxQ4aKTEyJSTKTZ8oyIc"
        XStore.init(BuildConfig.PROJECT_ID, token)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        initNavController()
        initDrawer()
        initVirtualBalance()
    }

    override fun onResume() {
        super.onResume()
        vmCart.updateCart()
        vmBalance.updateVirtualBalance()
    }

    private fun initVirtualBalance() {
        val balanceContainer: LinearLayout = findViewById(R.id.balanceContainer)
        vmBalance.virtualBalance.observe(this, Observer { virtualBalanceList ->
            balanceContainer.removeAllViews()
                virtualBalanceList.forEach { item ->
                    val balanceView = LayoutInflater.from(this).inflate(R.layout.item_balance, null)
                    Glide.with(this).load(item.imageUrl).into(balanceView.balanceIcon)
                    balanceView.balanceAmount.text = item.amount.toString()
                    balanceContainer.addView(balanceView, 0)
                }
        })
        chargeBalanceButton.setOnClickListener { findNavController(R.id.nav_host_fragment).navigate(R.id.nav_vc) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val cartView = menu.findItem(R.id.action_cart).actionView
        vmCart.cartContent.observe(this, Observer {
            val cartCounter = cartView.findViewById<TextView>(R.id.cart_badge)
            val count = it.sumBy { item -> item.quantity }
            cartCounter.text = count.toString()
            if (count == 0) {
                cartCounter.visibility = View.GONE
            } else {
                cartCounter.visibility = View.VISIBLE
            }
        })
        cartView.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_cart)
        }
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
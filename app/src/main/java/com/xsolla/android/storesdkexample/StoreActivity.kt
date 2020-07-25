package com.xsolla.android.storesdkexample

import android.app.Activity
import android.content.Intent
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
import com.xsolla.android.login.XLogin
import com.xsolla.android.store.XStore
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.item_balance.view.*
import kotlinx.android.synthetic.main.layout_drawer.*

class StoreActivity : AppCompatActivity() {

    companion object {
        const val RC_LOGIN = 1
    }

    private val vmCart: VmCart by viewModels()
    private val vmBalance: VmBalance by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        XStore.init(BuildConfig.PROJECT_ID, XLogin.getToken() ?: "")

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        initNavController()
        initDrawer()
        initVirtualBalance()
    }

    override fun onResume() {
        super.onResume()
        if (XLogin.isTokenExpired(60)) {
            startLogin()
        } else {
            XStore.init(BuildConfig.PROJECT_ID, XLogin.getToken())
        }
        vmCart.updateCart()
        vmBalance.updateVirtualBalance()
        setDrawerData()
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_LOGIN && resultCode != Activity.RESULT_OK) {
            finish()
        }
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
        vmCart.cartContent.observe(this, Observer { cartItems ->
            val cartCounter = cartView.findViewById<TextView>(R.id.cart_badge)
            val count = cartItems.sumBy { item -> item.quantity }
            cartCounter.text = count.toString()
            if (count == 0) {
                cartCounter.visibility = View.GONE
            } else {
                cartCounter.visibility = View.VISIBLE
            }

            cartView.setOnClickListener {
                if (cartItems.isNotEmpty()) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_cart)
                }
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
        drawerLayout = findViewById(R.id.drawer_layout)
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
        textLogout.setOnClickListener {
            XLogin.logout()
            startLogin()
        }
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

    private fun setDrawerData() {
        textEmail.text = XLogin.getJwt()?.getClaim("email")?.asString()
        textUsername.text = XLogin.getJwt()?.getClaim("username")?.asString()
    }

    private fun startLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, RC_LOGIN)
    }

}
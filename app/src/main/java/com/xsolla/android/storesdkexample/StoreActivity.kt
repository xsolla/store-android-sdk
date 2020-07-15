package com.xsolla.android.storesdkexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.item_balance.view.*
import kotlinx.android.synthetic.main.layout_drawer.*

class StoreActivity : AppCompatActivity() {

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
        getBalance()
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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
        textEmail.text = "a.nikonova@xsolla.com" // TODO take from XLogin
        textUsername.text = "usikpusik" // TODO take from XLogin
        textCartCounter.text = "2" // TODO take from XStore
    }
}
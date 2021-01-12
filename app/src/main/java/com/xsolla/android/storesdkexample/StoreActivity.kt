package com.xsolla.android.storesdkexample

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
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.appcore.extensions.setRateLimitedClickListener
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.RefreshTokenCallback
import com.xsolla.android.store.XStore
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
import com.xsolla.android.storesdkexample.ui.vm.base.ViewModelFactory
import com.xsolla.android.storesdkexample.util.sumByLong
import kotlinx.android.synthetic.main.activity_store.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.drawer_expandable_item.*
import kotlinx.android.synthetic.main.item_balance.view.*
import kotlinx.android.synthetic.main.layout_drawer.*

class StoreActivity : AppCompatActivity() {
    private val vmCart: VmCart by viewModels()
    private val vmBalance: VmBalance by viewModels()
    private val vmProfile: VmProfile by viewModels {
        ViewModelFactory(resources)
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    var showCartMenu = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        if (XLogin.isTokenExpired(60)) {
            if (!XLogin.canRefreshToken()) {
                startLogin()
            }
        }

        XStore.init(BuildConfig.PROJECT_ID, XLogin.token ?: "")
        XInventory.init(BuildConfig.PROJECT_ID, XLogin.token ?: "")

        val toolbar: Toolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        initNavController()
        initDrawer()
        initVirtualBalance()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("showCartMenu", showCartMenu)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        showCartMenu = savedInstanceState.getBoolean("showCartMenu")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (XLogin.isTokenExpired(60)) {
            if (XLogin.canRefreshToken()) {
                lock.visibility = View.VISIBLE
                XLogin.refreshToken(object : RefreshTokenCallback {
                    override fun onSuccess() {
                        lock.visibility = View.GONE
                        XStore.init(BuildConfig.PROJECT_ID, XLogin.token)
                        XInventory.init(BuildConfig.PROJECT_ID, XLogin.token!!)
                        vmCart.updateCart()
                        vmBalance.updateVirtualBalance()
                        setDrawerData()
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        lock.visibility = View.GONE
                        startLogin()
                    }
                })
            } else {
                startLogin()
            }
        } else {
            XStore.init(BuildConfig.PROJECT_ID, XLogin.token)
            XInventory.init(BuildConfig.PROJECT_ID, XLogin.token!!)
            vmCart.updateCart()
            vmBalance.updateVirtualBalance()

            setDrawerData()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        appbar?.mainToolbar?.title = ""
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
        chargeBalanceButton.setRateLimitedClickListener { findNavController(R.id.nav_host_fragment).navigate(R.id.nav_vc) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (showCartMenu) {
            menuInflater.inflate(R.menu.main, menu)
            val cartView = menu.findItem(R.id.action_cart).actionView
            vmCart.cartContent.observe(this, Observer { cartItems ->
                val cartCounter = cartView.findViewById<TextView>(R.id.cart_badge)
                val count = cartItems.sumByLong { item -> item.quantity }
                cartCounter.text = count.toString()
                if (count == 0L) {
                    cartCounter.visibility = View.GONE
                } else {
                    cartCounter.visibility = View.VISIBLE
                }

                cartView.setOnClickListener {
                    if (cartItems.isNotEmpty()) {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.nav_cart)
                    } else {
                        showSnack(getString(R.string.cart_message_empty))
                    }
                }
            })
        }

        return true
    }

    private fun initNavController() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_vi, R.id.nav_vc, R.id.nav_inventory, R.id.nav_friends, R.id.nav_character, R.id.nav_profile), drawerLayout)
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
        itemAccount.setOnClickListener {
            navController.navigate(R.id.nav_profile)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemInventory.setOnClickListener {
            navController.navigate(R.id.nav_inventory)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemCharacter.setOnClickListener {
            navController.navigate(R.id.nav_character)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemFriends.setOnClickListener {
            navController.navigate(R.id.nav_friends)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemVirtualItems.setOnClickListener {
            navController.navigate(R.id.nav_vi)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemVirtualCurrency.setOnClickListener {
            navController.navigate(R.id.nav_vc)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemCoupon.setOnClickListener {
            navController.navigate(R.id.nav_redeem_coupon)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemCart.setOnClickListener {
            if (vmCart.cartContent.value.isNullOrEmpty()) {
                showSnack(getString(R.string.cart_message_empty))
            } else {
                navController.navigate(R.id.nav_cart)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        itemLogout.setOnClickListener {
            XLogin.logout()
            startLogin()
        }
        vmCart.cartContent.observe(this, Observer {
            val count = it.sumByLong { item -> item.quantity }
            textCartCounter.text = count.toString()
            if (count == 0L) {
                bgCartCounter.visibility = View.GONE
                textCartCounter.visibility = View.GONE
            } else {
                bgCartCounter.visibility = View.VISIBLE
                textCartCounter.visibility = View.VISIBLE
            }
        })
    }

    private fun setDrawerData() {
        vmProfile.state.observe(this) { data ->
            if (data.email.isNotBlank()) textEmail.text = data.email
            else textEmail.visibility = View.GONE

            textUsername.text = when {
                data.nickname.isNotBlank() -> {
                    data.nickname
                }
                data.username.isNotBlank() -> {
                    data.nickname
                }
                data.firstName.isNotBlank() -> {
                    data.firstName
                }
                data.lastName.isNotBlank() -> {
                    data.lastName
                }
                else -> {
                    "Nickname"
                }
            }

            Glide.with(this@StoreActivity)
                .load(data.avatar)
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(iconProfile)
        }
    }

    private fun startLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}
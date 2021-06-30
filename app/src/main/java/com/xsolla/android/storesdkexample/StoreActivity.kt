package com.xsolla.android.storesdkexample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.appcore.databinding.ActivityStoreBinding
import com.xsolla.android.appcore.extensions.setRateLimitedClickListener
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.RefreshTokenCallback
import com.xsolla.android.store.XStore
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
import com.xsolla.android.storesdkexample.ui.vm.base.ViewModelFactory
import com.xsolla.android.storesdkexample.util.sumByLong

class StoreActivity : AppCompatActivity(R.layout.activity_store) {
    private val binding: ActivityStoreBinding by viewBinding(R.id.drawer_layout)

    private val vmCart: VmCart by viewModels()
    private val vmBalance: VmBalance by viewModels()
    private val vmProfile: VmProfile by viewModels {
        ViewModelFactory(resources)
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    var showCartMenu = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                binding.lock.visibility = View.VISIBLE
                XLogin.refreshToken(object : RefreshTokenCallback {
                    override fun onSuccess() {
                        binding.lock.visibility = View.GONE
                        XStore.init(BuildConfig.PROJECT_ID, XLogin.token!!)
                        XInventory.init(BuildConfig.PROJECT_ID, XLogin.token!!)
                        vmCart.updateCart()
                        vmBalance.updateVirtualBalance()
                        setDrawerData()

                        binding.root.closeDrawers()
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        binding.lock.visibility = View.GONE
                        startLogin()
                    }
                })
            } else {
                startLogin()
            }
        } else {
            XStore.init(BuildConfig.PROJECT_ID, XLogin.token!!)
            XInventory.init(BuildConfig.PROJECT_ID, XLogin.token!!)
            vmCart.updateCart()
            vmBalance.updateVirtualBalance()

            setDrawerData()
            binding.root.closeDrawers()
        }

        findViewById<Toolbar>(R.id.mainToolbar).title = ""
    }

    private fun initVirtualBalance() {
        val balanceContainer: LinearLayout = findViewById(R.id.balanceContainer)
        vmBalance.virtualBalance.observe(this, Observer { virtualBalanceList ->
            balanceContainer.removeAllViews()
                virtualBalanceList.forEach { item ->
                    val balanceView = LayoutInflater.from(this).inflate(R.layout.item_balance, null)
                    val balanceIcon = balanceView.findViewById<ImageView>(R.id.balanceIcon)
                    val balanceAmount = balanceView.findViewById<TextView>(R.id.balanceAmount)

                    Glide.with(this).load(item.imageUrl).into(balanceIcon)
                    balanceAmount.text = item.amount.toString()
                    balanceContainer.addView(balanceView, 0)
                }
        })
        findViewById<Button>(R.id.chargeBalanceButton).setRateLimitedClickListener { findNavController(R.id.nav_host_fragment).navigate(R.id.nav_vc) }
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
        val navController = findNavController(R.id.nav_host_fragment)

        findViewById<View>(R.id.itemAccount).setOnClickListener {
            navController.navigate(R.id.nav_profile)
            binding.root.closeDrawers()
        }
        findViewById<View>(R.id.itemInventory).setOnClickListener {
            navController.navigate(R.id.nav_inventory)
            binding.root.closeDrawers()
        }
        findViewById<View>(R.id.itemCharacter).setOnClickListener {
            navController.navigate(R.id.nav_character)
            binding.root.closeDrawers()
        }
        findViewById<View>(R.id.itemFriends).setOnClickListener {
            navController.navigate(R.id.nav_friends)
            binding.root.closeDrawers()
        }
        findViewById<View>(R.id.itemVirtualItems).setOnClickListener {
            navController.navigate(R.id.nav_vi)
            binding.root.closeDrawers()
        }
        findViewById<View>(R.id.itemVirtualCurrency).setOnClickListener {
            navController.navigate(R.id.nav_vc)
            binding.root.closeDrawers()
        }
        findViewById<View>(R.id.itemCoupon).setOnClickListener {
            navController.navigate(R.id.nav_redeem_coupon)
            binding.root.closeDrawers()
        }
        findViewById<View>(R.id.itemCart).setOnClickListener {
            if (vmCart.cartContent.value.isNullOrEmpty()) {
                showSnack(getString(R.string.cart_message_empty))
            } else {
                navController.navigate(R.id.nav_cart)
                binding.root.closeDrawers()
            }
        }
        findViewById<View>(R.id.itemLogout).setOnClickListener {
            XLogin.logout()
            startLogin()
        }
        vmCart.cartContent.observe(this, Observer {
            val count = it.sumByLong { item -> item.quantity }

            val textCartCounter = binding.root.findViewById<TextView>(R.id.textCartCounter)
            val bgCartCounter = binding.root.findViewById<View>(R.id.bgCartCounter)

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
        val email = binding.root.findViewById<TextView>(R.id.textEmail)
        val username = binding.root.findViewById<TextView>(R.id.textUsername)

        vmProfile.state.observe(this) { data ->
            if (data.email.isNotBlank()) email.text = data.email
            else email.visibility = View.GONE

            username.text = when {
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
                .into(binding.root.findViewById(R.id.iconProfile))
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
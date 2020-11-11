package com.xsolla.android.customauth.ui.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.appcore.extensions.setRateLimitedClickListener
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.data.local.PrefManager
import com.xsolla.android.customauth.databinding.ActivityStoreBinding
import com.xsolla.android.customauth.ui.login.LoginActivity
import com.xsolla.android.customauth.viewmodels.VmBalance
import com.xsolla.android.customauth.viewmodels.VmCart
import com.xsolla.android.store.XStore

class StoreActivity : AppCompatActivity(R.layout.activity_store) {
    private val binding: ActivityStoreBinding by viewBinding(R.id.container)
    private val balanceViewModel: VmBalance by viewModels()
    private val cartViewModel: VmCart by viewModels()

    private val preferences: PrefManager = PrefManager

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    var showCartMenu = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        XStore.init(com.xsolla.android.customauth.BuildConfig.PROJECT_ID, preferences.token)
        balanceViewModel.updateVirtualBalance()

        setSupportActionBar(binding.appbar.mainToolbar)

        initNavController()
        initVirtualBalance()
        initDrawer()
        setDrawerData()

        cartViewModel.cartContent.observe(this) { cartItems ->
            val count = cartItems.sumBy { item -> item.quantity.toInt() }

            val cartView = binding.appbar.mainToolbar.menu?.findItem(R.id.action_cart)?.actionView
            val cartCounter = cartView?.findViewById<TextView>(R.id.cart_badge)
            cartCounter?.text = count.toString()
            cartCounter?.isGone = count == 0

            binding.drawer.findViewById<TextView>(R.id.textCartCounter).text = count.toString()
            binding.drawer.findViewById<View>(R.id.textCartCounter).isGone = count == 0
            binding.drawer.findViewById<View>(R.id.bgCartCounter).isGone = count == 0
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (showCartMenu) {
            menuInflater.inflate(R.menu.main, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                if (cartViewModel.cartContent.value.isNullOrEmpty()) {
                    showSnack(getString(R.string.cart_message_empty))
                } else {
                    navController.navigate(R.id.nav_cart)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initNavController() {
        navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_vi, R.id.nav_vc, R.id.nav_cart, R.id.nav_inventory))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun initVirtualBalance() {
        balanceViewModel.virtualBalance.observe(this) { virtualBalanceList ->
            binding.appbar.balanceContainer.removeAllViews()
            virtualBalanceList.forEach { item ->
                val balanceView = LayoutInflater.from(this).inflate(R.layout.item_balance, null)
                Glide.with(this).load(item.imageUrl).into(balanceView.findViewById(R.id.balanceIcon))
                balanceView.findViewById<TextView>(R.id.balanceAmount).text = item.amount.toString()
                binding.appbar.balanceContainer.addView(balanceView, 0)
            }
        }
        binding.appbar.chargeBalanceButton.setRateLimitedClickListener { findNavController(R.id.nav_host_fragment).navigate(R.id.nav_vc) }
    }

    private fun initDrawer() {
        binding.drawer.findViewById<View>(R.id.itemCharacter).isGone = true
        binding.drawer.findViewById<View>(R.id.itemFriends).isGone = true
        binding.drawer.findViewById<View>(R.id.itemAccount).isGone = true

        binding.drawer.findViewById<View>(R.id.itemInventory).setOnClickListener {
            navController.navigate(R.id.nav_inventory)
            binding.root.close()
        }
        binding.drawer.findViewById<View>(R.id.itemVirtualItems).setOnClickListener {
            showSnack("VI")
            binding.root.close()
        }
        binding.drawer.findViewById<View>(R.id.itemVirtualCurrency).setOnClickListener {
            showSnack("VC")
            binding.root.close()
        }
        binding.drawer.findViewById<View>(R.id.itemCart).setOnClickListener {
            if (cartViewModel.cartContent.value.isNullOrEmpty()) {
                showSnack(getString(R.string.cart_message_empty))
            } else {
                navController.navigate(R.id.nav_cart)
            }
        }
        binding.drawer.findViewById<View>(R.id.itemLogout).setOnClickListener { logout() }
    }

    private fun setDrawerData() {
        binding.drawer.findViewById<TextView>(R.id.textEmail).text = preferences.email
        binding.drawer.findViewById<TextView>(R.id.textUsername).isGone = true
    }

    private fun logout() {
        preferences.clearAll()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}
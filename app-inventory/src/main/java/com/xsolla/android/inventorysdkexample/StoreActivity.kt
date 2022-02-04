package com.xsolla.android.inventorysdkexample

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
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventorysdkexample.databinding.ActivityStoreBinding
import com.xsolla.android.inventorysdkexample.ui.vm.VmBalance
import com.xsolla.android.inventorysdkexample.ui.vm.VmProfile
import com.xsolla.android.inventorysdkexample.ui.vm.base.ViewModelFactory
import com.xsolla.android.inventorysdkexample.util.extensions.openInBrowser
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.RefreshTokenCallback
import com.xsolla.android.store.XStore

class StoreActivity : AppCompatActivity(R.layout.activity_store) {
    private val binding: ActivityStoreBinding by viewBinding(R.id.drawer_layout)

    private val vmBalance: VmBalance by viewModels()
    private val vmProfile: VmProfile by viewModels {
        ViewModelFactory(resources)
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        XInventory.init(BuildConfig.PROJECT_ID, XLogin.token ?: "")
        XStore.init(BuildConfig.PROJECT_ID, XLogin.token ?: "")

        super.onCreate(savedInstanceState)

        if (XLogin.isTokenExpired(60)) {
            if (!XLogin.canRefreshToken()) {
                startLogin()
            }
        }

        val toolbar: Toolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        initNavController()
        initDrawer()
        initVirtualBalance()

        initChargeCurrencyButton()
    }

    private fun initChargeCurrencyButton() {
        val button = findViewById<Button>(R.id.chargeBalanceButton)
        button.setOnClickListener { openWebStore() }
    }

    override fun onResume() {
        super.onResume()
        if (XLogin.isTokenExpired(60)) {
            if (XLogin.canRefreshToken()) {
                binding.lock.visibility = View.VISIBLE
                XLogin.refreshToken(object : RefreshTokenCallback {
                    override fun onSuccess() {
                        binding.lock.visibility = View.GONE
                        XInventory.init(BuildConfig.PROJECT_ID, XLogin.token!!)
                        XStore.init(BuildConfig.PROJECT_ID, XLogin.token!!)
                        vmBalance.updateVirtualBalance()
                        setDrawerData()
                        drawerLayout.closeDrawer(GravityCompat.START)
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
            XInventory.init(BuildConfig.PROJECT_ID, XLogin.token!!)
            XStore.init(BuildConfig.PROJECT_ID, XLogin.token!!)
            vmBalance.updateVirtualBalance()

            setDrawerData()
            drawerLayout.closeDrawer(GravityCompat.START)
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun initNavController() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_inventory), drawerLayout)
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
        findViewById<View>(R.id.itemInventory).setOnClickListener {
            navController.navigate(R.id.nav_inventory)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<View>(R.id.itemRedeemCoupon).setOnClickListener {
            navController.navigate(R.id.nav_redeem_coupon)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<View>(R.id.itemWebStore).setOnClickListener {
            openWebStore()
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<View>(R.id.itemTutorial).setOnClickListener {
            startTutorial()
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<View>(R.id.itemLogout).setOnClickListener {
            XLogin.logout()
            startLogin()
        }
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

    private fun openWebStore() =
            "https://sitebuilder.xsolla.com/game/sdk-web-store/?token=${XLogin.token}&remember_me=false"
            .toUri()
            .openInBrowser(this)

    private fun startTutorial() {
        val intent = Intent(this, TutorialActivity::class.java)
        intent.putExtra(TutorialActivity.EXTRA_MANUAL_RUN, true)
        startActivity(intent)
    }
}
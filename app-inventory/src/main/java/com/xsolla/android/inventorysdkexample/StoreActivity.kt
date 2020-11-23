package com.xsolla.android.inventorysdkexample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
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
import com.xsolla.android.inventorysdkexample.ui.vm.VmBalance
import com.xsolla.android.inventorysdkexample.ui.vm.VmProfile
import com.xsolla.android.inventorysdkexample.ui.vm.base.ViewModelFactory
import com.xsolla.android.inventorysdkexample.util.extensions.openInBrowser
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.RefreshTokenCallback
import com.xsolla.android.store.XStore
import kotlinx.android.synthetic.main.activity_store.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.item_balance.view.*
import kotlinx.android.synthetic.main.layout_drawer.*

class StoreActivity : AppCompatActivity() {
    private val vmBalance: VmBalance by viewModels()
    private val vmProfile: VmProfile by viewModels {
        ViewModelFactory(resources)
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        if (XLogin.isTokenExpired(60)) {
            if (!XLogin.canRefreshToken()) {
                startLogin()
            }
        }

        XInventory.init(BuildConfig.PROJECT_ID, XLogin.token ?: "")
        XStore.init(BuildConfig.PROJECT_ID, XLogin.token ?: "")

        val toolbar: Toolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        initNavController()
        initDrawer()
        initVirtualBalance()
    }

    override fun onResume() {
        super.onResume()
        if (XLogin.isTokenExpired(60)) {
            if (XLogin.canRefreshToken()) {
                lock.visibility = View.VISIBLE
                XLogin.refreshToken(object : RefreshTokenCallback {
                    override fun onSuccess() {
                        lock.visibility = View.GONE
                        XInventory.init(BuildConfig.PROJECT_ID, XLogin.token!!)
                        XStore.init(BuildConfig.PROJECT_ID, XLogin.token!!)
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
            XInventory.init(BuildConfig.PROJECT_ID, XLogin.token!!)
            XStore.init(BuildConfig.PROJECT_ID, XLogin.token!!)
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
        itemInventory.setOnClickListener {
            navController.navigate(R.id.nav_inventory)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemRedeemCoupon.setOnClickListener {
            navController.navigate(R.id.nav_redeem_coupon)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemWebStore.setOnClickListener {
            openWebStore()
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemTutorial.setOnClickListener {
            startTutorial()
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        itemLogout.setOnClickListener {
            XLogin.logout()
            startLogin()
        }
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

    private fun openWebStore() =
            "https://livedemo.xsolla.com/sdk-account-linking-android/?token=${XLogin.token}&remember_me=false"
            .toUri()
            .openInBrowser(this)

    private fun startTutorial() {
        val intent = Intent(this, TutorialActivity::class.java)
        intent.putExtra(TutorialActivity.EXTRA_MANUAL_RUN, true)
        startActivity(intent)
    }
}
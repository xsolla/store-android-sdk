package com.xsolla.android.storesdkexample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.appcore.databinding.ActivityStoreBinding
import com.xsolla.android.appcore.extensions.openInBrowser
import com.xsolla.android.appcore.extensions.setRateLimitedClickListener
import com.xsolla.android.appcore.ui.vm.VmPurchase
import com.xsolla.android.googleplay.StoreUtils
import com.xsolla.android.googleplay.inventory.InventoryAdmin
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.RefreshTokenCallback
import com.xsolla.android.login.jwt.JWT
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.data.AccessToken
import com.xsolla.android.store.XStore
import com.xsolla.android.storesdkexample.data.local.DemoCredentialsManager
import com.xsolla.android.storesdkexample.googleplay.GooglePlayPurchaseHandler
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmGooglePlay
import com.xsolla.android.storesdkexample.ui.vm.VmProfile
import com.xsolla.android.storesdkexample.ui.vm.base.ViewModelFactory

class StoreActivity : AppCompatActivity(R.layout.activity_store) {

    companion object {
        private const val RC_PAYSTATION = 1
    }

    private val binding: ActivityStoreBinding by viewBinding(R.id.drawer_layout)

    private val vmPurchase: VmPurchase by viewModels()
    private val vmBalance: VmBalance by viewModels()
    private val vmGooglePlay: VmGooglePlay by viewModels()
    private val vmProfile: VmProfile by viewModels {
        ViewModelFactory(resources)
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var billingClient: BillingClient
    private lateinit var googlePlayPurchaseHandler: GooglePlayPurchaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        XStore.init(DemoCredentialsManager.projectId, XLogin.token ?: "")
        XInventory.init(DemoCredentialsManager.projectId, XLogin.token ?: "")

        super.onCreate(savedInstanceState)

        val jwtExpiresTime = if(XLogin.token != null) JWT(XLogin.token).expiresAt.time / 1000 else 0
        val currentTime = System.currentTimeMillis() / 1000

        if (jwtExpiresTime <= currentTime) {
            if (!XLogin.canRefreshToken()) {
                startLogin()
            }
        }

        val toolbar: Toolbar = findViewById(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        initNavController()
        initDrawer()
        initVirtualBalance()

        if (StoreUtils.isAppInstalledFromGooglePlay(this)) {
            initGooglePlay()
        }

        vmPurchase.paymentToken.observe(this) { token ->
            val intent = XPayments.createIntentBuilder(this)
                .accessToken(AccessToken(token))
                .isSandbox(BuildConfig.IS_SANDBOX)
                .build()
            startActivityForResult(intent, RC_PAYSTATION)
        }
        vmPurchase.startPurchaseError.observe(this) { errorMessage ->
            showSnack(errorMessage)
        }
    }

    override fun onResume() {
        super.onResume()
        val jwtExpiresTime = if(XLogin.token != null) JWT(XLogin.token).expiresAt.time / 1000 else 0
        val currentTime = System.currentTimeMillis() / 1000

        if (jwtExpiresTime <= currentTime) {
            if (XLogin.canRefreshToken()) {
                binding.lock.visibility = View.VISIBLE
                XLogin.refreshToken(object : RefreshTokenCallback {
                    override fun onSuccess() {
                        binding.lock.visibility = View.GONE
                        XStore.init(DemoCredentialsManager.projectId, XLogin.token!!)
                        XInventory.init(DemoCredentialsManager.projectId, XLogin.token!!)
                        vmBalance.updateVirtualBalance()
                        setDrawerData()
                        binding.root.closeDrawers()

                        supportFragmentManager.fragments.forEach { fragment ->
                            fragment.childFragmentManager.fragments.forEach { childFragment ->
                                if(childFragment is BaseFragment) {
                                    childFragment.initUI()
                                }
                            }
                        }
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
            XStore.init(DemoCredentialsManager.projectId, XLogin.token!!)
            XInventory.init(DemoCredentialsManager.projectId, XLogin.token!!)
            vmBalance.updateVirtualBalance()

            setDrawerData()
            binding.root.closeDrawers()
        }

        findViewById<Toolbar>(R.id.mainToolbar).title = ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_PAYSTATION) {
            val (status, _) = XPayments.Result.fromResultIntent(data)
            when (status) {
                XPayments.Status.COMPLETED -> showSnack(getString(R.string.payment_completed))
                XPayments.Status.CANCELLED -> showSnack(getString(R.string.payment_cancelled))
                XPayments.Status.UNKNOWN -> showSnack(getString(R.string.payment_unknown))
            }
        }
    }

    private fun initVirtualBalance() {
        val balanceContainer: LinearLayout = findViewById(R.id.balanceContainer)
        vmBalance.virtualBalance.observe(this) { virtualBalanceList ->
            balanceContainer.removeAllViews()
            virtualBalanceList.forEach { item ->
                val balanceView = LayoutInflater.from(this).inflate(R.layout.item_balance, null)
                val balanceIcon = balanceView.findViewById<ImageView>(R.id.balanceIcon)
                val balanceAmount = balanceView.findViewById<TextView>(R.id.balanceAmount)

                Glide.with(this).load(item.imageUrl).into(balanceIcon)
                balanceAmount.text = item.amount.toString()
                balanceContainer.addView(balanceView, 0)
            }
        }
        findViewById<Button>(R.id.chargeBalanceButton).setRateLimitedClickListener {
            findNavController(
                R.id.nav_host_fragment
            ).navigate(R.id.nav_vc)
        }
    }

    private fun initNavController() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_vi,
                R.id.nav_vc,
                R.id.nav_inventory,
                R.id.nav_friends,
                R.id.nav_attributes,
                R.id.nav_profile
            ), drawerLayout
        )
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
        findViewById<View>(R.id.itemAttributes).setOnClickListener {
            navController.navigate(R.id.nav_attributes)
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
        findViewById<View>(R.id.itemWebStore).setOnClickListener {
            openWebStore()
            binding.root.closeDrawers()
        }
        findViewById<View>(R.id.itemWebStore).isVisible =
            !StoreUtils.isAppInstalledFromGooglePlay(this)
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

    private fun initGooglePlay() {
        InventoryAdmin.init("https://us-central1-xsolla-sdk-demo.cloudfunctions.net")
        googlePlayPurchaseHandler = GooglePlayPurchaseHandler(
            this@StoreActivity,
            this@StoreActivity::showSnack,
            successGrantItemToUser = { vmBalance.updateVirtualBalance() }
        )

        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(googlePlayPurchaseHandler)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    vmGooglePlay.product.observe(this@StoreActivity) { product ->
                        val userId = vmProfile.state.value?.id!!
                        googlePlayPurchaseHandler.startPurchase(billingClient, product, userId)
                    }
                } else {
                    showSnack(billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                showSnack("Billing Client is not initialized")
            }
        })
    }

    private fun openWebStore() {
        val baseUri = DemoCredentialsManager.webshopUrl.toUri()
        Uri.Builder()
            .scheme(baseUri.scheme)
            .authority(baseUri.authority)
            .path(baseUri.path)
            .encodedQuery("token=${XLogin.token}&remember_me=false")
            .build()
            .openInBrowser(this)
    }

}
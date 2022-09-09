package com.xsolla.android.storesdkexample.ui.fragments.store

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.appcore.databinding.FragmentBundleBinding
import com.xsolla.android.appcore.ui.vm.VmPurchase
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.googleplay.StoreUtils
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.CreateOrderByVirtualCurrencyCallback
import com.xsolla.android.store.callbacks.GetBundleCallback
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.BundleAdapter
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmGooglePlay

class BundleFragment : BaseFragment(), PurchaseListener {

    private val binding: FragmentBundleBinding by viewBinding()
    private val args: BundleFragmentArgs by navArgs()
    private val vmPurchase: VmPurchase by activityViewModels()
    private val vmBalance: VmBalance by viewModels()
    private val vmGooglePlay: VmGooglePlay by activityViewModels()

    override fun getLayout(): Int = R.layout.fragment_bundle

    override val toolbarOption: ToolbarOptions = ToolbarOptions(showBalance = false, showCart = false)

    override fun initUI() {
        bindBundleFields()
        bindBuyButton()

        val bundleAdapter = BundleAdapter(mutableListOf())
        with(binding.rvBundleContent) {
            setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    linearLayoutManager.orientation
                ).apply {
                    ContextCompat.getDrawable(context, R.drawable.item_divider)
                        ?.let { setDrawable(it) }
                })
            layoutManager = linearLayoutManager
            adapter = bundleAdapter
        }
        bindBundleContent(bundleAdapter)
    }

    private fun bindBundleContent(bundleAdapter: BundleAdapter) {

        XStore.getBundle(object : GetBundleCallback {


            override fun onSuccess(response: BundleItem) {
                bundleAdapter.items.addAll(response.content)
                bundleAdapter.notifyDataSetChanged()
                //this code wont be applied on non-bundle objects, so its safe to do it like that
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnackBar("Error getting a bundle from server")
            }

        }, args.xbundle.sku!!)
    }

    private fun bindBundleFields() {

        if (args.xbundle.virtualPrices.isEmpty()) {
            //set bundle REAL PRICE
            val price = args.xbundle.price
            binding.itemVirtualPriceIcon.visibility = View.GONE
            binding.tvBundleName.text = args.xbundle.name
            Glide.with(this).load(args.xbundle.imageUrl).into(binding.ivBundlePreview)
            if (price!!.getAmountDecimal() == price.getAmountWithoutDiscountDecimal()) {
                //if no discounts active
                binding.tvBundlePrice.text =
                    AmountUtils.prettyPrint(price.getAmountDecimal()!!, price.currency!!)
                binding.tvBundleOldPrice.visibility = View.INVISIBLE
            } else {
                //if there is discount
                binding.tvBundlePrice.text =
                    AmountUtils.prettyPrint(price.getAmountDecimal()!!, price.currency!!)
                binding.tvBundleOldPrice.text =
                    AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal()!!)
                binding.tvBundleOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvBundleOldPrice.visibility = View.VISIBLE
            }
            binding.tvBundleDescription.text = args.xbundle.description
        } else {
            //set bundle VIRTUAL PRICE
            binding.itemVirtualPriceIcon.visibility = View.VISIBLE
            val price = args.xbundle.virtualPrices[0]
            Glide.with(this).load(price.imageUrl).into(binding.itemVirtualPriceIcon)
            binding.tvBundleName.text = args.xbundle.name
            Glide.with(this).load(args.xbundle.imageUrl).into(binding.ivBundlePreview)
            if (price.getAmountDecimal() == price.getAmountWithoutDiscountDecimal() || price.calculatedPrice?.amountWithoutDiscount == null) {
                //if no discounts active
                binding.tvBundlePrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!)
                binding.tvBundleOldPrice.visibility = View.INVISIBLE
            } else {
                //if there is discount
                binding.tvBundlePrice.text = AmountUtils.prettyPrint(price.getAmountDecimal()!!)
                binding.tvBundleOldPrice.text =
                    AmountUtils.prettyPrint(price.getAmountWithoutDiscountDecimal()!!)
                binding.tvBundleOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvBundleOldPrice.visibility = View.VISIBLE
            }
            binding.tvBundleDescription.text = args.xbundle.description
        }
    }

    private fun bindBuyButton() {
        if (args.xbundle.virtualPrices.isNullOrEmpty()) {
            // real price
            if (!StoreUtils.isAppInstalledFromGooglePlay(requireContext())) {
                binding.btBundleBuy.setOnClickListener { view ->
                    view.isEnabled = false
                    vmPurchase.startPurchase(BuildConfig.IS_SANDBOX, args.xbundle.sku!!, 1) {
                        view.isEnabled = true
                    }
                }
            } else {
                binding.btBundleBuy.setOnClickListener {
                    vmGooglePlay.startPurchase(args.xbundle.sku!!)
                }
            }
        } else {
            //buy via virtual price
            binding.btBundleBuy.setOnClickListener { v ->
                v.isEnabled = false
                XStore.createOrderByVirtualCurrency(
                    object : CreateOrderByVirtualCurrencyCallback {
                        override fun onSuccess(response: CreateOrderByVirtualCurrencyResponse) {
                            this@BundleFragment.onSuccess()
                            v.isEnabled = true
                            vmBalance.updateVirtualBalance()
                        }

                        override fun onError(throwable: Throwable?, errorMessage: String?) {
                            this@BundleFragment.onFailure(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                            v.isEnabled = true
                        }
                    },
                    args.xbundle.sku!!, args.xbundle.virtualPrices[0].sku!!
                )
            }
        }
    }

    override fun onSuccess() {
        showSnackBar("Success")
    }

    override fun onFailure(errorMessage: String) {
        showSnackBar("Error")
    }

    override fun showMessage(message: String) {
        TODO("Not yet implemented")
    }

    private fun showSnackBar(message: String) {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}
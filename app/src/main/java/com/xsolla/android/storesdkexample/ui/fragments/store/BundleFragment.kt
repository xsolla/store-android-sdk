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
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.googleplay.StoreUtils
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.CreateOrderByVirtualCurrencyCallback
import com.xsolla.android.store.callbacks.GetBundleCallback
import com.xsolla.android.store.callbacks.UpdateItemFromCurrentCartCallback
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.BundleAdapter
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.ui.vm.VmGooglePlay
import com.xsolla.android.storesdkexample.util.ViewUtils

class BundleFragment : BaseFragment(), PurchaseListener {

    private val binding: FragmentBundleBinding by viewBinding()
    private val args: BundleFragmentArgs by navArgs()
    private val vmCart: VmCart by viewModels()
    private val vmBalance: VmBalance by viewModels()
    private val vmGooglePlay: VmGooglePlay by activityViewModels()

    override fun getLayout(): Int = R.layout.fragment_bundle
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

        }, args.bundle.sku!!)
    }

    private fun bindBundleFields() {

        if (args.bundle.virtualPrices.isEmpty()) {
            //set bundle REAL PRICE
            val price = args.bundle.price
            binding.itemVirtualPriceIcon.visibility = View.GONE
            binding.tvBundleName.text = args.bundle.name
            Glide.with(this).load(args.bundle.imageUrl).into(binding.ivBundlePreview)
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
            binding.tvBundleDescription.text = args.bundle.description
        } else {
            //set bundle VIRTUAL PRICE
            binding.itemVirtualPriceIcon.visibility = View.VISIBLE
            val price = args.bundle.virtualPrices[0]
            Glide.with(this).load(price.imageUrl).into(binding.itemVirtualPriceIcon)
            binding.tvBundleName.text = args.bundle.name
            Glide.with(this).load(args.bundle.imageUrl).into(binding.ivBundlePreview)
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
            binding.tvBundleDescription.text = args.bundle.description
        }
    }

    private fun bindBuyButton() {
        if (args.bundle.virtualPrices.isNullOrEmpty()) {
            // real price - add to cart or buy via google play
            if (StoreUtils.isXsollaCartAvailable(requireContext())) {
                binding.btBundleBuy.setOnClickListener { v ->
                    ViewUtils.disable(v)
                    val cartContent = vmCart.cartContent.value
                    val quantity =
                        cartContent?.find { it.sku == args.bundle.sku }?.quantity ?: 0
                    XStore.updateItemFromCurrentCart(
                        object : UpdateItemFromCurrentCartCallback {
                            override fun onSuccess() {
                                vmCart.updateCart()
                                this@BundleFragment.onSuccess()
                                ViewUtils.enable(v)
                            }

                            override fun onError(throwable: Throwable?, errorMessage: String?) {
                                this@BundleFragment.onFailure(
                                    errorMessage ?: "Error adding to cart"
                                )
                                ViewUtils.enable(v)
                            }
                        }, args.bundle.sku!!, quantity + 1
                    )
                }
            } else {
                binding.btBundleBuy.setOnClickListener {
                    vmGooglePlay.startPurchase(args.bundle.sku!!)
                }
            }
        } else {
            //buy via virtual price
            binding.btBundleBuy.setOnClickListener { v ->
                ViewUtils.disable(v)
                XStore.createOrderByVirtualCurrency(
                    object : CreateOrderByVirtualCurrencyCallback {
                        override fun onSuccess(response: CreateOrderByVirtualCurrencyResponse) {
                            this@BundleFragment.onSuccess()
                            ViewUtils.enable(v)
                            vmBalance.updateVirtualBalance()
                        }

                        override fun onError(throwable: Throwable?, errorMessage: String?) {
                            this@BundleFragment.onFailure(errorMessage ?: "Error adding to cart")
                            ViewUtils.enable(v)
                        }
                    },
                    args.bundle.sku!!, args.bundle.virtualPrices[0].sku!!
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
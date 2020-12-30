package com.xsolla.android.customauth.ui.store

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.customauth.BuildConfig
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentCartBinding
import com.xsolla.android.customauth.ui.BaseFragment
import com.xsolla.android.customauth.ui.adapter.CartAdapter
import com.xsolla.android.customauth.viewmodels.VmCart
import com.xsolla.android.payments.XPayments.Companion.createIntentBuilder
import com.xsolla.android.payments.XPayments.Result.Companion.fromResultIntent
import com.xsolla.android.payments.data.AccessToken
import java.math.BigDecimal

class CartFragment : BaseFragment(), CartChangeListener {

    private companion object {
        private const val RC_PAYSTATION = 1
    }

    private val binding: FragmentCartBinding by viewBinding()

    private val vmCart: VmCart by activityViewModels()

    private var orderId = 0

    override fun getLayout() = R.layout.fragment_cart

    override fun initUI() {
        val cartAdapter = CartAdapter(mutableListOf(), vmCart, this)
        with(binding.recycler) {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                androidx.core.content.ContextCompat.getDrawable(context, R.drawable.item_divider)?.let { setDrawable(it) }
            })
            adapter = cartAdapter
        }

        vmCart.cartContent.observe(viewLifecycleOwner, Observer { items ->
            if (items.isEmpty()) {
                findNavController().navigateUp()
                return@Observer
            }

            cartAdapter.items.clear()
            cartAdapter.items.addAll(items)
            cartAdapter.notifyDataSetChanged()
            binding.checkoutButton.isEnabled = items.isNotEmpty()

            val currency = items[0].price!!.currency

            val sumWithoutDiscount = items.map { item -> item.price!!.getAmountWithoutDiscountDecimal()!! * item.quantity.toBigDecimal() }.fold(BigDecimal.ZERO, BigDecimal::add)
            val sumWithDiscount = items.map { item -> item.price!!.getAmountDecimal()!! * item.quantity.toBigDecimal() }.fold(BigDecimal.ZERO, BigDecimal::add)
            val discount = sumWithoutDiscount.minus(sumWithDiscount)

            val hasDiscount = discount.toDouble() != 0.0
            binding.subtotalLabel.isVisible = hasDiscount
            binding.subtotalValue.isVisible = hasDiscount
            binding.discountLabel.isVisible = hasDiscount
            binding.discountValue.isVisible = hasDiscount

            binding.subtotalValue.text = AmountUtils.prettyPrint(sumWithoutDiscount, currency!!)
            binding.discountValue.text = "- ${AmountUtils.prettyPrint(discount, currency)}"
            binding.totalValue.text = AmountUtils.prettyPrint(sumWithDiscount, currency)
        })

        binding.clearButton.setOnClickListener {
            vmCart.clearCart { result ->
                showSnack(result)
                findNavController().navigateUp()
            }
        }

        binding.checkoutButton.setOnClickListener {
            vmCart.createOrder { error -> showSnack(error) }
        }

        binding.continueButton.setOnClickListener { findNavController().navigateUp() }

        vmCart.paymentToken.observe(viewLifecycleOwner, Observer {
            val intent = createIntentBuilder(requireContext())
                .accessToken(AccessToken(it))
                .useWebview(true)
                .isSandbox(BuildConfig.IS_SANDBOX)
                .build()
            startActivityForResult(intent, RC_PAYSTATION)
        })

        vmCart.orderId.observe(viewLifecycleOwner, {
            orderId = it
        })
    }

    override fun onResume() {
        super.onResume()
        vmCart.updateCart()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            orderId = savedInstanceState.getInt("orderId")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("orderId", orderId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_PAYSTATION) {
            val (status, invoiceId) = fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                vmCart.checkOrder(orderId) { error -> showSnack(error) }
            }
        }
    }

    override fun onChange(result: String) {
        showSnack(result)
    }
}

interface CartChangeListener {
    fun onChange(result: String)
}
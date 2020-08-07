package com.xsolla.android.storesdkexample.ui.fragments.store

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.payments.XPayments.Companion.createIntentBuilder
import com.xsolla.android.payments.XPayments.Result.Companion.fromResultIntent
import com.xsolla.android.payments.data.AccessToken
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.CartAdapter
import com.xsolla.android.storesdkexample.listener.CartChangeListener
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.util.AmountUtils
import kotlinx.android.synthetic.main.fragment_cart.*
import java.math.BigDecimal

class CartFragment : Fragment(), CartChangeListener {

    private val vmCart: VmCart by activityViewModels()

    private var orderId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cartAdapter = CartAdapter(mutableListOf(), vmCart, this)
        with(recycler) {
            setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation).apply {
                ContextCompat.getDrawable(context, R.drawable.item_divider)?.let { setDrawable(it) }
            })
            layoutManager = linearLayoutManager
            adapter = cartAdapter
        }

        vmCart.cartContent.observe(viewLifecycleOwner, Observer { items ->
            if (items.isEmpty()) {
                showSnack(getString(R.string.cart_message_empty))
                findNavController().navigateUp()
                return@Observer
            }

            cartAdapter.items.clear()
            cartAdapter.items.addAll(items)
            cartAdapter.notifyDataSetChanged()
            checkoutButton.isEnabled = items.isNotEmpty()

            val currency = items[0].price.currency

            val sumWithoutDiscount = items.map { item -> item.price.amountWithoutDiscountDecimal * item.quantity.toBigDecimal() }.fold(BigDecimal.ZERO, BigDecimal::add)
            val sumWithDiscount = items.map { item -> item.price.amountDecimal * item.quantity.toBigDecimal() }.fold(BigDecimal.ZERO, BigDecimal::add)
            val discount = sumWithoutDiscount.minus(sumWithDiscount)

            subtotalValue.text = AmountUtils.prettyPrint(sumWithoutDiscount, currency)
            discountValue.text = "- ${AmountUtils.prettyPrint(discount, currency)}"
            totalValue.text = AmountUtils.prettyPrint(sumWithDiscount, currency)
        })

        clearButton.setOnClickListener {
            vmCart.clearCart { result ->
                showSnack(result)
                findNavController().navigateUp()
            }
        }

        checkoutButton.setOnClickListener {
            vmCart.createOrder { error -> showSnack(error) }
        }

        continueButton.setOnClickListener { findNavController().navigateUp() }

        vmCart.paymentToken.observe(viewLifecycleOwner, Observer {
            val intent = createIntentBuilder(requireContext())
                    .accessToken(AccessToken(it))
                    .useWebview(true)
                    .isSandbox(BuildConfig.IS_SANDBOX)
                    .build()
            startActivityForResult(intent, RC_PAYSTATION)
        })

        vmCart.orderId.observe(viewLifecycleOwner, Observer {
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
                vmCart.checkOrder(orderId) { error -> showSnack(error)}
            }
        }
    }

    override fun onChange(result: String) {
        showSnack(result)
    }

    private fun showSnack(message: String) {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val RC_PAYSTATION = 1

        @JvmStatic
        fun newInstance() =
                CartFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
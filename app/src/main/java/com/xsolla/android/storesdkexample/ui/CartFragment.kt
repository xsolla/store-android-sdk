package com.xsolla.android.storesdkexample.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xsolla.android.payments.XPayments.Companion.createIntentBuilder
import com.xsolla.android.payments.XPayments.Result.Companion.fromResultIntent
import com.xsolla.android.payments.data.AccessToken
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.CartAdapter
import com.xsolla.android.storesdkexample.vm.VmCart
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : Fragment() {

    private val vmCart: VmCart by activityViewModels()

    private var orderId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cartAdapter = CartAdapter(mutableListOf(), vmCart)
        with(recycler) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
        vmCart.cartContent.observe(viewLifecycleOwner, Observer {
            cartAdapter.items.clear()
            cartAdapter.items.addAll(it)
            cartAdapter.notifyDataSetChanged()
            checkoutButton.isEnabled = it.isNotEmpty()
        })
        checkoutButton.setOnClickListener {
            vmCart.createOrder()
        }
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
                vmCart.checkOrder(orderId)
            }
        }
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
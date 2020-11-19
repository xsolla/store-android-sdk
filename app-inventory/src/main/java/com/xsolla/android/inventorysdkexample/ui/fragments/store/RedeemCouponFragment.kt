package com.xsolla.android.inventorysdkexample.ui.fragments.store

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.adapter.RedeemCouponItemsAdapter
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.inventorysdkexample.ui.vm.RedeemCouponResult
import com.xsolla.android.inventorysdkexample.ui.vm.VmCoupon
import kotlinx.android.synthetic.main.activity_store.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_redeem_coupon.*

class RedeemCouponFragment : BaseFragment() {

    private val viewModel: VmCoupon by viewModels()

    override fun getLayout() = R.layout.fragment_redeem_coupon

    override fun initUI() {
        requireActivity().appbar.mainToolbar.isGone = true
        couponInput.addTextChangedListener {
            redeemButton.isEnabled = !it.isNullOrBlank()
            couponLayout.isErrorEnabled = false
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe

            when (result) {
                is RedeemCouponResult.Failure -> {
                    couponLayout.error = result.message
                }
                is RedeemCouponResult.Success -> {
                    couponGroup.isVisible = false
                    receivedItemsGroup.isVisible = true

                    receivedItemsRecycler.adapter = RedeemCouponItemsAdapter(result.items)
                }
            }

        }

        redeemButton.setOnClickListener {
            hideKeyboard()
            viewModel.redeemCoupon(couponInput.text.toString())
        }
        redeemAnotherButton.setOnClickListener {
            couponInput.text?.clear()
            receivedItemsGroup.isVisible = false
            couponGroup.isVisible = true
        }
        cancelButton.setOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    override fun onDestroyView() {
        requireActivity().appbar.mainToolbar.isVisible = true
        super.onDestroyView()
    }
}
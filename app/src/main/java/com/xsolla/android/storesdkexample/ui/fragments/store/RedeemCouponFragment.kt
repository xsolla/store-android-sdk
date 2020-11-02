package com.xsolla.android.storesdkexample.ui.fragments.store

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.RedeemCouponItemsAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.RedeemCouponResult
import com.xsolla.android.storesdkexample.ui.vm.VmCoupon
import kotlinx.android.synthetic.main.activity_store.appbar
import kotlinx.android.synthetic.main.app_bar_main.view.toolbar
import kotlinx.android.synthetic.main.fragment_redeem_coupon.cancelButton
import kotlinx.android.synthetic.main.fragment_redeem_coupon.couponGroup
import kotlinx.android.synthetic.main.fragment_redeem_coupon.couponInput
import kotlinx.android.synthetic.main.fragment_redeem_coupon.couponLayout
import kotlinx.android.synthetic.main.fragment_redeem_coupon.receivedItemsGroup
import kotlinx.android.synthetic.main.fragment_redeem_coupon.receivedItemsRecycler
import kotlinx.android.synthetic.main.fragment_redeem_coupon.redeemAnotherButton
import kotlinx.android.synthetic.main.fragment_redeem_coupon.redeemButton
import kotlinx.android.synthetic.main.fragment_redeem_coupon.toolbar

class RedeemCouponFragment : BaseFragment() {

    private val viewModel: VmCoupon by viewModels()

    override fun getLayout() = R.layout.fragment_redeem_coupon

    override fun initUI() {
        requireActivity().appbar.toolbar.isGone = true
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
        requireActivity().appbar.toolbar.isVisible = true
        super.onDestroyView()
    }
}
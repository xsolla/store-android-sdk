package com.xsolla.android.storesdkexample.ui.fragments.store

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmCoupon
import kotlinx.android.synthetic.main.activity_store.appbar
import kotlinx.android.synthetic.main.app_bar_main.view.toolbar
import kotlinx.android.synthetic.main.fragment_redeem_coupon.couponInput
import kotlinx.android.synthetic.main.fragment_redeem_coupon.redeemButton

class RedeemCouponFragment : BaseFragment() {

    private val viewModel: VmCoupon by viewModels()

    override fun getLayout() = R.layout.fragment_redeem_coupon

    override fun initUI() {
        requireActivity().appbar.toolbar.isGone = true
        couponInput.addTextChangedListener { redeemButton.isEnabled = !it.isNullOrBlank() }

        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe


        }
    }

    override fun onDestroyView() {
        requireActivity().appbar.toolbar.isVisible = true
        super.onDestroyView()
    }
}
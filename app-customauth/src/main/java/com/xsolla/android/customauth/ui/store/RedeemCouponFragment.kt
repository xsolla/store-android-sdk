package com.xsolla.android.customauth.ui.store

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentRedeemCouponBinding
import com.xsolla.android.customauth.ui.BaseFragment
import com.xsolla.android.customauth.ui.adapter.RedeemCouponItemsAdapter
import com.xsolla.android.customauth.viewmodels.RedeemCouponResult
import com.xsolla.android.customauth.viewmodels.VmCoupon

class RedeemCouponFragment : BaseFragment() {
    private val binding: FragmentRedeemCouponBinding by viewBinding()
    private val viewModel: VmCoupon by viewModels()

    override fun getLayout() = R.layout.fragment_redeem_coupon
    override val toolbarOption: ToolbarOptions = ToolbarOptions(showMainToolbar = false)

    override fun initUI() {
        binding.couponInput.addTextChangedListener {
            binding.redeemButton.isEnabled = !it.isNullOrBlank()
            binding.couponLayout.isErrorEnabled = false
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe

            when (result) {
                is RedeemCouponResult.Failure -> {
                    binding.couponLayout.error = result.message
                }
                is RedeemCouponResult.Success -> {
                    binding.couponGroup.isVisible = false
                    binding.receivedItemsGroup.isVisible = true

                    binding.receivedItemsRecycler.adapter = RedeemCouponItemsAdapter(result.items)
                }
            }

        }

        binding.redeemButton.setOnClickListener {
            hideKeyboard()
            viewModel.redeemCoupon(binding.couponInput.text.toString())
        }
        binding.redeemAnotherButton.setOnClickListener {
            binding.couponInput.text?.clear()
            binding.receivedItemsGroup.isVisible = false
            binding.couponGroup.isVisible = true
        }
        binding.cancelButton.setOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }
}
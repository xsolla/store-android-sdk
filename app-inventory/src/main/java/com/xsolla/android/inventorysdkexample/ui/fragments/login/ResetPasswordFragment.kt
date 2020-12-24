package com.xsolla.android.inventorysdkexample.ui.fragments.login

import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.databinding.FragmenResetPasswordBinding
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.inventorysdkexample.util.ViewUtils
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.ResetPasswordCallback

class ResetPasswordFragment : BaseFragment() {

    private val binding: FragmenResetPasswordBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragmen_reset_password
    }

    override fun initUI() {
        binding.backButton.setOnClickListener { openLoginFragment() }
        binding.usernameInput.addTextChangedListener { binding.resetPasswordButton.isEnabled = !it.isNullOrBlank() }

        binding.resetPasswordButton.setOnClickListener { v ->
            ViewUtils.disable(v)
            hideKeyboard()
            val username = binding.usernameInput.text.toString()
            XLogin.resetPassword(username, object : ResetPasswordCallback {
                override fun onSuccess() {
                    showSnack("Password reset success. Check your email")
                    openLoginFragment()
                    ViewUtils.enable(v)
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                    ViewUtils.enable(v)
                }

            })
        }
    }

    private fun openLoginFragment() {
        activity?.supportFragmentManager?.popBackStack()
    }
}
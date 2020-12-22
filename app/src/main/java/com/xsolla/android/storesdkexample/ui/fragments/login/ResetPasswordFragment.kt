package com.xsolla.android.storesdkexample.ui.fragments.login

import android.text.Editable
import android.text.TextWatcher
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.ResetPasswordCallback
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.databinding.FragmenResetPasswordBinding
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.ViewUtils

class ResetPasswordFragment : BaseFragment() {

    private val binding: FragmenResetPasswordBinding by viewBinding()

    override fun getLayout() = R.layout.fragmen_reset_password

    override fun initUI() {
        binding.backButton.setOnClickListener { openLoginFragment() }
        binding.usernameInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable) {
                binding.resetPasswordButton.isEnabled = s.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

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
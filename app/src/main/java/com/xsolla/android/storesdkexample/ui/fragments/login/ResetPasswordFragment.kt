package com.xsolla.android.storesdkexample.ui.fragments.login

import android.text.Editable
import android.text.TextWatcher
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.ResetPasswordCallback
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.fragmen_reset_password.view.*

class ResetPasswordFragment : BaseFragment() {

    override fun getLayout(): Int {
        return R.layout.fragmen_reset_password
    }

    override fun initUI() {
        rootView.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        rootView.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        rootView.usernameInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable) {
                rootView.resetPasswordButton.isEnabled = s.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        rootView.resetPasswordButton.setOnClickListener { v ->
            ViewUtils.disable(v)
            hideKeyboard()
            val username = rootView.usernameInput.text.toString()
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
        activity?.let {
            it.supportFragmentManager.popBackStack()
        }
    }
}
package com.xsolla.android.storesdkexample.fragments

import com.xsolla.android.login.XLogin
import com.xsolla.android.login.api.XLoginCallback
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.fragmen_reset_password.view.*

class ResetPasswordFragment : BaseFragment() {

    override fun getLayout(): Int {
        return R.layout.fragmen_reset_password
    }

    override fun initUI() {
        rootView.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        rootView.toolbar.setNavigationOnClickListener { popFragment() }

        rootView.resetPasswordButton.setOnClickListener { v ->
            ViewUtils.disable(v)
            hideKeyboard()
            val username = rootView.usernameInput.text.toString()
            XLogin.resetPassword(username, object : XLoginCallback<Void?>() {
                override fun onSuccess(response: Void?) {
                    showSnack("Password reset success. Check your email")
                    openLoginFragment()
                    ViewUtils.enable(v)
                }

                override fun onFailure(errorMessage: String) {
                    showSnack(errorMessage)
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
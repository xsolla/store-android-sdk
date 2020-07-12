package com.xsolla.android.storesdkexample.fragments

import com.xsolla.android.login.XLogin
import com.xsolla.android.login.api.XLoginCallback
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*

class SignUpFragment : BaseFragment() {

    override fun getLayout(): Int {
        return R.layout.fragment_sign_up
    }

    override fun initUI() {
        rootView.signUpButton.setOnClickListener { v ->
            hideKeyboard()
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            XLogin.register(username, email, password, object : XLoginCallback<Void?>() {
                override fun onSuccess(response: Void?) {
                    showSnack("Registration success. Please check your email")
                    openFragment(LoginFragment())
                    activity?.tabLayout?.getTabAt(0)?.select()
                    ViewUtils.enable(v)
                }

                override fun onFailure(errorMessage: String) {
                    showSnack(errorMessage)
                    ViewUtils.enable(v)                }
            })
        }
    }
}
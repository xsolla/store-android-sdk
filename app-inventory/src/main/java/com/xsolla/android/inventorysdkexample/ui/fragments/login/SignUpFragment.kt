package com.xsolla.android.inventorysdkexample.ui.fragments.login

import android.text.Editable
import android.text.TextWatcher
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.inventorysdkexample.util.ViewUtils
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.RegisterCallback
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*

class SignUpFragment : BaseFragment() {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sign_up
    }

    override fun initUI() {
        initLoginButtonEnabling()

        rootView.signUpButton.setOnClickListener { v ->

            ViewUtils.disable(v)
            hideKeyboard()
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            XLogin.register(username, email, password, object : RegisterCallback {
                override fun onSuccess() {
                    showSnack("Registration success. Please check your email")
                    parentFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, LoginFragment())
                            .commit()
                    activity?.tabLayout?.getTabAt(0)?.select()
                    ViewUtils.enable(v)
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                    ViewUtils.enable(v)
                }
            })
        }
    }

    private fun initLoginButtonEnabling() {
        rootView.usernameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        rootView.emailInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        rootView.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        rootView.passwordConfirmInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun updateLoginButtonEnable() {
        rootView.signUpButton.isEnabled = rootView.usernameInput.text?.isNotEmpty() == true
                && rootView.emailInput.text?.isNotEmpty() == true
                && (rootView.passwordInput.text?.length!! >= MIN_PASSWORD_LENGTH) == true
                && (rootView.passwordConfirmInput.text?.length!! >= MIN_PASSWORD_LENGTH) == true
                && (rootView.passwordInput.text?.toString() == rootView.passwordConfirmInput.text?.toString())
    }

}
package com.xsolla.android.inventorysdkexample.ui.fragments.login

import androidx.core.widget.addTextChangedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import com.xsolla.android.appcore.databinding.FragmentSignUpBinding
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.RegisterCallback

class SignUpFragment : BaseFragment() {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
    }

    private val binding: FragmentSignUpBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragment_sign_up
    }

    override fun initUI() {
        initLoginButtonEnabling()

        binding.signUpButton.setOnClickListener { v ->

            v.isEnabled = false
            hideKeyboard()
            val username = binding.usernameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            XLogin.register(username, email, password, object : RegisterCallback {
                override fun onSuccess() {
                    showSnack("Registration success. Please check your email")
                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.authFragmentContainer, LoginFragment())
                        .commit()
                    activity?.findViewById<TabLayout>(R.id.tabLayout)?.getTabAt(0)?.select()
                    v.isEnabled = true
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                    v.isEnabled = true
                }
            })
        }
    }

    private fun initLoginButtonEnabling() {
        binding.usernameInput.addTextChangedListener { updateLoginButtonEnable() }
        binding.emailInput.addTextChangedListener { updateLoginButtonEnable() }
        binding.passwordInput.addTextChangedListener { updateLoginButtonEnable() }
        binding.passwordConfirmInput.addTextChangedListener { updateLoginButtonEnable() }
    }

    private fun updateLoginButtonEnable() {
        binding.signUpButton.isEnabled = binding.usernameInput.text?.isNotEmpty() == true
                && binding.emailInput.text?.isNotEmpty() == true
                && (binding.passwordInput.text?.length!! >= MIN_PASSWORD_LENGTH) == true
                && (binding.passwordConfirmInput.text?.length!! >= MIN_PASSWORD_LENGTH) == true
                && (binding.passwordInput.text?.toString() == binding.passwordConfirmInput.text?.toString())
    }

}
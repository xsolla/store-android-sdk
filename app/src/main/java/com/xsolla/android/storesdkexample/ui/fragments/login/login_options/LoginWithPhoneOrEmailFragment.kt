package com.xsolla.android.storesdkexample.ui.fragments.login.login_options

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentLogInWithPhoneOrEmailBinding
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.StartPasswordlessAuthCallback
import com.xsolla.android.login.entity.response.StartPasswordlessAuthResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment

class LoginWithPhoneOrEmailFragment : BaseFragment() {

    enum class Type {
        EMAIL, PHONE
    }

    companion object {
        fun getInstance(type: Type): LoginWithPhoneOrEmailFragment {
            val fragment = LoginWithPhoneOrEmailFragment()
            fragment.arguments = bundleOf(
                "type" to type
            )
            return fragment
        }
    }

    private val binding: FragmentLogInWithPhoneOrEmailBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragment_log_in_with_phone_or_email
    }

    override fun initUI() {

        val type = requireArguments().getSerializable("type") as Type

        when (type) {
            Type.EMAIL -> binding.phoneOrEmailInput.inputType = InputType.TYPE_CLASS_TEXT
            Type.PHONE -> binding.phoneOrEmailInput.inputType = InputType.TYPE_CLASS_PHONE
        }

        when (type) {
            Type.EMAIL -> binding.textInputLayout.hint = getString(R.string.login_email_input_hint)
            Type.PHONE -> binding.textInputLayout.hint = getString(R.string.login_phone_input_hint)
        }

        binding.btBack.setOnClickListener {
            hideKeyboard()
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.phoneOrEmailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val text = binding.phoneOrEmailInput.text
                if (text == null) {
                    binding.btSendCode.isEnabled = false
                    return
                }
                when (type) {
                    Type.EMAIL -> binding.btSendCode.isEnabled =
                        text.contains('@') && text.contains('.')
                    Type.PHONE -> binding.btSendCode.isEnabled = text.length >= 8
                }
            }
        })
        binding.btSendCode.setOnClickListener {
            val phoneOrEmail = binding.phoneOrEmailInput.text.toString()
            hideKeyboard()
            val callback = object : StartPasswordlessAuthCallback {
                override fun onAuthStarted(data: StartPasswordlessAuthResponse) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(
                            R.id.rootFragmentContainer,
                            LoginEnterCodeFragment.getInstance(
                                when (type) {
                                    Type.EMAIL -> LoginEnterCodeFragment.Type.EMAIL
                                    Type.PHONE -> LoginEnterCodeFragment.Type.PHONE
                                },
                                phoneOrEmail, data.operationId
                            )
                        )
                        .addToBackStack(null)
                        .commit()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                }
            }
            when (type) {
                Type.EMAIL -> XLogin.startAuthByEmail(
                    phoneOrEmail,
                    callback = callback, sendLink = true, linkUrl = "app://xsollaconfirm"
                )
                Type.PHONE -> XLogin.startAuthByMobilePhone(
                    phoneOrEmail,
                    callback = callback, sendLink = true, linkUrl = "app://xsollaconfirm"
                )
            }

        }
    }
}
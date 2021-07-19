package com.xsolla.android.storesdkexample.ui.fragments.login.login_options

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.R
import com.xsolla.android.appcore.databinding.FragmentLogInWithPhoneEnterCodeBinding
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.CompleteAuthByPhoneCallback
import com.xsolla.android.storesdkexample.StoreActivity
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment

class LoginEnterCodeFragment(
    val operationId: String,
    val phoneNumber: String
) : BaseFragment() {

    private val binding: FragmentLogInWithPhoneEnterCodeBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragment_log_in_with_phone_enter_code
    }

    override fun initUI() {

        binding.codeInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.codeInput.text!!.length ==4){
                    binding.btLogIn.isEnabled = true
                }
            }
        })

        binding.btLogIn.setOnClickListener {
            val code = binding.codeInput.text.toString()
            XLogin.completeAuthByMobilePhone(phoneNumber,code,operationId,object : CompleteAuthByPhoneCallback{
                override fun onSuccess() {
                    val intent = Intent(requireActivity(), StoreActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                }
            })
        }
    }
}
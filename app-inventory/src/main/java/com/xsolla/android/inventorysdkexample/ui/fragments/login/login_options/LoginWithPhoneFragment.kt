package com.xsolla.android.inventorysdkexample.ui.fragments.login.login_options

import android.text.Editable
import android.text.TextWatcher
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentLogInWithPhoneBinding
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.StartAuthByPhoneCallback
import com.xsolla.android.login.entity.response.StartAuthByPhoneResponse

class LoginWithPhoneFragment: BaseFragment() {
    private val binding : FragmentLogInWithPhoneBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragment_log_in_with_phone
    }

    override fun initUI() {
        binding.btBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.phoneInput.addTextChangedListener(  object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.phoneInput.text!!.length>=8){ //min length is +290XXXX - St.Helena isles
                    binding.btSendCode.isEnabled=true
                }
            }
        } )
        binding.btSendCode.setOnClickListener {
                val phone = binding.phoneInput.text.toString()
            hideKeyboard()
            XLogin.startAuthByMobilePhone(phone,object : StartAuthByPhoneCallback{
                override fun onAuthStarted(data: StartAuthByPhoneResponse) {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.rootFragmentContainer, LoginEnterCodeFragment(data.operationId,phone))
                        .addToBackStack(null)
                        .commit()
                }


                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                }
            })
        }
    }
}
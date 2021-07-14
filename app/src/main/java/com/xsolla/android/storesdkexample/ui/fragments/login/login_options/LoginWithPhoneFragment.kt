package com.xsolla.android.storesdkexample.ui.fragments.login.login_options

import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentLogInWithPhoneBinding

import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment

class LoginWithPhoneFragment: BaseFragment() {
    private val binding : FragmentLogInWithPhoneBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragment_log_in_with_phone
    }

    override fun initUI() {
        binding.btBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}
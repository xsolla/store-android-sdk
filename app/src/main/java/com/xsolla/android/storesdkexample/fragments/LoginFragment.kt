package com.xsolla.android.storesdkexample.fragments

import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : BaseFragment() {

    override fun getLayout(): Int {
        return R.layout.fragment_login
    }

    override fun initUI() {
        rootView.resetPasswordButton.setOnClickListener { restPassword() }
    }

    private fun restPassword() {
        activity?.let {
            it.supportFragmentManager
                    .beginTransaction()
                    .add(R.id.rootFragmentContainer, ResetPasswordFragmentNew())
                    .addToBackStack(null)
                    .commit()
        }
    }

}
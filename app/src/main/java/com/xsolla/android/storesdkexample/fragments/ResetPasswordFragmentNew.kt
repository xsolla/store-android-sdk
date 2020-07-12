package com.xsolla.android.storesdkexample.fragments

import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragmen_reset_password_new.view.*

class ResetPasswordFragmentNew : BaseFragment() {

    override fun getLayout(): Int {
        return R.layout.fragmen_reset_password_new
    }

    override fun initUI() {
        rootView.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        rootView.toolbar.setNavigationOnClickListener { popFragment() }
    }
}
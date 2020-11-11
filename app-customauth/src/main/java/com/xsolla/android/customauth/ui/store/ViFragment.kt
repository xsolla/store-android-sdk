package com.xsolla.android.customauth.ui.store

import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentViBinding
import com.xsolla.android.customauth.ui.BaseFragment

class ViFragment : BaseFragment() {
    private val binding: FragmentViBinding by viewBinding()

    override fun getLayout() = R.layout.fragment_vi

    override fun initUI() {
    }
}
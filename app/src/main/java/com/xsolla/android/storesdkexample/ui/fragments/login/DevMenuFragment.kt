package com.xsolla.android.storesdkexample.ui.fragments.login

import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentDevMenuBinding
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment

class DevMenuFragment : BaseFragment() {

    private val binding: FragmentDevMenuBinding by viewBinding()

    override fun getLayout(): Int = R.layout.fragment_dev_menu

    override fun initUI() {
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_white_24dp)
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
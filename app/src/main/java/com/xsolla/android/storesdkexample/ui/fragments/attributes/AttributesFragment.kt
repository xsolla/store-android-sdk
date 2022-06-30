package com.xsolla.android.storesdkexample.ui.fragments.attributes

import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.appcore.databinding.FragmentAttributesBinding
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.AttributesPageAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmAttributesPage
import com.xsolla.android.storesdkexample.ui.vm.VmBalance

class AttributesFragment : BaseFragment() {
    private val binding: FragmentAttributesBinding by viewBinding()

    private val viewModel: VmAttributesPage by activityViewModels()
    private val balanceViewModel: VmBalance by activityViewModels()

    override fun getLayout() = R.layout.fragment_attributes

    override fun initUI() {
        balanceViewModel.virtualBalance.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val vc = it.first()
                viewModel.virtualCurrency = vc
            }
        }

        viewModel.loadAllAttributes()

        binding.viewPager.adapter = AttributesPageAdapter(this)
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            if (position == 0) {
                tab.setText(R.string.attributes_tab1)
            } else if (position == 1) {
                tab.setText(R.string.attributes_tab2)
            }
        }.attach()

        viewModel.error.observe(viewLifecycleOwner) {
            showSnack(it.message)
        }
    }

}
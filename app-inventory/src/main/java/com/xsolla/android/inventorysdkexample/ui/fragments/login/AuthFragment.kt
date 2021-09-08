package com.xsolla.android.inventorysdkexample.ui.fragments.login

import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.appcore.databinding.FragmentAuthBinding
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment

class AuthFragment : BaseFragment() {
    private val binding: FragmentAuthBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragment_auth
    }

    override fun initUI() {
        initTabs()

        parentFragmentManager.beginTransaction().replace(R.id.authFragmentContainer, LoginFragment()).commit()
    }

    private fun initTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when(tab.position) {
                    0 -> LoginFragment()
                    1 -> SignUpFragment()
                    else -> null
                }

                fragment?.let { newFragment ->
                    binding.authFragmentContainer.removeAllViews()
                    parentFragmentManager
                            .beginTransaction()
                            .replace(R.id.authFragmentContainer, newFragment)
                            .commit()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })
    }
}
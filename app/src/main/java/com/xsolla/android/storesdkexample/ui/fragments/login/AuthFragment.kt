package com.xsolla.android.storesdkexample.ui.fragments.login

import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import com.xsolla.android.appcore.databinding.FragmentAuthBinding
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment

class AuthFragment : BaseFragment() {
    private val binding: FragmentAuthBinding by viewBinding()

    override fun getLayout() = R.layout.fragment_auth

    override fun initUI() {
        initTabs()

        parentFragmentManager.beginTransaction()
            .replace(R.id.authFragmentContainer, LoginFragment()).commit()

        binding.logo.setOnClickListener {
            logoClick()
        }
    }

    private fun initTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when (tab.position) {
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

    override fun onResume() {
        super.onResume()
        logoClickCounter = 0
    }

    private var logoClickCounter = 0

    private fun logoClick() {
        logoClickCounter++
        if (logoClickCounter == CLICKS_TO_OPEN_DEV_MENU) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.rootFragmentContainer, DevMenuFragment())
                .addToBackStack("dev_menu")
                .commit()
            logoClickCounter = 0
        }
    }

    companion object {
        const val CLICKS_TO_OPEN_DEV_MENU = 8
    }

}
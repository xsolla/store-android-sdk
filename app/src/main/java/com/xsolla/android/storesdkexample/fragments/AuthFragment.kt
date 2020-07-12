package com.xsolla.android.storesdkexample.fragments

import com.google.android.material.tabs.TabLayout
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.fragment_auth.view.*

class AuthFragment : BaseFragment() {
    override fun getLayout(): Int {
        return R.layout.fragment_auth
    }

    override fun initUI() {
        initTabs()
    }

    private fun initTabs() {
        rootView.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when(tab.position) {
                    0 -> LoginFragment()
                    1 -> SignUpFragment()
                    else -> null
                }

                fragment?.let { newFragment ->
                    fragmentContainer.removeAllViews()
                    fragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.fragmentContainer, newFragment)
                            ?.addToBackStack(null)
                            ?.commit()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
        })
    }
}
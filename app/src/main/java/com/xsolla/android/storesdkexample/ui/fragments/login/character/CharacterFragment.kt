package com.xsolla.android.storesdkexample.ui.fragments.login.character

import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.CharacterPageAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_character.tabs
import kotlinx.android.synthetic.main.fragment_character.viewPager

class CharacterFragment : BaseFragment() {
    override fun getLayout() = R.layout.fragment_character

    override fun initUI() {
        viewPager.isUserInputEnabled = false
        viewPager.adapter = CharacterPageAdapter(this)

        TabLayoutMediator(tabs, viewPager) {_, _ -> }.attach()
    }

}
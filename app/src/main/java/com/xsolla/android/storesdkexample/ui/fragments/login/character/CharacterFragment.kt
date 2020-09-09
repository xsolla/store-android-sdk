package com.xsolla.android.storesdkexample.ui.fragments.login.character

import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.CharacterPageAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmCharacterPage
import kotlinx.android.synthetic.main.fragment_character.tabs
import kotlinx.android.synthetic.main.fragment_character.viewPager

class CharacterFragment : BaseFragment() {
    private val viewModel: VmCharacterPage by activityViewModels()

    override fun getLayout() = R.layout.fragment_character

    override fun initUI() {
        viewModel.loadAll()

        viewPager.isUserInputEnabled = false
        viewPager.adapter = CharacterPageAdapter(this)

        TabLayoutMediator(tabs, viewPager) {_, _ -> }.attach()
    }

}
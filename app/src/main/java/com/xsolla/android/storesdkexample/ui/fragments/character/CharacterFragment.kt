package com.xsolla.android.storesdkexample.ui.fragments.character

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.CharacterPageAdapter
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.UserInformation
import com.xsolla.android.storesdkexample.ui.vm.VmCharacterPage
import kotlinx.android.synthetic.main.fragment_character.avatar
import kotlinx.android.synthetic.main.fragment_character.nickname
import kotlinx.android.synthetic.main.fragment_character.tabs
import kotlinx.android.synthetic.main.fragment_character.viewPager

class CharacterFragment : BaseFragment() {
    private val viewModel: VmCharacterPage by activityViewModels()

    override fun getLayout() = R.layout.fragment_character

    override fun initUI() {
        viewModel.getUserDetailsAndAttributes()

        viewPager.isUserInputEnabled = false
        viewPager.adapter = CharacterPageAdapter(this)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            if (position == 0) {
                tab.setText(R.string.character_tab1)
            } else if (position == 1) {
                tab.setText(R.string.character_tab2)
            }
        }.attach()

        viewModel.userInformation.observe(viewLifecycleOwner) {
            setupUserInformation(it)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            showSnack(it.message)
        }
    }

    private fun setupUserInformation(user: UserInformation) {
        nickname.text = user.nickname

        Glide.with(requireContext())
            .load(user.avatar)
            .apply(circleCropTransform())
            .placeholder(R.drawable.ic_xsolla_logo)
            .error(R.drawable.ic_xsolla_logo)
            .into(avatar)
    }

}
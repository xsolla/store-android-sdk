package com.xsolla.android.storesdkexample.ui.fragments.character

import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.activityViewModels
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

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onDestroyView() {
        requireActivity().invalidateOptionsMenu()
        super.onDestroyView()
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
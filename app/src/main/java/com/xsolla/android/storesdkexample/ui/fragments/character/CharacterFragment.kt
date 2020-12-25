package com.xsolla.android.storesdkexample.ui.fragments.character

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.tabs.TabLayoutMediator
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.CharacterPageAdapter
import com.xsolla.android.storesdkexample.data.local.PrefManager
import com.xsolla.android.appcore.databinding.FragmentCharacterBinding
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.UserInformation
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmCharacterPage
import com.xsolla.android.storesdkexample.util.extensions.dpToPx

class CharacterFragment : BaseFragment() {
    private val binding: FragmentCharacterBinding by viewBinding()

    private val viewModel: VmCharacterPage by activityViewModels()
    private val balanceViewModel: VmBalance by activityViewModels()

    override fun getLayout() = R.layout.fragment_character

    override fun initUI() {
        balanceViewModel.virtualBalance.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val vc = it.first()
                viewModel.virtualCurrency = vc
                configureLevelUpButton(vc.imageUrl)
            }
        }

        viewModel.getUserDetailsAndAttributes()

        binding.viewPager.adapter = CharacterPageAdapter(this)
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
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

        binding.levelUpButton.setOnClickListener {
            viewModel.levelUp {
                balanceViewModel.updateVirtualBalance()
                binding.level.text = getString(R.string.character_lvl, PrefManager.getUserLevel(viewModel.userInformation.value!!.id))
            }
        }
    }

    private fun configureLevelUpButton(imageUrl: String?) {
        val sizeCurrency = 24.dpToPx()
        val upIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_level_up)
        binding.levelUpButton.setText(R.string.character_lvl_up)

        Glide.with(this)
            .load(imageUrl)
            .override(sizeCurrency, sizeCurrency)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    binding.levelUpButton.setCompoundDrawablesWithIntrinsicBounds(upIcon, null, null, null)
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    binding.levelUpButton.setCompoundDrawablesWithIntrinsicBounds(upIcon, null, resource, null)
                    return true
                }
            })
            .submit()
    }

    private fun setupUserInformation(user: UserInformation) {
        binding.nickname.text = user.nickname

        if (user.id.isNotBlank()) {
            binding.level.text = getString(R.string.character_lvl, PrefManager.getUserLevel(user.id))
        } else {
            binding.level.setText(R.string.character_1lvl)
        }

        Glide.with(this)
            .load(user.avatar)
            .circleCrop()
            .placeholder(R.drawable.ic_default_avatar)
            .error(R.drawable.ic_default_avatar)
            .into(binding.avatar)
    }

}
package com.xsolla.android.storesdkexample.ui.fragments.login

import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentDevMenuBinding
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmDevMenu

class DevMenuFragment : BaseFragment() {

    private val binding: FragmentDevMenuBinding by viewBinding()

    private val vmDevMenu: VmDevMenu by viewModels()

    override fun getLayout(): Int = R.layout.fragment_dev_menu

    override fun initUI() {
        // Toolbar
        binding.toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back_white_24dp)
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Input Fields
        binding.oauth2Id.doOnTextChanged { text, _, _, _ ->
            vmDevMenu.oauthClientId.value = text!!.toString()
        }
        vmDevMenu.oauthClientId.distinctUntilChanged().observe(this, {
            if (binding.oauth2Id.text!!.toString() != it) {
                binding.oauth2Id.text = SpannableStringBuilder(it)
            }
        })
        binding.loginId.doOnTextChanged { text, _, _, _ ->
            vmDevMenu.loginId.value = text!!.toString()
        }
        vmDevMenu.loginId.distinctUntilChanged().observe(this, {
            if (binding.loginId.text!!.toString() != it) {
                binding.loginId.text = SpannableStringBuilder(it)
            }
        })
        binding.projectId.doOnTextChanged { text, _, _, _ ->
            vmDevMenu.projectId.value = text!!.toString()
        }
        vmDevMenu.projectId.distinctUntilChanged().observe(this, {
            if (binding.projectId.text!!.toString() != it) {
                binding.projectId.text = SpannableStringBuilder(it)
            }
        })
        binding.webshopUrl.doOnTextChanged { text, _, _, _ ->
            vmDevMenu.webshopUrl.value = text!!.toString()
        }
        vmDevMenu.webshopUrl.distinctUntilChanged().observe(this, {
            if (binding.webshopUrl.text!!.toString() != it) {
                binding.webshopUrl.text = SpannableStringBuilder(it)
            }
        })

        // Buttons
        vmDevMenu.isReadyToApply.observe(this) {
            binding.applyButton.isEnabled = it
        }
        binding.applyButton.setOnClickListener {
            vmDevMenu.apply()
        }
        binding.resetButton.setOnClickListener {
            vmDevMenu.resetToDefaults()
        }
    }
}
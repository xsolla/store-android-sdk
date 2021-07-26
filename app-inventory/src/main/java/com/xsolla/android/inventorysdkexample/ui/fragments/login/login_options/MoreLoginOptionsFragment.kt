package com.xsolla.android.inventorysdkexample.ui.fragments.login.login_options

import android.content.Intent
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentMoreLogInOptionsBinding
import com.xsolla.android.inventorysdkexample.BuildConfig
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.StoreActivity
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback


class MoreLoginOptionsFragment : BaseFragment() {

    private val binding : FragmentMoreLogInOptionsBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragment_more_log_in_options
    }

    override fun initUI() {

        binding.ibCloseFragment.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.bnLoginWithPhone.setOnClickListener {

            //requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()

            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.rootFragmentContainer, LoginWithPhoneFragment())
                .addToBackStack(null)
                .commit()

        }
        binding.bnLoginAsDemoUser.setOnClickListener {
            binding.bnLoginAsDemoUser.isEnabled = false

            XLogin.login("xsolla", "xsolla", object : AuthCallback {
                override fun onSuccess() {
                    val intent = Intent(requireActivity(), StoreActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                    binding.bnLoginAsDemoUser.isEnabled = true
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                    binding.bnLoginAsDemoUser.isEnabled = true
                }

            }, BuildConfig.WITH_LOGOUT)
        }
    }
}
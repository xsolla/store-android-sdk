package com.xsolla.android.storesdkexample.ui.fragments.login.login_options

import android.content.Intent
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentMoreLogInOptionsBinding
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.StoreActivity
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment

class MoreLoginOptionsFragment : BaseFragment() {

    private val binding: FragmentMoreLogInOptionsBinding by viewBinding()

    override fun getLayout(): Int {
        return R.layout.fragment_more_log_in_options
    }

    override fun initUI() {

        binding.ibCloseFragment.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.bnLoginWithPhone.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.rootFragmentContainer,
                    LoginWithPhoneOrEmailFragment.getInstance(LoginWithPhoneOrEmailFragment.Type.PHONE)
                )
                .addToBackStack(null)
                .commit()
        }
        binding.bnPassworldlessAuth.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.rootFragmentContainer,
                    LoginWithPhoneOrEmailFragment.getInstance(LoginWithPhoneOrEmailFragment.Type.EMAIL)
                )
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

            })
        }
    }
}
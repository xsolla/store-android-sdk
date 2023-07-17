package com.xsolla.android.storesdkexample.ui.fragments.login.login_options

import android.content.Intent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.FragmentMoreLogInOptionsBinding
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.*
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.StoreActivity
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.login.loginAsDemoUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            binding.loader.isVisible = true
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val error = loginAsDemoUser(requireContext())
                    withContext(Dispatchers.Main) {
                        binding.loader.isVisible = false
                        if (error != null) {
                            showSnack(error)
                        } else {
                            val intent = Intent(requireActivity(), StoreActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                    }
                }
            }
        }
        binding.bnLoginWithDeviceId.setOnClickListener {
            binding.loader.isVisible = true
            XLogin.authenticateViaDeviceId(object : AuthViaDeviceIdCallback {
                override fun onSuccess() {
                    binding.loader.isVisible = false
                    val intent = Intent(requireActivity(), StoreActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    binding.loader.isVisible = false
                    showSnack(
                        errorMessage ?: throwable?.message ?: throwable?.javaClass?.name ?: "Error"
                    )
                }
            })
        }

        binding.bnLoginWithXsollaWidget.setOnClickListener {
            XLogin.startAuthWithXsollaWidget(
                this,
                object : StartXsollaWidgetAuthCallback {
                    override fun onAuthStarted() {
                        // auth successfully started
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        showSnack(
                            errorMessage ?: throwable?.message ?: throwable?.javaClass?.name ?: "Error"
                        )
                    }
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        XLogin.finishAuthWithXsollaWidget(
            requireActivity(),
            requestCode,
            resultCode,
            data,
            finishWithXsollaWidgetAuthCallback
        )
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val finishWithXsollaWidgetAuthCallback: FinishXsollaWidgetAuthCallback = object : FinishXsollaWidgetAuthCallback {
        override fun onAuthSuccess() {
            val intent = Intent(requireActivity(), StoreActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        override fun onAuthCancelled() {
            showSnack("Auth cancelled")
        }

        override fun onAuthError(throwable: Throwable?, errorMessage: String?) {
            showSnack(errorMessage?: throwable?.javaClass?.name ?: throwable?.localizedMessage ?: "Error")
        }
    }
}
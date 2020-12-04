package com.xsolla.android.inventorysdkexample.ui.fragments.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.xsolla.android.inventorysdkexample.BuildConfig
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.StoreActivity
import com.xsolla.android.inventorysdkexample.TutorialActivity
import com.xsolla.android.inventorysdkexample.data.local.PrefManager
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.inventorysdkexample.util.ViewUtils
import com.xsolla.android.inventorysdkexample.util.setRateLimitedClickListener
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.social.SocialNetwork
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import java.util.*

class LoginFragment : BaseFragment() {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
        private val POLICY_LANGUAGES = listOf("de", "ko", "zh", "ja", "ru")
    }

    private var selectedSocialNetwork: SocialNetwork? = null

    override fun getLayout(): Int {
        return R.layout.fragment_login
    }

    override fun initUI() {
        initLoginButtonEnabling()

        rootView.loginButton.setOnClickListener { v ->
            ViewUtils.disable(v)

            hideKeyboard()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            XLogin.authenticate(username, password, BuildConfig.WITH_LOGOUT, object : AuthCallback {
                override fun onSuccess() {
                    val intent = Intent(requireActivity(), StoreActivity::class.java)
                    startActivity(intent)
                    if (!PrefManager.getHideTutorial()) {
                        startTutorial()
                    }
                    activity?.finish()
                    ViewUtils.enable(v)
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                    ViewUtils.enable(v)
                }

            })
        }

        rootView.googleButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.GOOGLE
            XLogin.startSocialAuth(this, SocialNetwork.GOOGLE, BuildConfig.WITH_LOGOUT, startSocialCallback)
        }

        rootView.facebookButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.FACEBOOK
            XLogin.startSocialAuth(this, SocialNetwork.FACEBOOK, BuildConfig.WITH_LOGOUT, startSocialCallback)
        }

        rootView.twitterButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.TWITTER
            XLogin.startSocialAuth(this, SocialNetwork.TWITTER, BuildConfig.WITH_LOGOUT, startSocialCallback)
        }

        rootView.baiduButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.BAIDU
            XLogin.startSocialAuth(this, SocialNetwork.BAIDU, BuildConfig.WITH_LOGOUT, startSocialCallback)
        }

        rootView.naverButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.NAVER
            XLogin.startSocialAuth(this, SocialNetwork.NAVER, BuildConfig.WITH_LOGOUT, startSocialCallback)
        }

        rootView.resetPasswordButton.setOnClickListener { resetPassword() }

        rootView.privacyPolicyButton.setOnClickListener { showPrivacyPolicy() }
    }

    private fun initLoginButtonEnabling() {
        rootView.usernameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        rootView.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun updateLoginButtonEnable() {
        val usernameValid = rootView.usernameInput.text?.isNotEmpty() ?: false
        val passwordValid = (rootView.passwordInput.text?.length ?: 0) >= MIN_PASSWORD_LENGTH
        rootView.loginButton.isEnabled = usernameValid && passwordValid
    }

    private fun resetPassword() {
        requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.rootFragmentContainer, ResetPasswordFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun showPrivacyPolicy() {
        val lang = Locale.getDefault().language
        val url = if (POLICY_LANGUAGES.contains(lang)) {
            "https://xsolla.com/$lang/privacypolicy"
        } else {
            "https://xsolla.com/privacypolicy"
        }
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        XLogin.finishSocialAuth(context, selectedSocialNetwork, requestCode, resultCode, data, BuildConfig.WITH_LOGOUT, finishSocialCallback)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val startSocialCallback: StartSocialCallback = object : StartSocialCallback {
        override fun onAuthStarted() {
            // auth successfully started
        }

        override fun onError(throwable: Throwable?, errorMessage: String?) {
            showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
        }
    }

    private val finishSocialCallback: FinishSocialCallback = object : FinishSocialCallback {
        override fun onAuthSuccess() {
            val intent = Intent(requireActivity(), StoreActivity::class.java)
            startActivity(intent)
            if (!PrefManager.getHideTutorial()) {
                startTutorial()
            }
            activity?.finish()
        }

        override fun onAuthCancelled() {
            showSnack("Auth cancelled")
        }

        override fun onAuthError(throwable: Throwable?, errorMessage: String?) {
            showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedSocialNetwork?.let {
            outState.putString("selectedSocialNetwork", it.name)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let { state ->
            state.getString("selectedSocialNetwork")?.let {
                selectedSocialNetwork = SocialNetwork.valueOf(it)
            }
        }
    }

    private fun startTutorial() {
        val intent = Intent(activity, TutorialActivity::class.java)
        intent.putExtra(TutorialActivity.EXTRA_MANUAL_RUN, false)
        startActivity(intent)
    }

}
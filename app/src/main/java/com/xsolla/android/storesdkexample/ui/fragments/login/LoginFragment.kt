package com.xsolla.android.storesdkexample.ui.fragments.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.xsolla.android.appcore.LoginBottomSheet
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.StoreActivity
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.setRateLimitedClickListener
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*

class LoginFragment : BaseFragment(), LoginBottomSheet.SocialClickListener {

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

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            loginWithPassword(username, password)
        }

        demoUserButton.setOnClickListener {
            loginWithPassword("xsolla", "xsolla")
        }

        googleButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.GOOGLE
            XLogin.startSocialAuth(this, SocialNetwork.GOOGLE, BuildConfig.WITH_LOGOUT, startSocialCallback)
        }

        facebookButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.FACEBOOK
            XLogin.startSocialAuth(this, SocialNetwork.FACEBOOK, BuildConfig.WITH_LOGOUT, startSocialCallback)
        }

        baiduButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.BAIDU
            XLogin.startSocialAuth(this, SocialNetwork.BAIDU, BuildConfig.WITH_LOGOUT, startSocialCallback)
        }

        moreButton.setRateLimitedClickListener {
            LoginBottomSheet.newInstance().show(childFragmentManager, "moreSocials")
        }

        resetPasswordButton.setOnClickListener { resetPassword() }

        privacyPolicyButton.setOnClickListener { showPrivacyPolicy() }
    }

    private fun initLoginButtonEnabling() {
        usernameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        passwordInput.addTextChangedListener(object : TextWatcher {
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
        val usernameValid = usernameInput.text?.isNotEmpty() ?: false
        val passwordValid = (passwordInput.text?.length ?: 0) >= MIN_PASSWORD_LENGTH
        loginButton.isEnabled = usernameValid && passwordValid
    }

    private fun loginWithPassword(username: String, password: String) {
        loginButton.isEnabled = false
        demoUserButton.isEnabled = false

        hideKeyboard()

        XLogin.authenticate(username, password, BuildConfig.WITH_LOGOUT, object : AuthCallback {
            override fun onSuccess() {
                val intent = Intent(requireActivity(), StoreActivity::class.java)
                startActivity(intent)
                activity?.finish()
                loginButton.isEnabled = true
                demoUserButton.isEnabled = true
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                loginButton.isEnabled = true
                demoUserButton.isEnabled = true
            }

        })
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
        XLogin.finishSocialAuth(requireActivity(), selectedSocialNetwork, requestCode, resultCode, data, BuildConfig.WITH_LOGOUT, finishSocialCallback)
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

    override fun onSocialClicked(socialNetwork: LoginBottomSheet.SocialNetworks) {
        when (socialNetwork) {
            LoginBottomSheet.SocialNetworks.TWITTER -> {
                selectedSocialNetwork = SocialNetwork.TWITTER
                XLogin.startSocialAuth(this, SocialNetwork.TWITTER, BuildConfig.WITH_LOGOUT, startSocialCallback)
            }
            LoginBottomSheet.SocialNetworks.LINKEDIN -> {
                selectedSocialNetwork = SocialNetwork.LINKEDIN
                XLogin.startSocialAuth(this, SocialNetwork.LINKEDIN, BuildConfig.WITH_LOGOUT, startSocialCallback)
            }
        }
    }

}
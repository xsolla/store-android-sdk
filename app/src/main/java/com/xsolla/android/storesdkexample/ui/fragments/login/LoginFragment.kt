package com.xsolla.android.storesdkexample.ui.fragments.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.api.XLoginCallback
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.entity.response.AuthResponse
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.ViewUtils
import com.xsolla.android.storesdkexample.util.setRateLimitedClickListener
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : BaseFragment() {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
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

            XLogin.login(username, password, object : XLoginCallback<AuthResponse?>() {
                override fun onSuccess(response: AuthResponse?) {
                    activity?.setResult(Activity.RESULT_OK)
                    activity?.finish()
                    ViewUtils.enable(v)
                }

                override fun onFailure(errorMessage: String) {
                    showSnack(errorMessage)
                    ViewUtils.enable(v)
                }
            })
        }

        rootView.googleButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.GOOGLE
            XLogin.startSocialAuth(this, SocialNetwork.GOOGLE, startSocialCallback)
        }

        rootView.facebookButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.FACEBOOK
            XLogin.startSocialAuth(this, SocialNetwork.FACEBOOK, startSocialCallback)
        }

        rootView.twitterButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.TWITTER
            XLogin.startSocialAuth(this, SocialNetwork.TWITTER, startSocialCallback)
        }

        rootView.baiduButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.BAIDU
            XLogin.startSocialAuth(this, SocialNetwork.BAIDU, startSocialCallback)
        }

        rootView.naverButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.NAVER
            XLogin.startSocialAuth(this, SocialNetwork.NAVER, startSocialCallback)
        }

        rootView.resetPasswordButton.setOnClickListener { restPassword() }
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

    private fun restPassword() {
        activity?.let {
            it.supportFragmentManager
                    .beginTransaction()
                    .add(R.id.rootFragmentContainer, ResetPasswordFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        XLogin.finishSocialAuth(context, selectedSocialNetwork, requestCode, resultCode, data, finishSocialCallback)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val startSocialCallback: StartSocialCallback = object : StartSocialCallback {
        override fun onAuthStarted() {
            // auth successfully started
        }

        override fun onError(errorMessage: String) {
            showSnack(errorMessage)
        }
    }

    private val finishSocialCallback: FinishSocialCallback = object : FinishSocialCallback {
        override fun onAuthSuccess() {
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }

        override fun onAuthCancelled() {
            showSnack("Auth cancelled")
        }

        override fun onAuthError(errorMessage: String) {
            showSnack(errorMessage)
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

}
package com.xsolla.android.storesdkexample.ui.fragments.login


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.LoginBottomSheet
import com.xsolla.android.appcore.databinding.FragmentLoginBinding
import com.xsolla.android.appcore.extensions.setRateLimitedClickListener
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.GetCurrentUserDetailsCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.entity.response.UserDetailsResponse
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.StoreActivity
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.fragments.login.login_options.MoreLoginOptionsFragment
import java.util.*

class LoginFragment : BaseFragment(), LoginBottomSheet.SocialClickListener {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
        private val POLICY_LANGUAGES = listOf("de", "ko", "zh", "ja", "ru")
    }

    private val binding: FragmentLoginBinding by viewBinding()

    private var selectedSocialNetwork: SocialNetwork? = null

    private var authId: String? = null

    override fun getLayout(): Int {
        return R.layout.fragment_login
    }

    override fun initUI() {
        initLoginButtonEnabling()

        XLogin.getCurrentUserDetails(object : GetCurrentUserDetailsCallback {
            override fun onSuccess(data: UserDetailsResponse) {
                authId = data.id
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                authId = null
            }
        })


        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            loginWithPassword(username, password)
        }

        binding.moreLoginOptionsButton.setOnClickListener {

            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.rootFragmentContainer, MoreLoginOptionsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.googleButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.GOOGLE
            XLogin.startSocialAuth(
                this,
                SocialNetwork.GOOGLE,
                BuildConfig.WITH_LOGOUT,
                startSocialCallback
            )
        }

        binding.facebookButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.FACEBOOK
            XLogin.startSocialAuth(
                this,
                SocialNetwork.FACEBOOK,
                BuildConfig.WITH_LOGOUT,
                startSocialCallback
            )
        }

        binding.baiduButton.setRateLimitedClickListener {
            selectedSocialNetwork = SocialNetwork.BAIDU
            XLogin.startSocialAuth(
                this,
                SocialNetwork.BAIDU,
                BuildConfig.WITH_LOGOUT,
                startSocialCallback
            )
        }

        binding.moreButton.setRateLimitedClickListener {
            LoginBottomSheet.newInstance().show(childFragmentManager, "moreSocials")
        }

        binding.resetPasswordButton.setOnClickListener {
            resetPassword()
        }

        binding.privacyPolicyButton.setOnClickListener { showPrivacyPolicy() }
    }

    private fun initLoginButtonEnabling() {
        binding.usernameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonEnable()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.passwordInput.addTextChangedListener(object : TextWatcher {
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
        val usernameValid = binding.usernameInput.text?.isNotEmpty() ?: false
        val passwordValid = (binding.passwordInput.text?.length ?: 0) >= MIN_PASSWORD_LENGTH
        binding.loginButton.isEnabled = usernameValid && passwordValid
    }

    private fun loginWithPassword(username: String, password: String) {
        binding.loginButton.isEnabled = false
        binding.moreLoginOptionsButton.isEnabled = false

        hideKeyboard()

        XLogin.login(username, password, object : AuthCallback {
            override fun onSuccess() {
                val intent = Intent(requireActivity(), StoreActivity::class.java)
                startActivity(intent)
                activity?.finish()
                binding.loginButton.isEnabled = true
                binding.moreLoginOptionsButton.isEnabled = true
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                binding.loginButton.isEnabled = true
                binding.moreLoginOptionsButton.isEnabled = true
            }

        }, BuildConfig.WITH_LOGOUT)
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
        XLogin.finishSocialAuth(
            requireActivity(),
            selectedSocialNetwork,
            requestCode,
            resultCode,
            data,
            BuildConfig.WITH_LOGOUT,
            finishSocialCallback
        )
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
            showSnack(throwable?.javaClass?.name ?: throwable?.localizedMessage ?: errorMessage?: "Error")
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

    private val map = mapOf(
        LoginBottomSheet.SocialNetworks.TWITTER to SocialNetwork.TWITTER,
        LoginBottomSheet.SocialNetworks.LINKEDIN to SocialNetwork.LINKEDIN,
        LoginBottomSheet.SocialNetworks.BATTLENET to SocialNetwork.BATTLENET,
        LoginBottomSheet.SocialNetworks.DISCORD to SocialNetwork.DISCORD,
        LoginBottomSheet.SocialNetworks.GITHUB to SocialNetwork.GITHUB,
        LoginBottomSheet.SocialNetworks.KAKAO to SocialNetwork.KAKAO,
        LoginBottomSheet.SocialNetworks.QQ to SocialNetwork.QQ,
        LoginBottomSheet.SocialNetworks.REDDIT to SocialNetwork.REDDIT,
        LoginBottomSheet.SocialNetworks.STEAM to SocialNetwork.STEAM,
        LoginBottomSheet.SocialNetworks.TWITCH to SocialNetwork.TWITCH,
        LoginBottomSheet.SocialNetworks.VK to SocialNetwork.VK,
        LoginBottomSheet.SocialNetworks.VIMEO to SocialNetwork.VIMEO,
        LoginBottomSheet.SocialNetworks.WECHAT to SocialNetwork.WECHAT,
        LoginBottomSheet.SocialNetworks.WEIBO to SocialNetwork.WEIBO,
        LoginBottomSheet.SocialNetworks.YAHOO to SocialNetwork.YAHOO,
        LoginBottomSheet.SocialNetworks.YANDEX to SocialNetwork.YANDEX,
        LoginBottomSheet.SocialNetworks.YOUTUBE to SocialNetwork.YOUTUBE,
        LoginBottomSheet.SocialNetworks.XBOX to SocialNetwork.XBOX,
        LoginBottomSheet.SocialNetworks.MSN to SocialNetwork.MSN,
        LoginBottomSheet.SocialNetworks.OK to SocialNetwork.OK,
        LoginBottomSheet.SocialNetworks.PAYPAL to SocialNetwork.PAYPAL,
        LoginBottomSheet.SocialNetworks.NAVER to SocialNetwork.NAVER,
        LoginBottomSheet.SocialNetworks.APPLE to SocialNetwork.APPLE,
        LoginBottomSheet.SocialNetworks.AMAZON to SocialNetwork.AMAZON,
        LoginBottomSheet.SocialNetworks.MAILRU to SocialNetwork.MAILRU,
        LoginBottomSheet.SocialNetworks.MICROSOFT to SocialNetwork.MICROSOFT,
    )

    override fun onSocialClicked(socialNetwork: LoginBottomSheet.SocialNetworks) {
        val loginSocialNetwork = map[socialNetwork]
        selectedSocialNetwork = loginSocialNetwork
        XLogin.startSocialAuth(
            this,
            loginSocialNetwork,
            BuildConfig.WITH_LOGOUT,
            startSocialCallback
        )
    }

}
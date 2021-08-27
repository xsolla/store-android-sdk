package com.xsolla.android.storesdkexample.ui.fragments.login.login_options

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.R
import com.xsolla.android.appcore.databinding.FragmentLogInWithPhoneOrEmailEnterCodeBinding
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.CompletePasswordlessAuthCallback
import com.xsolla.android.login.callback.GetOtcCodeCallback
import com.xsolla.android.login.callback.StartPasswordlessAuthCallback
import com.xsolla.android.login.entity.response.OtcResponse
import com.xsolla.android.login.entity.response.StartPasswordlessAuthResponse
import com.xsolla.android.storesdkexample.StoreActivity
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment

class LoginEnterCodeFragment : BaseFragment() {

    enum class Type {
        EMAIL, PHONE
    }

    companion object {
        fun getInstance(
            type: Type,
            phoneOrEmail: String,
            operationId: String
        ): LoginEnterCodeFragment {
            val fragment = LoginEnterCodeFragment()
            fragment.arguments = bundleOf(
                "type" to type,
                "phoneOrEmail" to phoneOrEmail,
                "operationId" to operationId
            )
            return fragment
        }
    }

    private val binding: FragmentLogInWithPhoneOrEmailEnterCodeBinding by viewBinding()
    private lateinit var countDownTimer: CountDownTimer
    private var handler: Handler = Handler()
    private var runnable: Runnable? = null
    private var delay = 20000

    private lateinit var operationId: String

    override fun getLayout(): Int {
        return R.layout.fragment_log_in_with_phone_or_email_enter_code
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operationId = requireArguments().getString("operationId")!!
    }

    override fun onResume() {

        val phoneOrEmail = requireArguments().getString("phoneOrEmail")!!

        handler.postDelayed(Runnable {
            handler.postDelayed(runnable, delay.toLong())

            XLogin.getOtcCode(phoneOrEmail, operationId, object : GetOtcCodeCallback {
                override fun onSuccess(data: OtcResponse) {
                    XLogin.completeAuthByEmail(
                        phoneOrEmail,
                        data.code,
                        operationId,
                        object : CompletePasswordlessAuthCallback {
                            override fun onSuccess() {
                                countDownTimer.cancel()
                                val intent = Intent(requireActivity(), StoreActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }

                            override fun onError(throwable: Throwable?, errorMessage: String?) {
                                showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                            }
                        })
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                }
            })

        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun initUI() {

        when (requireArguments().getSerializable("type") as Type) {
            Type.EMAIL -> binding.textInputLayout.hint =
                getString(com.xsolla.android.storesdkexample.R.string.login_code_from_email)
            Type.PHONE -> binding.textInputLayout.hint =
                getString(com.xsolla.android.storesdkexample.R.string.login_code_from_sms)
        }

        initTimer()

        binding.btBack.setOnClickListener {
            hideKeyboard()
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        val phoneOrEmail = requireArguments().getString("phoneOrEmail")!!

        binding.tvResendCode.setOnClickListener {
            if (binding.tvExpiredIn.visibility == View.GONE) {
                XLogin.startAuthByEmail(phoneOrEmail, object : StartPasswordlessAuthCallback {
                    override fun onAuthStarted(data: StartPasswordlessAuthResponse) {
                        operationId = data.operationId
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                    }
                })
            } else {
                showSnack("Wait until timer expire")
            }
        }

        binding.codeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.btLogIn.isEnabled = binding.codeInput.text!!.length == 4
            }
        })

        binding.btLogIn.setOnClickListener {
            val code = binding.codeInput.text.toString()
            XLogin.completeAuthByEmail(
                phoneOrEmail,
                code,
                operationId,
                object : CompletePasswordlessAuthCallback {
                    override fun onSuccess() {
                        countDownTimer.cancel()
                        val intent = Intent(requireActivity(), StoreActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                    }
                })
        }
    }

    private fun initTimer() {
        val difference: Long = 300000  // 5minutes

        binding.tvCodeExpired.visibility = View.INVISIBLE
        binding.tvTimer.visibility = View.VISIBLE
        binding.tvExpiredIn.visibility = View.VISIBLE

        countDownTimer = object : CountDownTimer(difference, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedMinutes = (millisUntilFinished / 1000) / 60
                val elapsedSeconds = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text =
                    context!!.getString(R.string.login_code_timer, elapsedMinutes, elapsedSeconds)
            }

            override fun onFinish() {
                binding.tvExpiredIn.visibility = View.INVISIBLE
                binding.tvTimer.visibility = View.INVISIBLE
                binding.tvCodeExpired.visibility = View.VISIBLE
            }
        }.start()
    }
}
package com.xsolla.android.storesdkexample.ui.fragments.login.login_options

import android.content.Intent
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.R
import com.xsolla.android.appcore.databinding.FragmentLogInWithPhoneEnterCodeBinding
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.CompleteAuthByPhoneCallback
import com.xsolla.android.login.callback.StartAuthByPhoneCallback
import com.xsolla.android.login.entity.response.StartAuthByPhoneResponse
import com.xsolla.android.storesdkexample.StoreActivity
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.*

class LoginEnterCodeFragment(
    private var operationId: String,
    private val phoneNumber: String
) : BaseFragment() {

    private val binding: FragmentLogInWithPhoneEnterCodeBinding by viewBinding()
    private lateinit var countDownTimer: CountDownTimer

    override fun getLayout(): Int {
        return R.layout.fragment_log_in_with_phone_enter_code
    }

    override fun initUI() {

        initTimer()

        binding.btBack.setOnClickListener {
            hideKeyboard()
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.tvResendCode.setOnClickListener {
            if (binding.tvExpiredIn.visibility == View.GONE){
            XLogin.startAuthByMobilePhone(phoneNumber,object : StartAuthByPhoneCallback{
                override fun onAuthStarted(data: StartAuthByPhoneResponse) {
                    operationId = data.operationId
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    showSnack(throwable?.javaClass?.name ?: errorMessage ?: "Error")
                }
            })}
            else{
                showSnack("Wait until timer expire")
            }
        }

        binding.codeInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.codeInput.text!!.length ==4){
                    binding.btLogIn.isEnabled = true
                }
                else{ //codecheck shows it as redundant but itsnt
                    binding.btLogIn.isEnabled=false
                }
            }
        })

        binding.btLogIn.setOnClickListener {
            val code = binding.codeInput.text.toString()
            XLogin.completeAuthByMobilePhone(phoneNumber,code,operationId,object : CompleteAuthByPhoneCallback{
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
        val difference:Long = 300000  //current time + 5minutes

        binding.tvCodeExpired.visibility = View.INVISIBLE
        binding.tvTimer.visibility = View.VISIBLE
        binding.tvExpiredIn.visibility = View.VISIBLE

        countDownTimer = object : CountDownTimer(difference,1000){
            override fun onTick(millisUntilFinished: Long) {
                val elapsedMinutes = (millisUntilFinished / 1000) / 60
                val elapsedSeconds = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text = context!!.getString(R.string.login_code_timer, elapsedMinutes,elapsedSeconds)
            }

            override fun onFinish() {
                binding.tvExpiredIn.visibility = View.INVISIBLE
                binding.tvTimer.visibility = View.INVISIBLE
                binding.tvCodeExpired.visibility = View.VISIBLE
            }
        }.start()
    }
}
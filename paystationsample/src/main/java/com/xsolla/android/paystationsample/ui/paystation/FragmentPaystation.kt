package com.xsolla.android.paystationsample.ui.paystation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xsolla.android.paystation.XPaystation
import com.xsolla.android.paystationsample.R
import kotlinx.android.synthetic.main.fragment_paystation.*

class FragmentPaystation : Fragment() {

    companion object {
        private const val RC_PAYSTATION = 1

        fun newInstance() = FragmentPaystation()
    }

    private lateinit var viewModel: PaystationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_paystation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_webview_token.setOnClickListener {
            openWebviewWithToken()
        }
        button_webview_data.setOnClickListener {
            openWebviewWithData()
        }
        button_browser_token.setOnClickListener {
            openBrowserWithToken()
        }
        button_browser_data.setOnClickListener {
            openBrowserWithData()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaystationViewModel::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_PAYSTATION) {
            val result = XPaystation.Result.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "OK\n$result", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Fail\n$result", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openWebviewWithToken() {
        context?.let {
            val intent = XPaystation.createIntentBuilder(it)
                    .accessToken(viewModel.getAccessToken())
                    .isSandbox(viewModel.isSandbox())
                    .useWebview(true)
                    .build()
            startActivityForResult(intent, RC_PAYSTATION)
        }
    }

    private fun openWebviewWithData() {
        context?.let {
            val intent = XPaystation.createIntentBuilder(it)
                    .accessData(viewModel.getAccessData())
                    .isSandbox(viewModel.isSandbox())
                    .useWebview(true)
                    .build()
            startActivityForResult(intent, RC_PAYSTATION)
        }
    }

    private fun openBrowserWithToken() {
        context?.let {
            val intent = XPaystation.createIntentBuilder(it)
                    .accessToken(viewModel.getAccessToken())
                    .isSandbox(viewModel.isSandbox())
                    .useWebview(false)
                    .build()
            startActivityForResult(intent, RC_PAYSTATION)
        }
    }

    private fun openBrowserWithData() {
        context?.let {
            val intent = XPaystation.createIntentBuilder(it)
                    .accessData(viewModel.getAccessData())
                    .isSandbox(viewModel.isSandbox())
                    .useWebview(false)
                    .build()
            startActivityForResult(intent, RC_PAYSTATION)
        }
    }
}

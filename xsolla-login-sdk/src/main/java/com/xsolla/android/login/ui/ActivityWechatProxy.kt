package com.xsolla.android.login.ui

import android.app.Activity
import android.os.Bundle
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.util.*

internal class ActivityWechatProxy : Activity() {

    companion object {
        const val EXTRA_WECHAT_ID = "EXTRA_WECHAT_ID"
    }

    private lateinit var wechatId: String
    private var started = false

    private lateinit var iwxapi: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wechatId = intent.getStringExtra(EXTRA_WECHAT_ID)!!
        iwxapi = WXAPIFactory.createWXAPI(this, wechatId, false)

        if (savedInstanceState != null) {
            started = savedInstanceState.getBoolean("started")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("started", started)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        if (started) {
            finish()
        } else {
            val req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = UUID.randomUUID().toString()
            iwxapi.sendReq(req)
            started = true
        }
    }

}
package com.xsolla.android.login.util

import android.os.Handler
import android.os.Looper
import com.xsolla.android.login.callback.BaseCallback
import com.xsolla.lib_login.util.LoginApiException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private val ioExecutor = Executors.newCachedThreadPool()
private val callbackExecutor = if ("Dalvik" == System.getProperty("java.vm.name")) {
    object : Executor {
        private val mainHandler = Handler(Looper.getMainLooper())
        override fun execute(r: Runnable) {
            mainHandler.post(r)
        }
    }
} else {
    ioExecutor
}

fun runIo(runnable: Runnable) =
    ioExecutor.execute(runnable)

fun runCallback(runnable: Runnable) =
    callbackExecutor.execute(runnable)

fun handleException(e: Exception, callback: BaseCallback) {
    if (e is LoginApiException) {
        runCallback {
            callback.onError(e.cause, e.error.description)
        }
    } else {
        runCallback {
            callback.onError(e, null)
        }
    }
}

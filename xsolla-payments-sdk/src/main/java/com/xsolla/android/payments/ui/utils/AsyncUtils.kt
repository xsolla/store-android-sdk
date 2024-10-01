package com.xsolla.android.payments.ui.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

object AsyncUtils {
    private val LOG_TAG = AsyncUtils.javaClass.simpleName

    private val sThreadFactory = InternalThreadFactory()
    private val sThreadPool = Executors.newCachedThreadPool(sThreadFactory)

    private val sMainThreadExecutor = object : Executor {
        private val mainHandler = Handler(Looper.getMainLooper())

        override fun execute(r: Runnable) {
            mainHandler.post(r)
        }
    }

    fun run(runnable: Runnable) : Future<*>? {
        try {
            return sThreadPool.submit(runnable)
        } catch (e: Exception) {
            Log.d(LOG_TAG, "Failed to execute a task asynchronously.", e)
            return null
        }
    }

    fun runOnMainThread(runnable: Runnable) =
        sMainThreadExecutor.execute(runnable)

    class InternalThreadFactory : ThreadFactory {
        private val threadFactory = Executors.defaultThreadFactory()
        private val nextThreadId = AtomicInteger(1)

        override fun newThread(r: Runnable?): Thread {
            val thread = threadFactory.newThread(r)
            val threadId = nextThreadId.getAndIncrement()
            thread.name = "${THREAD_PREFIX}-${threadId}"
            return thread;
        }

        companion object {
            private const val THREAD_PREFIX = "XsollaPayments"
        }
    }
}

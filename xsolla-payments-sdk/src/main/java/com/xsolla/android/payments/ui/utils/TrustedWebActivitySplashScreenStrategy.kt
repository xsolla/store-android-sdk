package com.xsolla.android.payments.ui.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.browser.customtabs.CustomTabsSession
import androidx.browser.customtabs.TrustedWebUtils
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.google.androidbrowserhelper.trusted.Utils
import com.google.androidbrowserhelper.trusted.splashscreens.SplashScreenStrategy
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.ceil

/**
 * A behavior for [TrustedWebActivity]'s splash screen functionality that controls
 * how and when it's presented to the end-user.
 */
internal class TrustedWebActivitySplashScreenStrategy(
    private val mActivity: Activity,
    private val mImage: Image?,
    @ColorInt private val mBackgroundColor: Int,
    private val mScaleType: ScaleType,
    private val mTransformationMatrix: Matrix?,
    private val mFadeOutDurationMillis: Int,
    private val mFileProviderAuthority: String
) : SplashScreenStrategy {
    class Image private constructor(val bitmap: Bitmap) {
        companion object {
            fun forRef(
                context: Context, ref: TrustedWebActivityImageRef
            ): Image? = ref.fold(
                { drawableId -> forDrawableId(context, drawableId) },
                { filepath -> forFilepath(context, filepath) },
                { forEmpty() }
            )

            /**
             * Attempts to create an [Image] from a [Drawable].
             *
             * @param drawable Drawable to use.
             *
             * @return A non-null [Image] if there were no errors.
             */
            private fun forDrawable(drawable: Drawable): Image? {
                try {
                    val wrappedDrawable = DrawableCompat.wrap(drawable)

                    val MAX_WIDTH = 1024
                    val MAX_HEIGHT = 1024

                    var width = wrappedDrawable.intrinsicWidth
                    var height = wrappedDrawable.intrinsicHeight
                    val aspect = height.takeIf { it > 0 }?.let { width / it.toFloat() } ?: 1.0f

                    if (width > MAX_WIDTH) {
                        width = MAX_WIDTH
                        height = (width / aspect).toInt()
                        Log.d(TAG, "Splash screen drawable is too wide, limiting to $MAX_WIDTH")
                    } else if (height > MAX_HEIGHT) {
                        height = MAX_HEIGHT
                        width = (height * aspect).toInt()
                        Log.d(TAG, "Splash screen drawable is too tall, limiting to $MAX_HEIGHT")
                    }

                    val bitmap = wrappedDrawable.toBitmap(width, height)

                    return Image(bitmap)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create an image for a drawable", e)
                    return null
                }
            }

            /**
             * Attempts to create an [Image] from a [Drawable] resource ID.
             *
             * @param context Context for acquiring a [Drawable] by its ID.
             * @param id Drawable's resource ID.
             *
             * @return A non-null [Image] if the drawable was successfully located.
             */
            private fun forDrawableId(
                context: Context, @DrawableRes id: Int
            ): Image? {
                val drawable = ContextCompat.getDrawable(context, id)
                return drawable?.let(::forDrawable)
            }

            /**
             * Attempts to create an [Image] for a filepath that points
             * at a drawable (xml, png, webp, etc).
             *
             * Firstly, tries to open an asset at the filepath using a normal input file stream
             * and if it fails, tries to open it via the [android.content.res.AssetManager.open].
             *
             * @param context Context used for obtaining a [android.content.res.AssetManager].
             * @param filepath Either an absolute file path or an asset path.
             *
             * @return A non-null [Image] if the asset was successfully located.
             */
            private fun forFilepath(
                context: Context, filepath: String
            ): Image? {
                var stream: InputStream?

                try {
                    stream = FileInputStream(filepath)
                } catch (e: IOException) {
                    try {
                        stream = context.assets.open(filepath)
                    } catch (e1: IOException) {
                        Log.e(TAG, "Failed to locate splash screen image at: $filepath")
                        stream = null
                    }
                }

                return stream
                    ?.let { Drawable.createFromStream(it, null) }
                    ?.let(::forDrawable)
            }

            private fun forEmpty() = null
        }
    }

    private var mSplashImage: Bitmap? = null

    private var mSplashImageTransferTask: TrustedWebActivitySplashImageTransferTask? = null

    private var mProviderPackage: String? = null

    private var mProviderSupportsSplashScreens = false

    private var mEnterAnimationComplete: Boolean

    private var mOnEnterAnimationCompleteRunnable: Runnable? = null

    init {
        @SuppressLint("ObsoleteSdkInt")
        mEnterAnimationComplete = VERSION.SDK_INT < 21
    }

    override fun onTwaLaunchInitiated(
        providerPackage: String, builder: TrustedWebActivityIntentBuilder
    ) {
        mProviderPackage = providerPackage

        mProviderSupportsSplashScreens = TrustedWebUtils.areSplashScreensSupported(
            mActivity, providerPackage,
            "androidx.browser.trusted.category.TrustedWebActivitySplashScreensV1"
        )

        if (!mProviderSupportsSplashScreens) {
            Log.w(TAG, "Provider $providerPackage doesn't support splash screens")
        } else {
            showSplashScreen()
            if (mSplashImage != null) {
                customizeStatusAndNavBarDuringSplashScreen(providerPackage, builder)
            }
        }
    }

    private fun showSplashScreen() {
        mSplashImage = mImage?.bitmap
        if (mSplashImage == null) {
            Log.w(TAG, "Failed to retrieve splash image")
        } else {
            val view = ImageView(mActivity)
            view.layoutParams = ViewGroup.LayoutParams(-1, -1)
            view.setImageBitmap(mSplashImage)
            view.setBackgroundColor(mBackgroundColor)
            view.scaleType = mScaleType
            if (mScaleType == ScaleType.MATRIX) {
                view.imageMatrix = mTransformationMatrix
            }

            mActivity.setContentView(view)
        }
    }

    private fun customizeStatusAndNavBarDuringSplashScreen(
        providerPackage: String,
        builder: TrustedWebActivityIntentBuilder
    ) {
        val navbarColor = sSystemBarColorPredictor.getExpectedNavbarColor(
            mActivity, providerPackage, builder
        )
        if (navbarColor != null) {
            Utils.setNavigationBarColor(mActivity, navbarColor)
        }

        val statusBarColor = sSystemBarColorPredictor.getExpectedStatusBarColor(
            mActivity, providerPackage, builder
        )
        if (statusBarColor != null) {
            Utils.setStatusBarColor(mActivity, statusBarColor)
        }
    }

    override fun configureTwaBuilder(
        builder: TrustedWebActivityIntentBuilder,
        session: CustomTabsSession,
        onReadyCallback: Runnable
    ) {
        if (mProviderSupportsSplashScreens && mSplashImage != null && mProviderPackage != null) {
            if (TextUtils.isEmpty(mFileProviderAuthority)) {
                Log.w(TAG, "FileProvider authority not specified, can't transfer splash image.")
                onReadyCallback.run()
            } else {
                mSplashImageTransferTask = TrustedWebActivitySplashImageTransferTask(
                    mActivity, mSplashImage!!, mFileProviderAuthority, session, mProviderPackage!!
                )
                mSplashImageTransferTask!!.execute { success: Boolean ->
                    onSplashImageTransferred(builder, success, onReadyCallback)
                }
            }
        } else {
            onReadyCallback.run()
        }
    }

    private fun onSplashImageTransferred(
        builder: TrustedWebActivityIntentBuilder,
        success: Boolean, onReadyCallback: Runnable
    ) {
        if (!success) {
            Log.w(TAG, "Failed to transfer splash image.")
            onReadyCallback.run()
        } else {
            builder.setSplashScreenParams(makeSplashScreenParamsBundle())
            runWhenEnterAnimationComplete {
                onReadyCallback.run()
                mActivity.overridePendingTransition(0, 0)
            }
        }
    }

    private fun runWhenEnterAnimationComplete(runnable: Runnable) {
        if (mEnterAnimationComplete) {
            runnable.run()
        } else {
            mOnEnterAnimationCompleteRunnable = runnable
        }
    }

    private fun makeSplashScreenParamsBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(
            "androidx.browser.trusted.KEY_SPLASH_SCREEN_VERSION",
            "androidx.browser.trusted.category.TrustedWebActivitySplashScreensV1"
        )
        bundle.putInt(
            "androidx.browser.trusted.KEY_SPLASH_SCREEN_FADE_OUT_DURATION",
            mFadeOutDurationMillis
        )
        bundle.putInt(
            "androidx.browser.trusted.trusted.KEY_SPLASH_SCREEN_BACKGROUND_COLOR",
            mBackgroundColor
        )
        bundle.putInt("androidx.browser.trusted.KEY_SPLASH_SCREEN_SCALE_TYPE", mScaleType.ordinal)
        if (mTransformationMatrix != null) {
            val values = FloatArray(9)
            mTransformationMatrix.getValues(values)
            bundle.putFloatArray(
                "androidx.browser.trusted.KEY_SPLASH_SCREEN_TRANSFORMATION_MATRIX",
                values
            )
        }
        return bundle
    }

    fun onActivityEnterAnimationComplete() {
        mEnterAnimationComplete = true
        if (mOnEnterAnimationCompleteRunnable != null) {
            mOnEnterAnimationCompleteRunnable!!.run()
            mOnEnterAnimationCompleteRunnable = null
        }
    }

    fun destroy() {
        if (mSplashImageTransferTask != null) {
            mSplashImageTransferTask!!.cancel()
        }
    }

    companion object {
        private const val TAG = "SplashScreenStrategy"

        private val sSystemBarColorPredictor = TrustedWebActivitySystemBarColorPredictor()
    }
}

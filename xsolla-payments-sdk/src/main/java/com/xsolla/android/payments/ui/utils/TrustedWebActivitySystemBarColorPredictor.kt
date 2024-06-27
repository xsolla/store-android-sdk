package com.xsolla.android.payments.ui.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import com.google.androidbrowserhelper.trusted.ChromeLegacyUtils

internal class TrustedWebActivitySystemBarColorPredictor {
    private val mSupportedFeaturesCache: MutableMap<String, SupportedFeatures> = HashMap()

    fun getExpectedStatusBarColor(
        context: Context, providerPackage: String,
        builder: TrustedWebActivityIntentBuilder
    ): Int? {
        val intent = builder.buildCustomTabsIntent().intent
        if (providerSupportsColorSchemeParams(context, providerPackage)) {
            val colorScheme = getExpectedColorScheme(context, builder)
            val params =
                CustomTabsIntent.getColorSchemeParams(intent, colorScheme)
            return params.toolbarColor
        } else {
            val extras = intent.extras
            return if (extras != null
            ) extras["android.support.customtabs.extra.TOOLBAR_COLOR"] as Int?
            else null
        }
    }

    fun getExpectedNavbarColor(
        context: Context, providerPackage: String,
        builder: TrustedWebActivityIntentBuilder
    ): Int? {
        val intent = builder.buildCustomTabsIntent().intent
        if (providerSupportsNavBarColorCustomization(context, providerPackage)) {
            if (providerSupportsColorSchemeParams(context, providerPackage)) {
                val colorScheme = getExpectedColorScheme(context, builder)
                val params = CustomTabsIntent.getColorSchemeParams(intent, colorScheme)
                return params.navigationBarColor
            } else {
                val extras = intent.extras
                return extras?.let {
                    it["androidx.browser.customtabs.extra.NAVIGATION_BAR_COLOR"] as Int?
                }
            }
        } else {
            return if (ChromeLegacyUtils.usesWhiteNavbar(providerPackage)) -1 else null
        }
    }

    private fun providerSupportsNavBarColorCustomization(
        context: Context, providerPackage: String
    ): Boolean {
        return getSupportedFeatures(context, providerPackage).navbarColorCustomization
    }

    private fun providerSupportsColorSchemeParams(
        context: Context, providerPackage: String
    ): Boolean {
        return getSupportedFeatures(context, providerPackage).colorSchemeCustomization
    }

    private fun getSupportedFeatures(
        context: Context, providerPackage: String
    ): SupportedFeatures {
        val cached = mSupportedFeaturesCache[providerPackage]
        if (cached != null) {
            return cached
        } else if (ChromeLegacyUtils.supportsNavbarAndColorCustomization(
            context.packageManager, providerPackage
        )) {
            val features = SupportedFeatures(true, true)
            mSupportedFeaturesCache[providerPackage] = features
            return features
        } else {
            val serviceIntent = Intent()
                .setAction("android.support.customtabs.action.CustomTabsService")
                .setPackage(providerPackage)
            val resolveInfo = context.packageManager.resolveService(serviceIntent, 64)
            val features = SupportedFeatures(
                hasCategory(
                    resolveInfo,
                    "androidx.browser.customtabs.category.NavBarColorCustomization"
                ),
                hasCategory(
                    resolveInfo,
                    "androidx.browser.customtabs.category.ColorSchemeCustomization"
                )
            )
            mSupportedFeaturesCache[providerPackage] = features
            return features
        }
    }

    private class SupportedFeatures(
        val navbarColorCustomization: Boolean,
        val colorSchemeCustomization: Boolean
    )

    companion object {
        private fun hasCategory(info: ResolveInfo?, category: String): Boolean {
            return info?.filter != null && info.filter.hasCategory(category)
        }

        private fun getExpectedColorScheme(
            context: Context, builder: TrustedWebActivityIntentBuilder
        ): Int {
            val intent = builder.buildCustomTabsIntent().intent
            val extras = intent.extras
            val scheme = extras?.let {
                it["androidx.browser.customtabs.extra.COLOR_SCHEME"] as Int?
            }
            if (scheme != null && scheme != 0) {
                return scheme
            } else {
                val systemIsInDarkMode = (context.resources.configuration.uiMode and 48) == 32
                return if (systemIsInDarkMode) 2 else 1
            }
        }
    }
}

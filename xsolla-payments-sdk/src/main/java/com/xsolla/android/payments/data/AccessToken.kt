package com.xsolla.android.payments.data

import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt

data class AccessToken(
    val token: String
) {
    private companion object {
        const val LOG_TAG = "AccessToken"

        const val BACKGROUND_COLOR_TOKEN = "bg"
        const val BUTTON_COLOR_TOKEN = "tb"

        const val TOKEN_VALUE_DELIMITER = '_'
    }

    /**
     * Extracts the background color from the token if possible.
     *
     * The result is not cached, thus might be expensive in certain scenarios.
     */
    @ColorInt
    fun getBackgroundColor() : Int? =
        extractColor(BACKGROUND_COLOR_TOKEN)

    /**
     * Extracts the button color from the token if possible.
     *
     * The result is not cached, thus might be expensive in certain scenarios.
     */
    @ColorInt
    fun getButtonColor() : Int? =
        extractColor(BUTTON_COLOR_TOKEN)

    @ColorInt
    private fun extractColor(token: String) : Int? {
        val colorStr = this.token
            .substringAfter("$token$TOKEN_VALUE_DELIMITER", "")
            .substringBefore(TOKEN_VALUE_DELIMITER, "")
        try {
            return Color.parseColor("#$colorStr")
        } catch (e: Exception) {
            Log.e(LOG_TAG,"[AccessToken] Failed to parse color", e)
            return null
        }
    }
}
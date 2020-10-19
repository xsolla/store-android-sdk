package com.xsolla.android.storesdkexample.util.extensions

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.xsolla.android.storesdkexample.R

fun TextView.setClickableSpan(
    textColor: Int = R.color.light_state_gray_color,
    highlightColor: Int = Color.TRANSPARENT,
    isUnderlineText: Boolean = false,
    startIndex: Int,
    endIndex: Int,
    onClick: () -> Unit
) {
    val spannableString = SpannableString(this.text)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            onClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = ResourcesCompat.getColor(resources, textColor, null)
            ds.isUnderlineText = isUnderlineText
        }
    }

    spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.text = spannableString
    this.movementMethod = LinkMovementMethod.getInstance()
    this.highlightColor = highlightColor
}
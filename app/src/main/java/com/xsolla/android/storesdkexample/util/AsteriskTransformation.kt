package com.xsolla.android.storesdkexample.util

import android.text.method.PasswordTransformationMethod
import android.view.View

class AsteriskTransformation : PasswordTransformationMethod() {

    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return AsteriskCharSequence(source)
    }

    private class AsteriskCharSequence(private val source: CharSequence) : CharSequence {
        override val length: Int
            get() = source.length

        override fun get(index: Int): Char = '*'

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
                source.subSequence(startIndex, endIndex)
    }

}
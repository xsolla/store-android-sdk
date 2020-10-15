package com.xsolla.android.storesdkexample.data.local

import android.content.Context
import androidx.annotation.StringRes

interface IResourceProvider {
    fun getString(@StringRes resource: Int): String
    fun getString(@StringRes resource: Int, vararg formatArgs: Any): String
}

class ResourceProvider(private val context: Context) : IResourceProvider {
    override fun getString(resource: Int) = context.getString(resource)
    override fun getString(resource: Int, vararg formatArgs: Any) = context.getString(resource, formatArgs)
}
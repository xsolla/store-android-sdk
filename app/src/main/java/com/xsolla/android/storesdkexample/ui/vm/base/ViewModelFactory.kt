package com.xsolla.android.storesdkexample.ui.vm.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xsolla.android.storesdkexample.data.local.IResourceProvider
import com.xsolla.android.storesdkexample.ui.vm.VmProfile

class ViewModelFactory(private val params: Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VmProfile::class.java)) {
            return VmProfile(params as IResourceProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
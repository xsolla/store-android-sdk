package com.xsolla.android.inventorysdkexample.ui.vm.base

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xsolla.android.inventorysdkexample.ui.vm.VmProfile

class ViewModelFactory(private val params: Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VmProfile::class.java)) {
            return VmProfile(params as Resources) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
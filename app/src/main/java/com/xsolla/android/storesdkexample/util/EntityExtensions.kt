package com.xsolla.android.storesdkexample.util

import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.storesdkexample.ui.vm.UserAttributeItem

fun UserAttribute.toItem() = UserAttributeItem(this.key, this.value)
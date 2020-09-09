package com.xsolla.android.storesdkexample.util

import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.storesdkexample.ui.vm.UserAttributeUiEntity

fun UserAttribute.toUiEntity() = UserAttributeUiEntity(this.key, this.value)

fun List<UserAttribute>.toUiEntity(): List<UserAttributeUiEntity> {
    val list = mutableListOf<UserAttributeUiEntity>()
    this.forEach { list.add(it.toUiEntity()) }
    return list
}
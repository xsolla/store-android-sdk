package com.xsolla.android.storesdkexample.util.extensions

enum class BirthdayFormat {
    FROM_BACKEND_TO_UI,
    FROM_UI_TO_BACKEND
}

fun String.formatBirthday(format: BirthdayFormat): String {
    try {
        if (this.isBlank()) return this

        return when (format) {
            BirthdayFormat.FROM_BACKEND_TO_UI -> {
                val split = this.split("-")
                "${split.last()}/${split[1]}/${split.first()}"
            }
            BirthdayFormat.FROM_UI_TO_BACKEND -> {
                val split = this.split("/")
                "${split.last()}-${split[1]}-${split.first()}"
            }
        }
    } catch (e: Exception) {
        return this
    }
}
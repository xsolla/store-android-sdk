package com.xsolla.android.appcore.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.use
import com.xsolla.android.appcore.R

class DrawerInnerItem(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_inner_drawer, this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.DrawerInnerItem, 0, 0).use {
            findViewById<TextView>(R.id.item_text).text = it.getString(R.styleable.DrawerInnerItem_text)
        }
    }

}
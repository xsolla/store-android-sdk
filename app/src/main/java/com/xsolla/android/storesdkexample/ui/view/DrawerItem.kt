package com.xsolla.android.storesdkexample.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.use
import com.xsolla.android.appcore.R

class DrawerItem(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_drawer, this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.DrawerItem, 0, 0).use {
            findViewById<ImageView>(R.id.item_icon).setImageResource(it.getResourceId(R.styleable.DrawerItem_item_icon, 0))
            findViewById<TextView>(R.id.item_text).text = it.getString(R.styleable.DrawerItem_item_text)
        }
    }

}
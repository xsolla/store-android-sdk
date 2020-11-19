package com.xsolla.android.inventorysdkexample.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.res.use
import com.xsolla.android.inventorysdkexample.R
import kotlinx.android.synthetic.main.item_drawer.view.*

class DrawerItem(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_drawer, this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.DrawerItem, 0, 0).use {
            item_icon.setImageResource(it.getResourceId(R.styleable.DrawerItem_item_icon, 0))
            item_text.text = it.getString(R.styleable.DrawerItem_item_text)
        }
    }

}
package com.xsolla.android.storesdkexample.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.xsolla.android.storesdkexample.R
import kotlinx.android.synthetic.main.item_drawer.view.*

class DrawerItemView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.item_drawer, this, true)

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.DrawerItemView,
                0, 0
        ).apply {
            try {
                item_icon.setImageResource(getResourceId(R.styleable.DrawerItemView_item_icon, 0))
                item_text.text = getString(R.styleable.DrawerItemView_item_text)
            } finally {
                recycle()
            }
        }
    }

}
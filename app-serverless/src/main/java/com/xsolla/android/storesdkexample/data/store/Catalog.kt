package com.xsolla.android.storesdkexample.data.store

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

object Catalog {

    lateinit var context: Context

    val catalog by lazy {
        val moshi = Moshi.Builder()
                .add(BigDecimalAdapter)
                .build()
        val jsonString = context.assets.open("catalog.json").bufferedReader().use { it.readText() }
        val type: Type = Types.newParameterizedType(MutableList::class.java, CatalogItem::class.java)
        val adapter: JsonAdapter<List<CatalogItem>> = moshi.adapter<List<CatalogItem>>(type)
        val catalogItems: List<CatalogItem>? = adapter.fromJson(jsonString)
        catalogItems!!
    }
}
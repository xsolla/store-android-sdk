package com.xsolla.android.nativepaymentssdk.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.nativepaymentssdk.R
import com.xsolla.android.nativepaymentssdk.vm.VmPayment
import java.net.URL


class SavedCardsAdapter(
    private val dataSet: List<VmPayment.CardInfo>,
    private val cardClickListener: CardClickListener) :
    RecyclerView.Adapter<SavedCardsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view
        val img: ImageView = view.findViewById(R.id.xsolla_native_payments_recycler_item_img)
        val title1: TextView = view.findViewById(R.id.xsolla_native_payments_recycler_item_text1)
        val title2: TextView = view.findViewById(R.id.xsolla_native_payments_recycler_item_text2)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.xsolla_native_payments_card_preview, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(viewHolder.root).load(dataSet[position].iconSrc).into(viewHolder.img)
        viewHolder.title1.text = dataSet[position].name
        viewHolder.title2.text = dataSet[position].psName
        viewHolder.root.setOnClickListener {
            cardClickListener.onCardClick(dataSet[position])
        }
    }

    override fun getItemCount() = dataSet.size

    interface CardClickListener {
        fun onCardClick(cardInfo: VmPayment.CardInfo)
    }

}
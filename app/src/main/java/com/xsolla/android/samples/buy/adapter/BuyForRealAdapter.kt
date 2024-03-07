package com.xsolla.android.samples.buy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.data.AccessToken
import com.xsolla.android.payments.ui.utils.BrowserUtils
import com.xsolla.android.samples.buy.BuyForRealActivity
import com.xsolla.android.samples.buy.adapter.holder.BuyViewHolder
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.CreateOrderCallback
import com.xsolla.android.store.entity.request.payment.MobileSettings
import com.xsolla.android.store.entity.request.payment.PaymentOptions
import com.xsolla.android.store.entity.request.payment.PaymentProjectSettings
import com.xsolla.android.store.entity.request.payment.SettingsRedirectPolicy
import com.xsolla.android.store.entity.request.payment.UiMobileProjectSettingHeader
import com.xsolla.android.store.entity.request.payment.UiProjectSetting
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.R


class BuyForRealAdapter(private val parentActivity: BuyForRealActivity, private val items: List<VirtualItemsResponse.Item>) :
    RecyclerView.Adapter<BuyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyViewHolder {
        return BuyViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.buy_item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: BuyViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.view).load(item.imageUrl).into(holder.itemImage)
        holder.itemName.text = item.name
        holder.itemDescription.text = item.description
        var priceText: String
        if(item.virtualPrices.isNotEmpty()) {
            priceText = item.virtualPrices[0].getAmountRaw() + " " + item.virtualPrices[0].name
        } else {
            priceText = item.price?.getAmountRaw() + " " + item.price?.currency.toString()
        }

        holder.itemPrice.text = priceText

        val isDisplayCloseButton = !BrowserUtils.isCustomTabsBrowserAvailable(parentActivity)

        holder.itemButton.setOnClickListener {

            val paymentOptions = PaymentOptions(
                isSandbox = true,
                settings = PaymentProjectSettings(
                    ui = UiProjectSetting(
                        mobile = MobileSettings(header = UiMobileProjectSettingHeader(closeButton = isDisplayCloseButton))
                    ),
                    returnUrl = "app://xpayment.com.xsolla.android.storesdkexample",
                    redirectPolicy = SettingsRedirectPolicy(
                        redirectConditions = "successful",
                        delay = 0,
                        statusForManualRedirection = "none",
                        redirectButtonCaption = "Back to the Game"
                    )
                )
            )


            XStore.createOrderByItemSku(object : CreateOrderCallback {
                override fun onSuccess(response: CreateOrderResponse) {
                    val token = response.token
                    val intent = XPayments.createIntentBuilder(parentActivity)
                        .accessToken(AccessToken(token))
                        .isSandbox(BuildConfig.IS_SANDBOX)
                        .build()
                    parentActivity.startActivityForResult(intent, 1)
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    val message = errorMessage ?: throwable?.javaClass?.name ?: "Error"
                    Snackbar.make(holder.view, message, Snackbar.LENGTH_LONG).show()
                }
            }, item.sku!!, paymentOptions)
        }
    }

    override fun getItemCount() = items.size
}
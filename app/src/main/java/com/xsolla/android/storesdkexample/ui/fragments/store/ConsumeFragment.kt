package com.xsolla.android.storesdkexample.ui.fragments.store

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.inventory.InventoryResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.fragment_consume.*

class ConsumeFragment : BaseFragment() {

    companion object {
        const val ITEM_ARG = "item"
    }

    override fun getLayout() = R.layout.fragment_consume

    override fun initUI() {
        arguments?.getParcelable<InventoryResponse.Item>(ITEM_ARG)?.let { item ->
            Glide.with(requireActivity()).load(item.imageUrl).into(itemIcon)
            itemName.text = item.name
            updateQuantity(item.quantity)
            goToStoreButton.setOnClickListener { findNavController().navigate(R.id.nav_vi) }

            consumeButton.setOnClickListener { v ->
                ViewUtils.disable(v)
                val quantity = try {
                    quantityInput.text.toString().toLong()
                } catch (e: Exception) {
                    0L
                }
                if (quantity > item.quantity) {
                    updateQuantity(item.quantity)
                    ViewUtils.enable(v)
                    return@setOnClickListener
                }
                XStore.consumeItem(item.sku, quantity, null, object : XStoreCallback<Void>() {
                    override fun onSuccess(response: Void?) {
                        XStore.getInventory(object : XStoreCallback<InventoryResponse>() {
                            override fun onSuccess(response: InventoryResponse) {
                                response.items
                                        .find { it.sku == item.sku }
                                        ?.quantity
                                        ?.let { quantity -> updateQuantity(quantity) }
                                        ?: kotlin.run {
                                            findNavController().navigateUp()
                                            val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                                            inputManager?.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                                        }

                                showSnack("Item consumed")
                                ViewUtils.enable(v)
                            }

                            override fun onFailure(errorMessage: String) {
                                showSnack(errorMessage)
                                ViewUtils.enable(v)
                            }

                        })
                    }

                    override fun onFailure(errorMessage: String) {
                        showSnack(errorMessage)
                        ViewUtils.enable(v)
                    }
                })
            }
        }
    }

    private fun updateQuantity(quantity: Long) {
        quantityLabel.text = String.format("of %s available", quantity)
        quantityInput.setText("1")
    }

}
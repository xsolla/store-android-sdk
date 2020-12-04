package com.xsolla.android.inventorysdkexample.ui.fragments.store

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.inventorysdkexample.util.ViewUtils
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
            goToStoreButton.setOnClickListener { findNavController().navigate(R.id.nav_inventory) }

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
                XInventory.consumeItem(item.sku!!, quantity, null, object : ConsumeItemCallback {
                    override fun onSuccess() {
                        XInventory.getInventory(object : GetInventoryCallback{
                            override fun onSuccess(data: InventoryResponse) {
                                data.items
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

                            override fun onError(throwable: Throwable?, errorMessage: String?) {
                                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                                ViewUtils.enable(v)
                            }

                        })
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
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
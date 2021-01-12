package com.xsolla.android.customauth.ui.store

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentCatalogBinding
import com.xsolla.android.customauth.ui.BaseFragment
import com.xsolla.android.customauth.ui.adapter.ViAdapter
import com.xsolla.android.customauth.viewmodels.VmBalance
import com.xsolla.android.customauth.viewmodels.VmCart

class ViPageFragment : BaseFragment(), PurchaseListener {
    companion object {
        private const val ARG_ITEMS = "items"

        fun getInstance(items: ArrayList<VirtualItemUiEntity>): ViPageFragment {
            val catalogFragment = ViPageFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_ITEMS, items)
            catalogFragment.arguments = bundle
            return catalogFragment
        }
    }

    private val binding: FragmentCatalogBinding by viewBinding()
    private val vmCart: VmCart by activityViewModels()
    private val vmBalance: VmBalance by activityViewModels()

    override fun getLayout() = R.layout.fragment_catalog

    override fun initUI() {
        val items = requireArguments().getParcelableArrayList<VirtualItemUiEntity>(ARG_ITEMS)
        items?.let {
            with(binding.catalogRecyclerView) {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    ContextCompat.getDrawable(context, R.drawable.item_divider)?.let { setDrawable(it) }
                })
                adapter = ViAdapter(it, vmCart, vmBalance, this@ViPageFragment)
            }
        }
    }

    override fun onSuccess() {
        showSnack("Success")
    }

    override fun onFailure(errorMessage: String) {
        showSnack(errorMessage)
    }

    override fun showMessage(message: String) {
        showSnack(message)
    }
}

interface PurchaseListener {
    fun onSuccess()
    fun onFailure(errorMessage: String)
    fun showMessage(message: String)
}
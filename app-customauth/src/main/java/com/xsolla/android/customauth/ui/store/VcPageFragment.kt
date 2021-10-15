package com.xsolla.android.customauth.ui.store

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.ui.vm.VmPurchase
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentCatalogBinding
import com.xsolla.android.customauth.ui.BaseFragment
import com.xsolla.android.customauth.ui.adapter.VcAdapter
import com.xsolla.android.customauth.viewmodels.VmBalance
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse

class VcPageFragment : BaseFragment(), PurchaseListener {
    private val binding: FragmentCatalogBinding by viewBinding()

    private val vmPurchase: VmPurchase by activityViewModels()
    private val vmBalance: VmBalance by activityViewModels()

    companion object {
        private const val ARG_ITEMS = "items"

        fun getInstance(items: ArrayList<VirtualCurrencyPackageResponse.Item>): VcPageFragment {
            val catalogFragment = VcPageFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_ITEMS, items)
            catalogFragment.arguments = bundle
            return catalogFragment
        }
    }

    override fun getLayout() = R.layout.fragment_catalog

    override fun initUI() {
        val items =
            requireArguments().getParcelableArrayList<VirtualCurrencyPackageResponse.Item>(ARG_ITEMS)
        items?.let {
            with(binding.catalogRecyclerView) {
                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    ).apply {
                        ContextCompat.getDrawable(context, R.drawable.item_divider)
                            ?.let { setDrawable(it) }
                    })
                adapter = VcAdapter(it, vmPurchase, vmBalance, this@VcPageFragment)
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
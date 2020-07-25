package com.xsolla.android.storesdkexample.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.VcAdapter
import com.xsolla.android.storesdkexample.listener.PurchaseListener
import com.xsolla.android.storesdkexample.ui.vm.VmBalance
import com.xsolla.android.storesdkexample.ui.vm.VmCart
import com.xsolla.android.storesdkexample.util.BaseParcelable
import kotlinx.android.synthetic.main.fragment_catalog.view.*

class VcPageFragment : Fragment(), PurchaseListener {

    private val vmCart: VmCart by activityViewModels()
    private val vmBalance: VmBalance by activityViewModels()

    companion object {
        const val ARG_ITEMS = "items"

        fun getInstance(items: ArrayList<VirtualCurrencyPackageResponse.Item>): VcPageFragment {
            val catalogFragment = VcPageFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(ARG_ITEMS, items)
            bundle.putParcelable(ARG_ITEMS, BaseParcelable(items))
            catalogFragment.arguments = bundle
            return catalogFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val items = requireArguments().getParcelable<BaseParcelable>(ARG_ITEMS)?.value as? List<VirtualCurrencyPackageResponse.Item>
        items?.let {
            with(view.catalogRecyclerView) {
                val linearLayoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation).apply {
                    ContextCompat.getDrawable(context, R.drawable.item_divider)?.let { setDrawable(it) }
                })
                layoutManager = linearLayoutManager
                adapter = VcAdapter(it, vmCart, vmBalance, this@VcPageFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vmCart.updateCart()
    }

    override fun onSuccess() {
        showSnackBar("Success")
    }

    override fun onFailure(errorMessage: String) {
        showSnackBar(errorMessage)
    }

    override fun showMessage(message: String) {
        showSnackBar(message)
    }

    private fun showSnackBar(message: String) {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }

}
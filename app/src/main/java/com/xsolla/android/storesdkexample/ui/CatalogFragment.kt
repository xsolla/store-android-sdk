package com.xsolla.android.storesdkexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.CatalogAdapter
import com.xsolla.android.storesdkexample.util.BaseParcelable
import kotlinx.android.synthetic.main.fragment_catalog.view.*

class CatalogFragment : Fragment() {

    companion object {
        const val ARG_ITEMS = "items"

        fun getInstance(items: ArrayList<VirtualItemsResponse.Item>): CatalogFragment {
            val catalogFragment = CatalogFragment()
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
        val items = requireArguments().getParcelable<BaseParcelable>(ARG_ITEMS)?.value as? List<VirtualItemsResponse.Item>
        items?.let {
            with(view.catalogRecyclerView) {
                layoutManager = LinearLayoutManager(view.context)
                adapter = CatalogAdapter(it)
            }
        }
    }

}
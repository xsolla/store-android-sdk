package com.xsolla.android.storesdkexample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.vm.VmCart

class CartFragment : Fragment() {

    private val vmCart: VmCart by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vmCart.cartContent.observe(viewLifecycleOwner, Observer {

        })
    }

    override fun onResume() {
        super.onResume()
        vmCart.updateCart()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                CartFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}
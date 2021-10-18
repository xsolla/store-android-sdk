package com.xsolla.android.storesdkexample.ui.fragments.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.storesdkexample.R

abstract class BaseFragment : Fragment() {
    var mainToolbar: View? = null
    private lateinit var rootView: View

    abstract fun getLayout(): Int

    open val toolbarOption: ToolbarOptions = ToolbarOptions(showBalance = true, showCart = true)

    abstract fun initUI()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(getLayout(), container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainToolbar = requireActivity().findViewById(R.id.mainToolbar)

        initUI()

        requireActivity().findViewById<View>(R.id.balanceLayout)?.isVisible = toolbarOption.showBalance
        requireActivity().invalidateOptionsMenu()
    }

    fun showSnack(message: String) {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }

    fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

    class ToolbarOptions(val showBalance: Boolean, val showCart: Boolean)
}
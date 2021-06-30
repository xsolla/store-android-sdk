package com.xsolla.android.customauth.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.ui.store.StoreActivity

abstract class BaseFragment : Fragment() {
    lateinit var rootView: View

    abstract fun getLayout(): Int

    open val toolbarOption: ToolbarOptions = ToolbarOptions(true, true, true)

    abstract fun initUI()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(getLayout(), container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AmountUtils.init(requireContext())
        initUI()

        requireActivity().findViewById<View>(R.id.mainToolbar).isVisible = toolbarOption.showMainToolbar
        requireActivity().findViewById<View>(R.id.balanceLayout).isVisible = toolbarOption.showBalance
        (requireActivity() as? StoreActivity)?.showCartMenu = toolbarOption.showCart
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

    class ToolbarOptions(val showMainToolbar: Boolean = true, val showBalance: Boolean = true, val showCart: Boolean = true)
}
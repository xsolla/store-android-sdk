package com.xsolla.android.customauth.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.databinding.FragmentViBinding

class ViFragment : Fragment(R.layout.fragment_vi) {
    private val binding: FragmentViBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
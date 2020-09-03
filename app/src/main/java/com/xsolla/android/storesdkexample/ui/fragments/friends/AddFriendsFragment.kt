package com.xsolla.android.storesdkexample.ui.fragments.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.ui.vm.VmAddFriends
import kotlinx.android.synthetic.main.fragment_add_friends.*

class AddFriendsFragment : Fragment() {

    private val vmAddFriends: VmAddFriends by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchInput.addTextChangedListener {
            vmAddFriends.currentSearchQuery.value = it?.toString() ?: ""
        }
        vmAddFriends.loadAllSocialFriends()
    }
}
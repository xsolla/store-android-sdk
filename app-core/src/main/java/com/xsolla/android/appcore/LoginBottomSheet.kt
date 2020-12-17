package com.xsolla.android.appcore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.login_bottom_sheet.*

class LoginBottomSheet : BottomSheetDialogFragment() {

    lateinit var listener: SocialClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.login_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listener = parentFragment as SocialClickListener
        twitterButton.setOnClickListener {
            listener.onSocialClicked(SocialNetworks.TWITTER)
            dismiss()
        }
        linkedinButton.setOnClickListener {
            listener.onSocialClicked(SocialNetworks.LINKEDIN)
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginBottomSheet()
    }

    enum class SocialNetworks {
        TWITTER, LINKEDIN
    }

    interface SocialClickListener {
        fun onSocialClicked(socialNetwork: SocialNetworks)
    }
}
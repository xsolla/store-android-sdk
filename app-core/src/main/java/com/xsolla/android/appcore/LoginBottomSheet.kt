package com.xsolla.android.appcore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginBottomSheet : BottomSheetDialogFragment() {

    lateinit var listener: SocialClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.login_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listener = parentFragment as SocialClickListener
        view.findViewById<Button>(R.id.twitterButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.TWITTER)
            dismiss()
        }
        view.findViewById<Button>(R.id.linkedinButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.LINKEDIN)
            dismiss()
        }
        view.findViewById<Button>(R.id.battlenetButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.BATTLENET)
            dismiss()
        }
        view.findViewById<Button>(R.id.discordButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.DISCORD)
            dismiss()
        }
        view.findViewById<Button>(R.id.githubButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.GITHUB)
            dismiss()
        }
        view.findViewById<Button>(R.id.kakaoButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.KAKAO)
            dismiss()
        }
        view.findViewById<Button>(R.id.qqButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.QQ)
            dismiss()
        }
        view.findViewById<Button>(R.id.redditButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.REDDIT)
            dismiss()
        }
        view.findViewById<Button>(R.id.steamButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.STEAM)
            dismiss()
        }
        view.findViewById<Button>(R.id.twitchButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.TWITCH)
            dismiss()
        }
        view.findViewById<Button>(R.id.vkButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.VK)
            dismiss()
        }
        view.findViewById<Button>(R.id.vimeoButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.VIMEO)
            dismiss()
        }
        view.findViewById<Button>(R.id.wechatButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.WECHAT)
            dismiss()
        }
        view.findViewById<Button>(R.id.weiboButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.WEIBO)
            dismiss()
        }
        view.findViewById<Button>(R.id.yahooButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.YAHOO)
            dismiss()
        }
        view.findViewById<Button>(R.id.yandexButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.YANDEX)
            dismiss()
        }
        view.findViewById<Button>(R.id.youtubeButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.YOUTUBE)
            dismiss()
        }
        view.findViewById<Button>(R.id.xboxButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.XBOX)
            dismiss()
        }
        view.findViewById<Button>(R.id.msnButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.MSN)
            dismiss()
        }
        view.findViewById<Button>(R.id.okButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.OK)
            dismiss()
        }
        view.findViewById<Button>(R.id.paypalButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.PAYPAL)
            dismiss()
        }
        view.findViewById<Button>(R.id.naverButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.NAVER)
            dismiss()
        }
        view.findViewById<Button>(R.id.appleButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.APPLE)
            dismiss()
        }
        view.findViewById<Button>(R.id.amazonButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.AMAZON)
            dismiss()
        }
        view.findViewById<Button>(R.id.mailruButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.MAILRU)
            dismiss()
        }
        view.findViewById<Button>(R.id.microsoftButton).setOnClickListener {
            listener.onSocialClicked(SocialNetworks.MICROSOFT)
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginBottomSheet()
    }

    enum class SocialNetworks {
        TWITTER, LINKEDIN, BATTLENET, DISCORD, GITHUB, KAKAO, QQ, REDDIT, STEAM, TWITCH, VK, VIMEO,
        WECHAT, WEIBO, YAHOO, YANDEX, YOUTUBE, XBOX, MSN, OK, PAYPAL, NAVER, APPLE, AMAZON, MAILRU, MICROSOFT
    }

    interface SocialClickListener {
        fun onSocialClicked(socialNetwork: SocialNetworks)
    }
}
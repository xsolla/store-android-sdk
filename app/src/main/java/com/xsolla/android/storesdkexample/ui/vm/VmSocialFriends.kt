package com.xsolla.android.storesdkexample.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetSocialFriendsCallback
import com.xsolla.android.login.callback.LinkedSocialNetworksCallback
import com.xsolla.android.login.callback.UpdateSocialFriendsCallback
import com.xsolla.android.login.entity.response.LinkedSocialNetworkResponse
import com.xsolla.android.login.entity.response.SocialFriend
import com.xsolla.android.login.entity.response.SocialFriendsResponse
import com.xsolla.android.login.social.FriendsPlatform
import com.xsolla.android.login.social.SocialNetworkForLinking
import com.xsolla.android.storesdkexample.util.SingleLiveEvent

class VmSocialFriends(application: Application) : AndroidViewModel(application) {

    val linkedSocialNetworks = MutableLiveData<List<SocialNetworkForLinking?>>(listOf())

    val socialFriendsList = MutableLiveData<MutableList<SocialFriend>>(mutableListOf())

    val hasError = SingleLiveEvent<Boolean>()

    init {
        hasError.value = false
    }

    fun loadAllSocialFriends() {
        socialFriendsList.value?.clear()
        socialFriendsList.value = socialFriendsList.value
        loadSocialFriends(FriendsPlatform.FACEBOOK) {
            loadSocialFriends(FriendsPlatform.TWITTER) {
                loadSocialFriends(FriendsPlatform.VK)
            }
        }
    }

    private fun loadSocialFriends(friendsPlatform: FriendsPlatform, callback: (() -> Unit)? = null) {
        XLogin.getSocialFriends(friendsPlatform, VmAddFriends.REQUEST_OFFSET, VmAddFriends.REQUEST_LIMIT, false, object : GetSocialFriendsCallback {
            override fun onSuccess(data: SocialFriendsResponse) {
                socialFriendsList.value?.addAll(data.friendsList)
                socialFriendsList.value = socialFriendsList.value
                callback?.invoke()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                throwable?.printStackTrace()
                hasError.value = true
            }
        })
    }

    fun loadLinkedSocialAccounts() {
        XLogin.getLinkedSocialNetworks(object : LinkedSocialNetworksCallback {
            override fun onSuccess(data: List<LinkedSocialNetworkResponse>) {
                linkedSocialNetworks.value = data.map { it.provider }
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                throwable?.printStackTrace()
            }
        })
    }

    fun updateSocialFriends() {
        XLogin.updateSocialFriends(FriendsPlatform.VK, object : UpdateSocialFriendsCallback {
            override fun onSuccess() {
                loadAllSocialFriends()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                throwable?.printStackTrace()
            }
        })
    }

    data class SocialFriendUiEntity(
        val xsollaId: String?,
        val socialId: String,
        val imageUrl: String?,
        val nickname: String,
        val fromPlatform: SocialNetworkForLinking
    )
}
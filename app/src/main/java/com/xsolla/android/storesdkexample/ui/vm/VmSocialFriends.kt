package com.xsolla.android.storesdkexample.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetSocialFriendsCallback
import com.xsolla.android.login.callback.LinkedSocialNetworksCallback
import com.xsolla.android.login.entity.response.LinkedSocialNetworkResponse
import com.xsolla.android.login.entity.response.SocialFriend
import com.xsolla.android.login.entity.response.SocialFriendsResponse
import com.xsolla.android.login.social.FriendsPlatform
import com.xsolla.android.login.social.SocialNetworkForLinking

class VmSocialFriends(application: Application) : AndroidViewModel(application) {

    val linkedSocialNetworks = MutableLiveData<List<SocialNetworkForLinking?>>(listOf())

    val socialFriendsList = MutableLiveData<MutableList<SocialFriend>>(mutableListOf())

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
                socialFriendsList.value?.add(SocialFriend(null, "Friend", SocialNetworkForLinking.FACEBOOK, "111", "123")) //TODO remove
                socialFriendsList.value = socialFriendsList.value
                println("!!! ${socialFriendsList.value}") // TODO
                callback?.invoke()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                throwable?.printStackTrace()
                println("!!! $errorMessage") // TODO
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

}
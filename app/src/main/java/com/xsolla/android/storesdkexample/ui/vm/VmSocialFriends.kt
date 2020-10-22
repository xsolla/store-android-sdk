package com.xsolla.android.storesdkexample.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetSocialFriendsCallback
import com.xsolla.android.login.callback.LinkedSocialNetworksCallback
import com.xsolla.android.login.callback.UpdateSocialFriendsCallback
import com.xsolla.android.login.entity.response.LinkedSocialNetworkResponse
import com.xsolla.android.login.entity.response.SocialFriendsResponse
import com.xsolla.android.login.social.FriendsPlatform
import com.xsolla.android.login.social.SocialNetworkForLinking
import com.xsolla.android.storesdkexample.util.SingleLiveEvent

class VmSocialFriends(application: Application) : AndroidViewModel(application) {

    val linkedSocialNetworks = MutableLiveData<List<SocialNetworkForLinking?>>(listOf())

    val socialFriendsList = MutableLiveData<List<SocialFriendUiEntity>>(listOf())

    val hasError = SingleLiveEvent<String>()

    fun loadAllSocialFriends() {
        XLogin.getSocialFriends(null, VmAddFriends.REQUEST_OFFSET, VmAddFriends.REQUEST_LIMIT, false, object : GetSocialFriendsCallback {
            override fun onSuccess(data: SocialFriendsResponse) {

                val groupByXsollaId = data.friendsList.groupBy { it.xsollaUserId }
                val socialFriends = mutableListOf<SocialFriendUiEntity>()
                groupByXsollaId.forEach { (xsollaId, friends) ->
                    if (xsollaId == null) {
                        socialFriends.addAll(friends.map {
                            SocialFriendUiEntity(
                                    null,
                                    it.socialNetworkUserId,
                                    it.avatar,
                                    it.name,
                                    listOf(it.platform)
                            )
                        })
                    } else {
                        socialFriends.add(
                                SocialFriendUiEntity(
                                        friends.first().xsollaUserId,
                                        null,
                                        friends.find { !it.avatar.isNullOrEmpty() }?.avatar,
                                        friends.find { !it.name.isBlank() }?.name ?: "",
                                        friends.map { it.platform }
                                )
                        )
                    }
                }

                socialFriendsList.value = socialFriends
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                throwable?.printStackTrace()
                hasError.value = errorMessage ?: throwable?.javaClass?.name ?: "Failure"
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
            val socialId: String?,
            val imageUrl: String?,
            val nickname: String,
            val fromPlatform: List<SocialNetworkForLinking>
    )
}
package com.xsolla.android.storesdkexample.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.*
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequestAction
import com.xsolla.android.login.entity.request.UserFriendsRequestSortBy
import com.xsolla.android.login.entity.request.UserFriendsRequestSortOrder
import com.xsolla.android.login.entity.request.UserFriendsRequestType
import com.xsolla.android.login.entity.response.LinkedSocialNetworkResponse
import com.xsolla.android.login.entity.response.SocialFriendsResponse
import com.xsolla.android.login.entity.response.UserFriendsResponse
import com.xsolla.android.login.social.FriendsPlatform
import com.xsolla.android.login.social.SocialNetworkForLinking
import com.xsolla.android.storesdkexample.util.SingleLiveEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VmSocialFriends(application: Application) : AndroidViewModel(application) {

    val linkedSocialNetworks = MutableLiveData<List<SocialNetworkForLinking?>>(listOf())

    val socialFriendsList = MutableLiveData<List<SocialFriendUiEntity>>(listOf())

    private var friendsActive = listOf<FriendUiEntity>()
    private var friendsRequested = listOf<FriendUiEntity>()
    private var friendsRequestedBy = listOf<FriendUiEntity>()
    private var friendsBlocked = listOf<FriendUiEntity>()
    private var friendsBlockedBy = listOf<FriendUiEntity>()

    val hasError = SingleLiveEvent<String>()

    private suspend fun loadAllXsollaFriends() {
        friendsActive = loadItemsByTab(UserFriendsRequestType.FRIENDS, FriendsRelationship.STANDARD)
        friendsRequested = loadItemsByTab(UserFriendsRequestType.FRIEND_REQUESTED, FriendsRelationship.REQUESTED)
        friendsRequestedBy = loadItemsByTab(UserFriendsRequestType.FRIEND_REQUESTED_BY, FriendsRelationship.PENDING)
        friendsBlocked = loadItemsByTab(UserFriendsRequestType.BLOCKED, FriendsRelationship.BLOCKED)
        friendsBlockedBy = loadItemsByTab(UserFriendsRequestType.BLOCKED_BY, FriendsRelationship.BLOCKED_BY)
    }

    private suspend fun loadItemsByTab(requestType: UserFriendsRequestType, relationship: FriendsRelationship): List<FriendUiEntity> = suspendCoroutine { continuation ->
        XLogin.getCurrentUserFriends(
                null,
                VmAddFriends.MAX_LIMIT_FOR_LOADING_FRIENDS,
                requestType,
                UserFriendsRequestSortBy.BY_UPDATED,
                UserFriendsRequestSortOrder.ASC,
                object : GetCurrentUserFriendsCallback {
                    override fun onSuccess(data: UserFriendsResponse) {
                        if (data.relationships.isNotEmpty()) {
                            val uiEntities = data.toUiEntities(relationship)
                            continuation.resume(uiEntities)
                        } else {
                            continuation.resume(listOf())
                        }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        hasError.value = errorMessage ?: throwable?.javaClass?.name ?: "Failure"
                        continuation.resume(listOf())
                    }
                }
        )
    }

    fun loadAllSocialFriends() = viewModelScope.launch {
        loadAllXsollaFriends()
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
                                    null,
                                    it.avatar,
                                    it.name,
                                    listOf(it.platform)
                            )
                        })
                    } else {
                        val relationship = findRelationship(xsollaId)
                        if (relationship != FriendsRelationship.BLOCKED && relationship != FriendsRelationship.BLOCKED_BY) {
                            socialFriends.add(
                                    SocialFriendUiEntity(
                                            xsollaId,
                                            null,
                                            relationship,
                                            friends.find { !it.avatar.isNullOrEmpty() }?.avatar,
                                            friends.find { !it.name.isBlank() }?.name ?: "",
                                            friends.map { it.platform }
                                    )
                            )
                        }
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

    private fun findRelationship(xsollaId: String): FriendsRelationship {
        return friendsActive.find { it.id == xsollaId }?.relationship
                ?: friendsRequested.find { it.id == xsollaId }?.relationship
                ?: friendsRequestedBy.find { it.id == xsollaId }?.relationship
                ?: friendsBlocked.find { it.id == xsollaId }?.relationship
                ?: friendsBlockedBy.find { it.id == xsollaId }?.relationship
                ?: FriendsRelationship.NONE
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

    fun reloadAfterChange() {
        viewModelScope.launch {
            delay(1000)
            loadAllSocialFriends()
        } // TODO better async
    }

    fun updateFriendship(user: FriendUiEntity, updateType: UpdateUserFriendsRequestAction) {
        XLogin.updateCurrentUserFriend(user.id, updateType, object : UpdateCurrentUserFriendsCallback {
            override fun onSuccess() {
                reloadAfterChange()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                hasError.value = errorMessage ?: throwable?.javaClass?.name ?: "Failure"
            }
        })
    }

    data class SocialFriendUiEntity(
            val xsollaId: String?,
            val socialId: String?,
            val relationship: FriendsRelationship?,
            val imageUrl: String?,
            val nickname: String,
            val fromPlatform: List<SocialNetworkForLinking>
    ) {
        fun toFriendUiEntity(): FriendUiEntity =
                FriendUiEntity(xsollaId ?: "", imageUrl, false, nickname, relationship ?: FriendsRelationship.NONE)
    }
}
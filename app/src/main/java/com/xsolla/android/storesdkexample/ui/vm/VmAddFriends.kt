package com.xsolla.android.storesdkexample.ui.vm

import android.app.Application
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserFriendsCallback
import com.xsolla.android.login.callback.SearchUsersByNicknameCallback
import com.xsolla.android.login.callback.UpdateCurrentUserFriendsCallback
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequestAction
import com.xsolla.android.login.entity.request.UserFriendsRequestSortBy
import com.xsolla.android.login.entity.request.UserFriendsRequestSortOrder
import com.xsolla.android.login.entity.request.UserFriendsRequestType
import com.xsolla.android.login.entity.response.SearchUsersByNicknameResponse
import com.xsolla.android.login.entity.response.UserFriendsResponse
import com.xsolla.android.storesdkexample.util.SingleLiveEvent
import kotlinx.coroutines.*

class VmAddFriends(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_MIN_LENGTH = 3
        private const val SEARCH_DELAY = 1000L // Login API has a limit of 1rps for search
        const val REQUEST_OFFSET = 0
        const val REQUEST_LIMIT = 100

        private const val MAX_LIMIT_FOR_LOADING_FRIENDS = 50
    }

    val currentSearchQuery = MutableLiveData("")

    val searchResultList = MutableLiveData<List<FriendUiEntity>>(listOf())
    private var searchJob: Job? = null

    private val searchObserver = Observer<String> {
        searchJob?.cancel()
        if (it.length >= SEARCH_MIN_LENGTH) {
            searchJob = GlobalScope.launch(Dispatchers.Main) {
                delay(SEARCH_DELAY)
                doSearch(it)
            }
        }
    }

    private val friendsActive = mutableListOf<FriendUiEntity>()
    private val friendsRequested = mutableListOf<FriendUiEntity>()
    private val friendsRequestedBy = mutableListOf<FriendUiEntity>()
    private val friendsBlocked = mutableListOf<FriendUiEntity>()
    private val friendsBlockedBy = mutableListOf<FriendUiEntity>()

    val hasError = SingleLiveEvent<Boolean>()

    init {
        hasError.value = false
    }


    init {
        currentSearchQuery.observeForever(searchObserver)
    }

    override fun onCleared() {
        super.onCleared()
        currentSearchQuery.removeObserver(searchObserver)
    }

    private fun doSearch(query: String) {
        XLogin.searchUsersByNickname(query, REQUEST_OFFSET, REQUEST_LIMIT, object : SearchUsersByNicknameCallback {
            override fun onSuccess(data: SearchUsersByNicknameResponse) {
                val newList = mutableListOf<FriendUiEntity>()
                for (user in data.users) {
                    if (user.isCurrentUser) {
                        continue
                    }
                    if (user.xsollaUserId in friendsActive.map { it.id }) {
                        newList.add(friendsActive.find { it.id == user.xsollaUserId }!!)
                        continue
                    }
                    if (user.xsollaUserId in friendsRequested.map { it.id }) {
                        newList.add(friendsRequested.find { it.id == user.xsollaUserId }!!)
                        continue
                    }
                    if (user.xsollaUserId in friendsRequestedBy.map { it.id }) {
                        newList.add(friendsRequestedBy.find { it.id == user.xsollaUserId }!!)
                        continue
                    }
                    if (user.xsollaUserId in friendsBlocked.map { it.id }) {
                        continue
                    }
                    if (user.xsollaUserId in friendsBlockedBy.map { it.id }) {
                        continue
                    }
                    newList.add(
                            FriendUiEntity(user.xsollaUserId, user.avatar, false, user.nickname, FriendsRelationship.NONE)
                    )
                }
                searchResultList.value = newList
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                searchResultList.value = listOf()
                throwable?.printStackTrace()
            }

        })
    }

    fun loadAllFriends() {
        loadItemsByTab(UserFriendsRequestType.FRIENDS, FriendsRelationship.STANDARD, friendsActive)
        loadItemsByTab(UserFriendsRequestType.FRIEND_REQUESTED, FriendsRelationship.REQUESTED, friendsRequested)
        loadItemsByTab(UserFriendsRequestType.FRIEND_REQUESTED_BY, FriendsRelationship.PENDING, friendsRequestedBy)
        loadItemsByTab(UserFriendsRequestType.BLOCKED, FriendsRelationship.BLOCKED, friendsBlocked)
        loadItemsByTab(UserFriendsRequestType.BLOCKED_BY, FriendsRelationship.BLOCKED_BY, friendsBlockedBy)
    }

    private fun loadItemsByTab(requestType: UserFriendsRequestType, relationship: FriendsRelationship, items: MutableList<FriendUiEntity>) {
        items.clear()
        XLogin.getCurrentUserFriends(
                null,
                MAX_LIMIT_FOR_LOADING_FRIENDS,
                requestType,
                UserFriendsRequestSortBy.BY_UPDATED,
                UserFriendsRequestSortOrder.ASC,
                object : GetCurrentUserFriendsCallback {
                    override fun onSuccess(data: UserFriendsResponse) {
                        if (data.relationships.isNotEmpty()) {
                            val uiEntities = data.toUiEntities(relationship)
                            items.apply { addAll(uiEntities) }
                        }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        hasError.value = true
                    }
                }
        )
    }

    fun reloadAfterChange() {
        loadAllFriends()
        Handler().postDelayed({
            currentSearchQuery.value?.let {
                doSearch(it)
            }
        }, 2000) // TODO better async
    }

    fun updateFriendship(user: FriendUiEntity, updateType: UpdateUserFriendsRequestAction) {
        XLogin.updateCurrentUserFriend(user.id, updateType, object : UpdateCurrentUserFriendsCallback {
            override fun onSuccess() {
                reloadAfterChange()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                hasError.value = true
            }
        })
    }

}
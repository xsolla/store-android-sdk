package com.xsolla.android.storesdkexample.ui.vm

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserFriendsCallback
import com.xsolla.android.login.callback.UpdateCurrentUserFriendsCallback
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequestAction
import com.xsolla.android.login.entity.request.UserFriendsRequestSortBy
import com.xsolla.android.login.entity.request.UserFriendsRequestSortOrder
import com.xsolla.android.login.entity.request.UserFriendsRequestType
import com.xsolla.android.login.entity.response.Presence
import com.xsolla.android.login.entity.response.UserFriendsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.util.SingleLiveEvent

class VmFriends : ViewModel() {

    private companion object {
        private const val MAX_LIMIT_FOR_LOADING_FRIENDS = 50
    }

    val items = MutableLiveData<List<FriendUiEntity>>(listOf())
    val tab = MutableLiveData(FriendsTab.FRIENDS)

    val isSearch = MutableLiveData(false)
    val searchQuery = MutableLiveData("")

    val hasError = SingleLiveEvent<Boolean>()

    init {
        hasError.value = false
    }

    fun getItems() = items.value!!

    fun updateTab(tab: FriendsTab) {
        this.tab.value = tab
    }

    fun clearTemporaryRelationship() {
        val updatedItems = getItems().apply {
            forEach {
                it.temporaryRelationship = null
            }
        }
        items.value = updatedItems
    }

    fun loadAllFriends() {
        loadItemsByTab(FriendsTab.FRIENDS)
        loadItemsByTab(FriendsTab.PENDING)
        loadItemsByTab(FriendsTab.REQUESTED)
        loadItemsByTab(FriendsTab.BLOCKED)
    }

    fun clearAllFriends() {
        items.value = getItems().toMutableList().apply { clear() }
    }

    fun getItemsCountByTab(): Map<FriendsTab, Int> {
        val groupedItems = getItems().groupBy { it.relationship }
        return mutableMapOf<FriendsTab, Int>().apply { // Is there better way?
            put(FriendsTab.FRIENDS, groupedItems[FriendsTab.FRIENDS.relationship]?.size ?: 0)
            put(FriendsTab.PENDING, groupedItems[FriendsTab.PENDING.relationship]?.size ?: 0)
            put(FriendsTab.REQUESTED, groupedItems[FriendsTab.REQUESTED.relationship]?.size ?: 0)
            put(FriendsTab.BLOCKED, groupedItems[FriendsTab.BLOCKED.relationship]?.size ?: 0)
        }
    }

    fun getItemsByTab(tab: FriendsTab) = getItems().filter {
        it.relationship == tab.relationship || it.temporaryRelationship in tab.temporaryRelationships
    }

    fun updateFriend(friend: FriendUiEntity, strategy: UpdateFriendStrategy) {
        strategy.update(friend, items) { hasError.value = true }
    }

    sealed class UpdateFriendStrategy {
        abstract fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit)

        object DeleteStrategy : UpdateFriendStrategy() {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriend(friend.id, UpdateUserFriendsRequestAction.FRIEND_REMOVE, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        items.value = items.value!!.toMutableList().apply { remove(friend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        }
        object BlockStrategy : UpdateFriendStrategy() {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriend(friend.id, UpdateUserFriendsRequestAction.BLOCK, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        val updatedFriend = friend.copy(relationship = FriendsRelationship.BLOCKED, temporaryRelationship = null)
                        val index = items.value!!.indexOf(friend)
                        items.value = items.value!!.toMutableList().apply { set(index, updatedFriend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        }
        object UnblockStrategy : UpdateFriendStrategy() {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriend(friend.id, UpdateUserFriendsRequestAction.UNBLOCK, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        val updatedFriend = friend.copy(relationship = FriendsRelationship.NONE, temporaryRelationship = TemporaryFriendRelationship.UNBLOCKED)
                        val index = items.value!!.indexOf(friend)
                        items.value = items.value!!.toMutableList().apply { set(index, updatedFriend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        }
        object AcceptStrategy : UpdateFriendStrategy() {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriend(friend.id, UpdateUserFriendsRequestAction.FRIEND_REQUEST_APPROVE, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        val updatedFriend = friend.copy(relationship = FriendsRelationship.STANDARD, temporaryRelationship = TemporaryFriendRelationship.REQUEST_ACCEPTED)
                        val index = items.value!!.indexOf(friend)
                        items.value = items.value!!.toMutableList().apply { set(index, updatedFriend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        }
        object DeclineStrategy : UpdateFriendStrategy() {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriend(friend.id, UpdateUserFriendsRequestAction.FRIEND_REQUEST_DENY, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        val updatedFriend = friend.copy(relationship = FriendsRelationship.NONE, temporaryRelationship = TemporaryFriendRelationship.REQUEST_DENIED)
                        val index = items.value!!.indexOf(friend)
                        items.value = items.value!!.toMutableList().apply { set(index, updatedFriend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        }
        class CancelStrategy(private val from: FriendsTab) : UpdateFriendStrategy() {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriend(friend.id, UpdateUserFriendsRequestAction.FRIEND_REQUEST_CANCEL, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        val updatedFriend = if (from == FriendsTab.BLOCKED) {
                            friend.copy(relationship = FriendsRelationship.NONE, temporaryRelationship = TemporaryFriendRelationship.UNBLOCKED)
                        } else {
                            friend.copy(relationship = FriendsRelationship.NONE, temporaryRelationship = TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST)
                        }
                        val index = items.value!!.indexOf(friend)
                        items.value = items.value!!.toMutableList().apply { set(index, updatedFriend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        }
        class AddStrategy(private val from: FriendsTab) : UpdateFriendStrategy() {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriend(friend.id, UpdateUserFriendsRequestAction.FRIEND_REQUEST_ADD, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        val updatedFriend = if (from == FriendsTab.BLOCKED) {
                            friend.copy(relationship = FriendsRelationship.REQUESTED, temporaryRelationship = TemporaryFriendRelationship.UNBLOCKED_AND_REQUEST_FRIEND)
                        } else {
                            friend.copy(relationship = FriendsRelationship.REQUESTED, temporaryRelationship = null)
                        }
                        val index = items.value!!.indexOf(friend)
                        items.value = items.value!!.toMutableList().apply { set(index, updatedFriend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        }
    }

    fun handleSearchMode(isSearch: Boolean) {
        this.isSearch.value = isSearch
    }

    fun handleSearch(query: String?) {
        query ?: return
        searchQuery.value = query
    }

    private fun loadItemsByTab(tab: FriendsTab) {
        XLogin.getCurrentUserFriends(
            null,
            MAX_LIMIT_FOR_LOADING_FRIENDS,
            tab.requestType,
            UserFriendsRequestSortBy.BY_UPDATED,
            UserFriendsRequestSortOrder.ASC,
            object : GetCurrentUserFriendsCallback {
                override fun onSuccess(data: UserFriendsResponse) {
                    if (data.relationships.isNotEmpty()) {
                        val uiEntities = data.toUiEntities(tab)
                        items.value = getItems().toMutableList().apply { addAll(uiEntities) }
                    }
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    hasError.value = true
                }
            }
        )
    }
}

enum class FriendsTab(
    val position: Int,
    val title: String,
    val requestType: UserFriendsRequestType,
    val relationship: FriendsRelationship,
    val temporaryRelationships: List<TemporaryFriendRelationship>,
    @StringRes val placeholderText: Int
) {
    FRIENDS(
        0,
        "FRIENDS",
        UserFriendsRequestType.FRIENDS,
        FriendsRelationship.STANDARD,
        listOf(),
        R.string.friends_tab_placeholder
    ),
    PENDING(
        1,
        "PENDING",
        UserFriendsRequestType.FRIEND_REQUESTED_BY,
        FriendsRelationship.PENDING,
        listOf(TemporaryFriendRelationship.REQUEST_ACCEPTED, TemporaryFriendRelationship.REQUEST_DENIED),
        R.string.friends_pending_tab_placeholder
    ),
    REQUESTED(
        2,
        "REQUESTS",
        UserFriendsRequestType.FRIEND_REQUESTED,
        FriendsRelationship.REQUESTED,
        listOf(TemporaryFriendRelationship.CANCEL_MY_OWN_REQUEST),
        R.string.friends_requested_tab_placeholder
    ),
    BLOCKED(
        3,
        "BLOCKED",
        UserFriendsRequestType.BLOCKED,
        FriendsRelationship.BLOCKED,
        listOf(TemporaryFriendRelationship.UNBLOCKED, TemporaryFriendRelationship.UNBLOCKED_AND_REQUEST_FRIEND),
        R.string.friends_blocked_tab_placeholder
    );

    companion object {
        fun getBy(position: Int): FriendsTab {
            return values().find { it.position == position } ?: throw IllegalArgumentException()
        }
    }
}

enum class FriendsRelationship {
    STANDARD,
    PENDING,
    REQUESTED,
    BLOCKED,
    NONE
}

enum class TemporaryFriendRelationship {
    REQUEST_ACCEPTED,
    REQUEST_DENIED,
    CANCEL_MY_OWN_REQUEST,
    UNBLOCKED,
    UNBLOCKED_AND_REQUEST_FRIEND
}

data class FriendUiEntity(
    val id: String,
    val imageUrl: String?,
    val isOnline: Boolean,
    val nickname: String,
    val relationship: FriendsRelationship,
    var temporaryRelationship: TemporaryFriendRelationship? = null
)

fun UserFriendsResponse.toUiEntities(tab: FriendsTab): List<FriendUiEntity> {
    val list = mutableListOf<FriendUiEntity>()
    this.relationships.forEach {
        val id = it.user.id
        val imageUrl = it.user.picture
        val isOnline = it.presence?.let { presence -> presence == Presence.ONLINE } ?: false
        val nickname = it.user.nickname ?: it.user.name ?: it.user.firstName ?: it.user.lastName ?: "Nickname is empty"
        val relationship = tab.relationship
        list.add(FriendUiEntity(id, imageUrl, isOnline, nickname, relationship))
    }
    return list
}
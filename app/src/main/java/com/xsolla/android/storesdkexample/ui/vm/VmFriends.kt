package com.xsolla.android.storesdkexample.ui.vm

import android.os.Parcelable
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
import kotlinx.android.parcel.Parcelize

class VmFriends : ViewModel() {

    val viewState = MutableLiveData(ViewState.LOADING)
    val items = MutableLiveData<List<FriendUiEntity>>(listOf())

    fun getItems() = items.value!!

    fun loadAllFriends() {
        loadItemsByTab(FriendsTab.FRIENDS)
        loadItemsByTab(FriendsTab.PENDING)
        loadItemsByTab(FriendsTab.REQUESTED)
        loadItemsByTab(FriendsTab.BLOCKED)
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

    fun getItemsByTab(tab: FriendsTab): List<FriendUiEntity> {
        return getItems().filter { it.relationship == tab.relationship }
    }

    fun updateFriend(friend: FriendUiEntity, strategy: UpdateFriendStrategy) {
        strategy.update(friend, items) { viewState.value = ViewState.FAILURE }
    }

    enum class UpdateFriendStrategy {
        DeleteStrategy {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriends(friend.id, UpdateUserFriendsRequestAction.FRIEND_REMOVE, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        items.value = items.value!!.toMutableList().apply { remove(friend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        },
        BlockStrategy {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriends(friend.id, UpdateUserFriendsRequestAction.BLOCK, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        val updatedFriend = friend.copy(relationship = FriendsRelationship.BLOCKED)
                        val index = items.value!!.indexOf(friend)
                        items.value = items.value!!.toMutableList().apply { set(index, updatedFriend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        },
        UnblockStrategy {
            override fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit) {
                XLogin.updateCurrentUserFriends(friend.id, UpdateUserFriendsRequestAction.UNBLOCK, object : UpdateCurrentUserFriendsCallback {
                    override fun onSuccess() {
                        items.value = items.value!!.toMutableList().apply { remove(friend) }
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        onFailure()
                    }
                })
            }
        };

        abstract fun update(friend: FriendUiEntity, items: MutableLiveData<List<FriendUiEntity>>, onFailure: () -> Unit)
    }

    private fun loadItemsByTab(tab: FriendsTab) {
        XLogin.getCurrentUserFriends(
            null,
            50,
            tab.requestType,
            UserFriendsRequestSortBy.BY_UPDATED,
            UserFriendsRequestSortOrder.ASC,
            object : GetCurrentUserFriendsCallback {
                override fun onSuccess(data: UserFriendsResponse) {
                    if (data.relationships.isEmpty()) {
                        viewState.value = ViewState.EMPTY
                    } else {
                        viewState.value = ViewState.SUCCESS
                        val uiEntities = data.toUiEntities(tab)
                        items.value = getItems().toMutableList().apply { addAll(uiEntities) }
                    }
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    viewState.value = ViewState.FAILURE
                }
            }
        )
    }

    enum class ViewState {
        SUCCESS,
        EMPTY,
        FAILURE,
        LOADING
    }
}

enum class FriendsTab(
    val position: Int,
    val title: String,
    val requestType: UserFriendsRequestType,
    val relationship: FriendsRelationship
) {
    FRIENDS(0, "FRIENDS", UserFriendsRequestType.FRIENDS, FriendsRelationship.STANDARD),
    PENDING(1, "PENDING", UserFriendsRequestType.FRIEND_REQUESTED_BY, FriendsRelationship.PENDING),
    REQUESTED(2, "MY REQUESTS", UserFriendsRequestType.FRIEND_REQUESTED, FriendsRelationship.REQUESTED),
    BLOCKED(3, "BLOCKED", UserFriendsRequestType.BLOCKED, FriendsRelationship.BLOCKED);

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
    BLOCKED
}

@Parcelize
data class FriendUiEntity(
    val id: String,
    val imageUrl: String?,
    val isOnline: Boolean,
    val nickname: String,
    val relationship: FriendsRelationship
) : Parcelable

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
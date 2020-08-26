package com.xsolla.android.storesdkexample.ui.vm

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserFriendsCallback
import com.xsolla.android.login.entity.request.UserFriendsRequestSortBy
import com.xsolla.android.login.entity.request.UserFriendsRequestSortOrder
import com.xsolla.android.login.entity.request.UserFriendsRequestType
import com.xsolla.android.login.entity.response.Presence
import com.xsolla.android.login.entity.response.UserFriendsResponse
import kotlinx.android.parcel.Parcelize

class VmFriends : ViewModel() {

    val viewState = MutableLiveData(ViewState.LOADING)
    val items = MutableLiveData<List<FriendUiEntity>>(listOf())
    val tab = MutableLiveData(FriendsTab.FRIENDS)

    fun updateItems(items: List<FriendUiEntity>) {
        this.items.value = items
    }

    fun updateTab(tab: FriendsTab) {
        this.tab.value = tab
    }

    fun getItems() = items.value!!

    fun loadAllFriends() {
        loadItemsByTab(FriendsTab.FRIENDS)
        loadItemsByTab(FriendsTab.PENDING)
        loadItemsByTab(FriendsTab.REQUESTED)
        loadItemsByTab(FriendsTab.BLOCKED)
    }

    fun loadItemsByTab(tab: FriendsTab) {
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

    fun getItemsByTab(tab: FriendsTab): List<FriendUiEntity> {
        return getItems().filter { it.relationship == tab.relationship }
    }

    enum class Relationship {
        STANDARD,
        PENDING,
        BLOCKED,
        WAITING
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
    val relationship: VmFriends.Relationship
) {
    FRIENDS(0, "FRIENDS", UserFriendsRequestType.FRIENDS, VmFriends.Relationship.STANDARD),
    PENDING(1, "PENDING", UserFriendsRequestType.FRIEND_REQUESTED_BY, VmFriends.Relationship.PENDING),
    REQUESTED(2, "MY REQUESTS", UserFriendsRequestType.FRIEND_REQUESTED, VmFriends.Relationship.WAITING),
    BLOCKED(3, "BLOCKED", UserFriendsRequestType.BLOCKED, VmFriends.Relationship.BLOCKED);

    companion object {
        fun getBy(position: Int): FriendsTab {
            return values().find { it.position == position } ?: throw IllegalArgumentException()
        }
    }
}

@Parcelize
data class FriendUiEntity(
    val id: String,
    val imageUrl: String?,
    val isOnline: Boolean,
    val nickname: String,
    val relationship: VmFriends.Relationship
) : Parcelable

fun UserFriendsResponse.toUiEntities(tab: FriendsTab): List<FriendUiEntity> {
    val list = mutableListOf<FriendUiEntity>()
    this.relationships.forEach {
        val id = it.user.id
        val imageUrl = it.user.picture
        val isOnline = it.presence?.let { presence -> presence == Presence.ONLINE } ?: false
        val nickname = it.user.nickname ?: it.user.name ?: it.user.firstName ?: it.user.lastName ?: "Nickname is empty"
        val relationship = tab.relationship
        list.add(
            FriendUiEntity(
                id,
                imageUrl,
                isOnline,
                nickname,
                relationship
            )
        )
    }
    return list
}
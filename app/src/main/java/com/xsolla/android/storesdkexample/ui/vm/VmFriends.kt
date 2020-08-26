package com.xsolla.android.storesdkexample.ui.vm

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.parcel.Parcelize

class VmFriends : ViewModel() {

    val viewState = MutableLiveData(ViewState.LOADING)
    val items = MutableLiveData<List<FriendUiEntity>>(listOf())
    val tab = MutableLiveData(FriendsTab.FRIENDS)

    fun updateItems(items: List<FriendUiEntity>) {
        this.items.value = items
    }

    fun getItems() = items.value!!

    enum class Relationship(val tab: FriendsTab?) {
        STANDARD(FriendsTab.FRIENDS),
        PENDING(FriendsTab.PENDING),
        BLOCKED(FriendsTab.BLOCKED),
        WAITING(FriendsTab.REQUESTED),
        NONE(null)
    }

    enum class ViewState {
        SUCCESS,
        EMPTY,
        FAILURE,
        LOADING
    }
}

enum class FriendsTab(val position: Int, val title: String) {
    FRIENDS(0, "FRIENDS"),
    PENDING(1, "PENDING"),
    REQUESTED(2, "MY REQUESTS"),
    BLOCKED(3, "BLOCKED");

    companion object {
        fun getBy(position: Int): FriendsTab {
            return values().find { it.position == position } ?: throw IllegalArgumentException()
        }
    }
}

// TODO: rename
@Parcelize
data class FriendUiEntity(
    val id: String,
    val imageUrl: String,
    val isOnline: Boolean,
    val nickname: String,
    val relationship: VmFriends.Relationship
) : Parcelable
package com.xsolla.android.storesdkexample.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.SearchUsersByNicknameCallback
import com.xsolla.android.login.entity.response.SearchUsersByNicknameResponse
import com.xsolla.android.login.entity.response.UserFromSearch
import kotlinx.coroutines.*

class VmAddFriends(application: Application) : AndroidViewModel(application) {

    companion object {
        const val SEARCH_MIN_LENGTH = 3
        const val SEARCH_DELAY = 1000L // Login API has a limit of 1rps for search
        const val REQUEST_OFFSET = 0
        const val REQUEST_LIMIT = 100
    }

    val currentSearchQuery = MutableLiveData("")

    val searchResultList = MutableLiveData<List<UserFromSearch>>(listOf())
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
                searchResultList.value = data.users
                println("!!! ${data.users}") //TODO
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                searchResultList.value = listOf()
                throwable?.printStackTrace()
                println("!!! $errorMessage") // TODO
            }

        })
    }

}
package com.example.myapplication.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.myapplication.paging.PagingSource
import com.example.myapplication.preference.UserModel
import com.example.myapplication.preference.UserPreference
import com.example.myapplication.response.ListStoryItem
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPref: UserPreference
) {

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = {
                Log.d(ContentValues.TAG, "tokenrepository: $token")
                PagingSource(token)
            }
        ).liveData
    }

    suspend fun saveSession(user: UserModel) {
        userPref.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPref.getSession()
    }

    suspend fun logout() {
        userPref.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}
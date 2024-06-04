package com.example.myapplication.repository

import com.example.myapplication.preference.UserModel
import com.example.myapplication.preference.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPref: UserPreference
) {

//    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 20
//            ),
//            pagingSourceFactory = {
//                Log.d(ContentValues.TAG, "tokenrepository: $token")
//                PagingStorySource(token)
//            }
//        ).liveData
//    }

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
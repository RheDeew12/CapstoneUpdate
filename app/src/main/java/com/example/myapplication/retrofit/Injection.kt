package com.example.myapplication.retrofit

import android.content.Context
import com.example.myapplication.preference.UserPreference
import com.example.myapplication.preference.dataStore
import com.example.myapplication.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}
package com.example.myapplication.ui.email

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.myapplication.preference.UserModel
import com.example.myapplication.repository.UserRepository
import com.example.myapplication.response.ListStoryItem

class DiscussViewModel (private val repository: UserRepository) : ViewModel() {
    private val _story = MutableLiveData<List<ListStoryItem>>()
    val story: LiveData<List<ListStoryItem>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> =
        repository.getStories(token).cachedIn(viewModelScope)
}
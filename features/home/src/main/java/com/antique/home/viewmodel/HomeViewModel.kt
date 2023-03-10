package com.antique.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antique.common.ApiStatus
import com.antique.common.Constant
import com.antique.common.SingleEvent
import com.antique.home.data.PostUiState
import com.antique.home.usecase.GetCategoriesUseCase
import com.antique.home.usecase.GetInitPostsUseCase
import com.antique.home.usecase.GetMorePostsUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getInitPostsUseCase: GetInitPostsUseCase,
    private val getMorePostsUseCase: GetMorePostsUseCase
) : ViewModel() {
    private var index: String = Constant.UNINITIALIZED_VALUE

    private val _categories = MutableLiveData<ApiStatus<List<String>>>()
    val categories: LiveData<ApiStatus<List<String>>> get() = _categories

    private val _category = MutableLiveData(Constant.DEFAULT_CATEGORY)
    val category: LiveData<String> get() = _category

    private val _isRefresh = MutableLiveData<SingleEvent<Boolean>>()
    val isRefresh: LiveData<SingleEvent<Boolean>> get() = _isRefresh

    private val _isLoading = MutableLiveData<SingleEvent<Boolean>>()
    val isLoading: LiveData<SingleEvent<Boolean>> get() = _isLoading

    private val _posts = MutableLiveData<ApiStatus<List<PostUiState>>>()
    val posts: LiveData<ApiStatus<List<PostUiState>>> get() = _posts


    fun initialize() {
        viewModelScope.launch {
            try {
                if(categories.value == null) {
                    val response = getCategoriesUseCase()
                    _categories.value = ApiStatus.Success(response)
                    getInitPosts()
                }
            } catch (e: Exception) {
                _categories.value = ApiStatus.Error(e)
            }
        }
    }


    fun getNewCategoryPosts(category: String) {
        _category.value = category
        getPosts()
    }

    private fun getInitPosts() {
        viewModelScope.launch {
            try {
                _isLoading.value = SingleEvent(true)
                if(posts.value == null) {
                   category.value?.let { category ->
                       val response = getInitPostsUseCase(category)
                       if(response.isNotEmpty()) {
                           index = response.last().postId
                           _posts.value = ApiStatus.Success(response)
                       } else {
                           index = Constant.UNINITIALIZED_VALUE
                           _posts.value = ApiStatus.Success(response)
                       }
                   }
                }
            } catch (e: Exception) {
                _posts.value = ApiStatus.Error(e)
            } finally {
                _isLoading.value = SingleEvent(false)
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            try {
                category.value?.let { category ->
                    val response = getInitPostsUseCase(category)
                    if(response.isNotEmpty()) {
                        index = response.last().postId
                        _posts.value = ApiStatus.Success(response)
                    } else {
                        index = Constant.UNINITIALIZED_VALUE
                        _posts.value = ApiStatus.Success(response)
                    }

                    _isRefresh.value = SingleEvent(false)
                }
            } catch (e: Exception) {
                _posts.value = ApiStatus.Error(e)
            }
        }
    }

    private fun getPosts() {
        viewModelScope.launch {
            try {
                _isLoading.value = SingleEvent(true)
                category.value?.let { category ->
                    val response = getInitPostsUseCase(category)
                    if(response.isNotEmpty()) {
                        index = response.last().postId
                        _posts.value = ApiStatus.Success(response)
                    } else {
                        index = Constant.UNINITIALIZED_VALUE
                        _posts.value = ApiStatus.Success(response)
                    }
                }
            } catch (e: Exception) {
                _posts.value = ApiStatus.Error(e)
            } finally {
                _isLoading.value = SingleEvent(false)
            }
        }
    }

    fun getMorePosts() {
        viewModelScope.launch {
            try {
                _isLoading.value = SingleEvent(true)
                category.value?.let { category ->
                    val response = getMorePostsUseCase(category, index)
                    if(response.isNotEmpty()) {
                        index = response.last().postId

                        posts.value?.let {
                            when(it) {
                                is ApiStatus.Success -> {
                                    val newList = it.items.toMutableList()
                                    newList.addAll(response)
                                    _posts.value = ApiStatus.Success(newList)
                                }
                                is ApiStatus.Error -> {
                                    _posts.value = ApiStatus.Success(response)
                                }
                                is ApiStatus.Loading -> {}
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _posts.value = ApiStatus.Error(e)
            } finally {
                _isLoading.value = SingleEvent(false)
            }
        }
    }
}
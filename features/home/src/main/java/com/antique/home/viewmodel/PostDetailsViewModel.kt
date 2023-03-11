package com.antique.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antique.common.ApiStatus
import com.antique.home.data.PostDetailsUiState
import com.antique.home.usecase.GetPostDetailsUseCase
import kotlinx.coroutines.launch

class PostDetailsViewModel(
    private val getPostDetailsUseCase: GetPostDetailsUseCase
) : ViewModel() {
    private val _postDetails = MutableLiveData<ApiStatus<PostDetailsUiState>>()
    val postDetails: MutableLiveData<ApiStatus<PostDetailsUiState>> get() = _postDetails

    fun getPostDetails(postId: String, category: String) {
        viewModelScope.launch {
            try {
                val response = getPostDetailsUseCase(postId, category)
                _postDetails.value = ApiStatus.Success(response)
            } catch (e: Exception) {
                _postDetails.value = ApiStatus.Error(e)
            }
        }
    }
}
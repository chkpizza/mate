package com.antique.post.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antique.common.ApiStatus
import com.antique.common.SingleEvent
import com.antique.post.usecase.GetCategoriesUseCase
import com.antique.post.usecase.RegisterPostUseCase
import kotlinx.coroutines.launch

class PostViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val registerPostUseCase: RegisterPostUseCase
) : ViewModel() {
    private val _categories = MutableLiveData<ApiStatus<List<String>>>()
    val categories: LiveData<ApiStatus<List<String>>> get() = _categories

    private val _selectedCategory = MutableLiveData<String>("카테고리를 선택해주세요")
    val selectedCategory: LiveData<String> get() = _selectedCategory

    private val _details = MutableLiveData<String>()
    val details: LiveData<String> get() = _details

    private val _selectedImages = MutableLiveData<List<String>>()
    val selectedImages: LiveData<List<String>> get() = _selectedImages

    private val _registerState = MutableLiveData<SingleEvent<ApiStatus<Boolean>>>()
    val registerState: LiveData<SingleEvent<ApiStatus<Boolean>>> get() = _registerState

    fun getCategories() {
        viewModelScope.launch {
            try {
                val response = getCategoriesUseCase()
                _categories.value = ApiStatus.Success(response)
            } catch (e: Exception) {
                _categories.value = ApiStatus.Error(e)
            }
        }
    }

    fun bindCategory(category: String) {
        _selectedCategory.value = category
    }

    fun bindDetails(details: String) {
        _details.value = details
    }

    fun bindImages(uris: List<String>) {
        _selectedImages.value = uris
    }

    fun registerPost() {
        viewModelScope.launch {
            try {
                if(!details.value.isNullOrEmpty() && !selectedCategory.value.isNullOrEmpty()) {
                    _registerState.value = SingleEvent(ApiStatus.Loading)

                    val details = details.value ?: ""
                    val category = selectedCategory.value ?: ""
                    val uris = selectedImages.value?.let {
                        it.ifEmpty { emptyList() }
                    } ?: emptyList()

                    val response = registerPostUseCase(details, category, uris)
                    _registerState.value = SingleEvent(ApiStatus.Success(response))
                }
            } catch (e: Exception) {
                _registerState.value = SingleEvent(ApiStatus.Error(e))
            }
        }
    }

    fun clear() {
        _details.value = ""
        _selectedCategory.value = "카테고리를 선택해주세요"
        _selectedImages.value = emptyList()
    }
}
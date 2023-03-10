package com.antique.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antique.common.ApiStatus
import com.antique.home.usecase.GetCategoriesUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {
    private val _categories = MutableLiveData<ApiStatus<List<String>>>()
    val categories: LiveData<ApiStatus<List<String>>> get() = _categories

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> get() = _category

    fun getCategories() {
        viewModelScope.launch {
            try {
                if(categories.value == null) {
                    val response = getCategoriesUseCase()
                    _categories.value = ApiStatus.Success(response)
                }
            } catch (e: Exception) {
                _categories.value = ApiStatus.Error(e)
            }
        }
    }

    fun initCategory(defaultCategory: String) {
        if(category.value == null) {
            _category.value = defaultCategory
        }
    }

    fun updateCategory(category: String) {
        _category.value = category
    }
}
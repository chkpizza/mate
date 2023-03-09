package com.antique.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _category = MutableLiveData("일상")
    val category: LiveData<String> get() = _category

    fun updateCategory(category: String) {
        _category.value = category
    }
}
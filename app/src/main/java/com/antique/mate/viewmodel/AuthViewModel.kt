package com.antique.mate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antique.mate.usecase.RegisterUserUseCase
import kotlinx.coroutines.launch

class AuthViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {
    private val _registerState = MutableLiveData<Boolean>()
    val registerState: LiveData<Boolean> get() = _registerState

    fun registerUser() {
        viewModelScope.launch {
            val response = registerUserUseCase()
            _registerState.value = response
        }
    }
}
package com.antique.mate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antique.mate.repo.AuthRepositoryImpl
import com.antique.mate.usecase.RegisterUserUseCase
import kotlinx.coroutines.Dispatchers

class AuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val authRepository = AuthRepositoryImpl(Dispatchers.IO)
            return AuthViewModel(
                RegisterUserUseCase(authRepository)
            ) as T
        }
        throw IllegalArgumentException("unknown view model")
    }
}
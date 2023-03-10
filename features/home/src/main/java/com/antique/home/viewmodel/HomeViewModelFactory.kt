package com.antique.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antique.home.repo.HomeRepositoryImpl
import com.antique.home.usecase.GetCategoriesUseCase
import com.antique.home.usecase.GetInitPostsUseCase
import com.antique.home.usecase.GetMorePostsUseCase
import kotlinx.coroutines.Dispatchers

class HomeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val homeRepository = HomeRepositoryImpl(Dispatchers.IO)
            return HomeViewModel(
                GetCategoriesUseCase(homeRepository),
                GetInitPostsUseCase(homeRepository),
                GetMorePostsUseCase(homeRepository)
            ) as T
        }
        throw IllegalArgumentException("unknown view model")
    }
}
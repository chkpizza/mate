package com.antique.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antique.home.repo.PostDetailsRepositoryImpl
import com.antique.home.usecase.GetPostDetailsUseCase
import kotlinx.coroutines.Dispatchers

class PostDetailsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PostDetailsViewModel::class.java)) {
            val postDetailsRepository = PostDetailsRepositoryImpl(Dispatchers.IO)
            return PostDetailsViewModel(
                GetPostDetailsUseCase(postDetailsRepository)
            ) as T
        }
        throw IllegalArgumentException("unknown view model")
    }
}
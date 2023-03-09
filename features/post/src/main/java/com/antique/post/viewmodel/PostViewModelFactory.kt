package com.antique.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antique.post.repo.PostRepositoryImpl
import com.antique.post.usecase.GetCategoriesUseCase
import com.antique.post.usecase.RegisterPostUseCase
import kotlinx.coroutines.Dispatchers

class PostViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PostViewModel::class.java)) {
            val postRepository = PostRepositoryImpl(Dispatchers.IO)

            return PostViewModel(
                GetCategoriesUseCase(postRepository),
                RegisterPostUseCase(postRepository)
            ) as T
        }

        throw IllegalArgumentException("unknown view model")
    }
}
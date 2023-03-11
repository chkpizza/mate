package com.antique.home.usecase

import com.antique.common.Post
import com.antique.home.data.PostDetailsUiState
import com.antique.home.repo.PostDetailsRepository

class ReportPostUseCase(private val postDetailsRepository: PostDetailsRepository) {
    suspend operator fun invoke(post: PostDetailsUiState): Boolean = postDetailsRepository.reportPost(mapper(post))
    
    private suspend fun mapper(post: PostDetailsUiState): Post {
        return Post(post.author.uid, post.details, post.images, post.category, post.date, post.postId)
    }
}
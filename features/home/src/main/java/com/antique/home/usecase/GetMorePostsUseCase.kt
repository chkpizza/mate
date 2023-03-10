package com.antique.home.usecase

import com.antique.common.Post
import com.antique.home.data.PostUiState
import com.antique.home.repo.HomeRepository

class GetMorePostsUseCase(private val homeRepository: HomeRepository) {
    suspend operator fun invoke(category: String, index: String): List<PostUiState> {
        val posts = homeRepository.getMorePosts(category, index).reversed()

        return mapper(posts)
    }

    private suspend fun mapper(posts: List<Post>): List<PostUiState> {
        val uiStates = mutableListOf<PostUiState>()

        posts.forEach { post ->
            val commentCount = homeRepository.getCommentCount(post)
            val uiState = PostUiState(post.author, post.details, post.images, post.category, post.date, commentCount, post.postId)
            uiStates.add(uiState)
        }

        return uiStates.toList()
    }
}
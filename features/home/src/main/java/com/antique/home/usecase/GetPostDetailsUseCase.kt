package com.antique.home.usecase

import com.antique.common.PostDetails
import com.antique.common.PostOverview
import com.antique.home.data.CommentUiState
import com.antique.home.data.PostDetailsUiState
import com.antique.home.data.PostUiState
import com.antique.home.repo.PostDetailsRepository

class GetPostDetailsUseCase(private val postDetailsRepository: PostDetailsRepository) {
    suspend operator fun invoke(postId: String, category: String): PostDetailsUiState {
        val response = postDetailsRepository.getPostDetails(postId, category)
        return mapper(response)
    }

    private suspend fun mapper(postDetails: PostDetails): PostDetailsUiState {
        val postAuthor = postDetailsRepository.getAuthor(postDetails.author)

        val commentUiStates = mutableListOf<CommentUiState>()
        postDetails.comments.forEach {
            val commentAuthor = postDetailsRepository.getAuthor(it.author)
            commentUiStates.add(
                CommentUiState(
                    PostOverview(postDetails.author, postDetails.postId, postDetails.category),
                    commentAuthor,
                    it.details,
                    it.date,
                    it.commentId
                )
            )
        }

        return PostDetailsUiState(postAuthor, postDetails.details, postDetails.images, postDetails.category, postDetails.date, postDetails.postId, commentUiStates)
    }
}
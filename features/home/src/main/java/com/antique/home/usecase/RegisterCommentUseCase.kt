package com.antique.home.usecase

import com.antique.common.Comment
import com.antique.common.PostOverview
import com.antique.home.data.CommentUiState
import com.antique.home.repo.PostDetailsRepository

class RegisterCommentUseCase(private val postDetailsRepository: PostDetailsRepository) {
    suspend operator fun invoke(postOverview: PostOverview, details: String): CommentUiState {
        val response = postDetailsRepository.registerComment(postOverview, details)

        return mapper(response)
    }

    private suspend fun mapper(comment: Comment): CommentUiState {
        val author = postDetailsRepository.getAuthor(comment.author)

        return CommentUiState(comment.postOverview, author, comment.details, comment.date, comment.commentId)
    }
}
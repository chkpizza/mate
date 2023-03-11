package com.antique.home.usecase

import com.antique.common.Comment
import com.antique.home.data.CommentUiState
import com.antique.home.repo.PostDetailsRepository

class RemoveCommentUseCase(private val postDetailsRepository: PostDetailsRepository) {
    suspend operator fun invoke(comment: CommentUiState) {
        postDetailsRepository.removeComment(mapper(comment))
    }

    private fun mapper(comment: CommentUiState): Comment {
        return Comment(comment.postOverview, comment.author.uid, comment.details, comment.date, comment.commentId)
    }
}
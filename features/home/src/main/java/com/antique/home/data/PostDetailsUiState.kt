package com.antique.home.data

import androidx.annotation.Keep
import com.antique.common.User

@Keep
data class PostDetailsUiState(
    val author: User,
    val details: String,
    val images: List<String>,
    val category: String,
    val date: String,
    val postId: String,
    val comments: List<CommentUiState>
) {
    constructor() : this(User(), "", emptyList(), "", "", "", emptyList())
}

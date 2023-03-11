package com.antique.home.data

import androidx.annotation.Keep
import com.antique.common.PostOverview
import com.antique.common.User

@Keep
data class CommentUiState(
    val postOverview: PostOverview,
    val author: User,
    val details: String,
    val date: String,
    val commentId: String
) {
    constructor() : this(PostOverview(), User(), "", "", "")
}
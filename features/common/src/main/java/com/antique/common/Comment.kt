package com.antique.common

import androidx.annotation.Keep

@Keep
data class Comment(
    val postOverview: PostOverview,
    val author: String,
    val details: String,
    val date: String,
    val commentId: String
) {
    constructor() : this(PostOverview(), "", "", "", "")
}
package com.antique.common

import androidx.annotation.Keep

@Keep
data class PostDetails(
    val author: String,
    val details: String,
    val images: List<String>,
    val category: String,
    val date: String,
    val postId: String,
    val comments: List<Comment>
) {
    constructor() : this("", "", emptyList(), "", "", "", emptyList())
}
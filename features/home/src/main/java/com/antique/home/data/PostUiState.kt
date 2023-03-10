package com.antique.home.data

import androidx.annotation.Keep

@Keep
data class PostUiState(
    val author: String,
    val details: String,
    val images: List<String>,
    val category: String,
    val date: String,
    val commentCount: Long,
    val postId: String,
) {
    constructor() : this("", "", emptyList(), "", "", 0, "")
}
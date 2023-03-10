package com.antique.common

import androidx.annotation.Keep

@Keep
data class Post(
    val author: String,
    val details: String,
    val images: List<String>,
    val category: String,
    val date: String,
    val postId: String,
) {
    constructor() : this("", "", emptyList(), "", "", "")
}
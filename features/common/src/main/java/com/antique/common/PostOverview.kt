package com.antique.common

import androidx.annotation.Keep

@Keep
data class PostOverview(
    val author: String,
    val postId: String,
    val category: String
) {
    constructor() : this("", "", "")
}

package com.antique.home.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PostUiState(
    val author: String,
    val details: String,
    val images: List<String>,
    val category: String,
    val date: String,
    val commentCount: Long,
    val postId: String,
) : Parcelable {
    constructor() : this("", "", emptyList(), "", "", 0, "")
}
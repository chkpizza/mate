package com.antique.home.repo

import com.antique.common.PostDetails
import com.antique.common.User

interface PostDetailsRepository {
    suspend fun getPostDetails(postId: String, category: String): PostDetails
    suspend fun getAuthor(uid: String): User
}
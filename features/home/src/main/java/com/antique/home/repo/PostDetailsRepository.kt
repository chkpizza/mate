package com.antique.home.repo

import com.antique.common.*

interface PostDetailsRepository {
    suspend fun getPostDetails(postId: String, category: String): PostDetails
    suspend fun getAuthor(uid: String): User
    suspend fun registerComment(postOverview: PostOverview, details: String): Comment
    suspend fun removeComment(comment: Comment)
    suspend fun removePost(post: Post): Boolean
    suspend fun reportPost(post: Post): Boolean
    suspend fun reportComment(comment: Comment): Boolean
}
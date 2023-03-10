package com.antique.home.repo

import com.antique.common.Post

interface HomeRepository {
    suspend fun getCategories(): List<String>
    suspend fun getInitPosts(category: String): List<Post>
    suspend fun getMorePosts(category: String, _index: String): List<Post>
    suspend fun getCommentCount(post: Post): Long
}
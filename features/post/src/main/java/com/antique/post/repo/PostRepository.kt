package com.antique.post.repo

interface PostRepository {
    suspend fun getCategories(): List<String>
    suspend fun registerPost(details: String, category: String, uris: List<String>): Boolean
}
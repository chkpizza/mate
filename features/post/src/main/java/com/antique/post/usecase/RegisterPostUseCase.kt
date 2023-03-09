package com.antique.post.usecase

import com.antique.post.repo.PostRepository

class RegisterPostUseCase(private val postRepository: PostRepository) {
    suspend operator fun invoke(details: String, category: String, uris: List<String>): Boolean = postRepository.registerPost(details, category, uris)
}
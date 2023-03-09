package com.antique.post.usecase

import com.antique.post.repo.PostRepository

class GetCategoriesUseCase(private val postRepository: PostRepository) {
    suspend operator fun invoke(): List<String> = postRepository.getCategories()
}
package com.antique.home.usecase

import com.antique.home.repo.HomeRepository

class GetCategoriesUseCase(private val homeRepository: HomeRepository) {
    suspend operator fun invoke(): List<String> = homeRepository.getCategories()
}
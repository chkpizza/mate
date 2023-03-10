package com.antique.home.repo

interface HomeRepository {
    suspend fun getCategories(): List<String>
}
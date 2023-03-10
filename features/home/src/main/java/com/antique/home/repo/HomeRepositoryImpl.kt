package com.antique.home.repo

import com.antique.common.Constant
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(private val dispatcher: CoroutineDispatcher) : HomeRepository {
    override suspend fun getCategories(): List<String> = withContext(dispatcher) {
        val response = Firebase.database.reference.child(Constant.CATEGORY_NODE).get().await()
        val categories = mutableListOf<String>()

        response.children.forEach {
            it.getValue(String::class.java)?.let { category ->
                categories.add(category)
            }
        }

        categories.toList()
    }
}
package com.antique.mate.repo

import com.antique.mate.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val dispatcher: CoroutineDispatcher
) : AuthRepository {
    override suspend fun registerUser(user: User): Boolean = withContext(dispatcher) {
        val ref = Firebase.database.reference.child("User").child(user.uid)
        if(isExist(ref)) {
            true
        } else {
            ref.setValue(user).await()
            validate(ref)
        }
    }

    private suspend fun isExist(ref: DatabaseReference): Boolean = ref.get().await().exists()
    private suspend fun validate(ref: DatabaseReference): Boolean = ref.get().await().exists()
}
package com.antique.mate.repo

import com.antique.mate.data.User

interface AuthRepository {
    suspend fun registerUser(user: User): Boolean
}
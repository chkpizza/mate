package com.antique.post

import com.google.errorprone.annotations.Keep

@Keep
sealed class ApiStatus<out T> {
    object Loading : ApiStatus<Nothing>()
    data class Success<T>(val items: T) : ApiStatus<T>()
    data class Error(val exception: Exception) : ApiStatus<Nothing>()
}
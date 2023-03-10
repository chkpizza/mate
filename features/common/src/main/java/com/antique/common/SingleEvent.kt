package com.antique.common

import androidx.lifecycle.Observer

open class SingleEvent<out T>(private val value: T) {
    var hasBeenHandled = false
        private set

    fun getValueIfNotHandled(): T? {
        return if(hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            value
        }
    }
}

class EventObserver<T>(private val onEventUnHandledValue: (T) -> Unit) : Observer<SingleEvent<T>> {
    override fun onChanged(value: SingleEvent<T>) {
        value.getValueIfNotHandled()?.let {
            onEventUnHandledValue(it)
        }
    }
}
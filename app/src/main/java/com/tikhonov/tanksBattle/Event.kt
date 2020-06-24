package com.tikhonov.tanksBattle

import androidx.lifecycle.Observer

class Event<T>(private val content: T? = null) {
    private var hasBeenHandled = false

    val contentIfNotHandled: T?
        get() {
            if (hasBeenHandled) return null
            hasBeenHandled = true
            return content
        }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            content == null -> false
            else -> if (other !is Event<*>) false else content == other.content
        }
    }
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.contentIfNotHandled?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}


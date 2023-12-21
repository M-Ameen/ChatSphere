package com.example.chatapp

open class Event<out T>(val content: T) {
    var hasbeenhandled = false
    fun getContentorNull(): T? {
        return if (hasbeenhandled) null else {
            hasbeenhandled = true
            content
        }
    }
}
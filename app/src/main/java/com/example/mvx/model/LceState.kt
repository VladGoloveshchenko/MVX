package com.example.mvx.model

sealed class LceState<out T> {
    object Loading : LceState<Nothing>()

    data class Content<T>(val items: T) : LceState<T>()

    data class Error(val throwable: Throwable) : LceState<Nothing>()
}
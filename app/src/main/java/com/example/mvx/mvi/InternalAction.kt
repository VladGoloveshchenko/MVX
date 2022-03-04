package com.example.mvx.mvi

import com.example.mvx.model.Entity

sealed class InternalAction : Action {
    object DataLoading : InternalAction()
    data class DataLoaded(val items: List<Entity>) : InternalAction()
    data class DataError(val throwable: Throwable) : InternalAction()
    data class SuggestionsLoaded(val items: List<String>) : InternalAction()
}
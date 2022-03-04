package com.example.mvx.mvi

sealed class UiAction : Action {
    data class QueryConfirmed(val query: String) : UiAction()
    data class QueryChanged(val query: String) : UiAction()
}
package com.example.mvx.mvi

import com.example.mvx.model.Entity

data class UiState(
    val isLoading: Boolean = false,
    val items: List<Entity> = emptyList(),
    val throwable: Throwable? = null,
    val suggestions: List<String> = emptyList()
)
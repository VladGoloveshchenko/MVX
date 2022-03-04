package com.example.mvx.mvi

import kotlinx.coroutines.flow.Flow

interface MviView<A, S> {
    val actions: Flow<A>
    fun render(state: S)
}
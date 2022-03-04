package com.example.mvx.mvi

import androidx.lifecycle.ViewModel
import com.example.mvx.model.DataStore
import kotlinx.coroutines.flow.*

class MVIViewModel : ViewModel() {

    private val dataStore = DataStore()

    private val actionFlow = MutableSharedFlow<UiAction>(extraBufferCapacity = 1)

    val state = merge(
        bindInitial(),
        bindSearch(actionFlow),
        bindSuggestions(actionFlow)
    )
        .scan(UiState()) { state, action ->
            when (action) {
                is InternalAction.DataError -> state.copy(
                    isLoading = false,
                    throwable = action.throwable,
                    suggestions = emptyList()
                )
                is InternalAction.DataLoaded -> state.copy(
                    isLoading = false,
                    items = action.items,
                    suggestions = emptyList()
                )
                is InternalAction.SuggestionsLoaded -> state.copy(
                    suggestions = action.items
                )
                InternalAction.DataLoading -> state.copy(
                    isLoading = true,
                    items = emptyList(),
                    suggestions = emptyList()
                )
            }
        }

    fun handleAction(action: UiAction) {
        actionFlow.tryEmit(action)
    }

    private fun bindSearch(actions: Flow<Action>): Flow<InternalAction> =
        actions
            .filterIsInstance<UiAction.QueryConfirmed>()
            .transformLatest {
                emit(InternalAction.DataLoading)
                val items = dataStore.fetchItems(it.query)
                emit(InternalAction.DataLoaded(items))
            }
            .catch { emit(InternalAction.DataError(it)) }

    private fun bindSuggestions(actions: Flow<Action>): Flow<InternalAction> =
        actions
            .filterIsInstance<UiAction.QueryChanged>()
            .debounce(300)
            .mapLatest { dataStore.fetchSuggestions(it.query) }
            .catch { emit(emptyList()) }
            .map { InternalAction.SuggestionsLoaded(it) }

    private fun bindInitial(): Flow<InternalAction> = flow {
        emit(InternalAction.DataLoading)
        try {
            val items = dataStore.fetchItems()
            emit(InternalAction.DataLoaded(items))
        } catch (e: Throwable) {
            emit(InternalAction.DataError(e))
        }
    }
}
package com.example.mvx.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvx.model.DataStore
import com.example.mvx.model.Entity
import com.example.mvx.model.LceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MVVMViewModel(private val dataStore: DataStore) : ViewModel() {

    // replay = 0 to prevent showing Toast after screen rotation
    private val mutableSharedFlow = MutableSharedFlow<Unit>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sharedFlow: Flow<Unit> = mutableSharedFlow.asSharedFlow()

    private val _lceFlow = MutableStateFlow<LceState<List<Entity>>>(LceState.Loading)
    val lceFlow: Flow<LceState<List<Entity>>> = _lceFlow.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val items = withContext(Dispatchers.IO) { dataStore.fetchItems() }
                _lceFlow.tryEmit(LceState.Content(items))
            } catch (e: Throwable) {
                _lceFlow.tryEmit(LceState.Error(e))
            }
        }
    }

    fun onButtonClicked() {
        mutableSharedFlow.tryEmit(Unit)
    }
}
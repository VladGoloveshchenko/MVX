package com.example.mvx.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvx.model.DataStore
import com.example.mvx.model.Entity
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

    private val _listFlow = MutableStateFlow<List<Entity>>(emptyList())
    val listFlow: Flow<List<Entity>> = _listFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) { dataStore.fetchItems() }
            _listFlow.tryEmit(items)
        }
    }

    fun onButtonClicked() {
        mutableSharedFlow.tryEmit(Unit)
    }
}
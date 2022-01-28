package com.example.mvx.model

import kotlinx.coroutines.delay

class DataStore {

    suspend fun fetchItems(): List<Entity> {
        delay(5000)
        return List(10) {
            Entity("entity $it")
        }
    }
}
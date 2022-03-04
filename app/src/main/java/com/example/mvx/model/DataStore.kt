package com.example.mvx.model

import kotlinx.coroutines.delay

class DataStore {

    suspend fun fetchItems(): List<Entity> {
        delay(5000)
        return List(10) {
            Entity("entity $it")
        }
    }

    suspend fun fetchItems(query: String): List<Entity> {
        delay(5000)
        return List(10) {
            Entity("$query $it")
        }
    }

    suspend fun fetchSuggestions(query: String): List<String> {
        delay(1000)
        return if (query.isNotEmpty()) {
            List(3) { "Suggestion ${it + 1}: $query" }
        } else {
            emptyList()
        }
    }
}
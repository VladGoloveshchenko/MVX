package com.example.mvx.model

object ServiceProvider {

    private val dataStore by lazy { DataStore() }

    fun provideDataStore(): DataStore {
        return dataStore
    }
}
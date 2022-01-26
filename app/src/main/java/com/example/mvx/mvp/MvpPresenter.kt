package com.example.mvx.mvp

import com.example.mvx.model.DataStore
import kotlinx.coroutines.*

class MvpPresenter(private val dataStore: DataStore) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var view: MvpView? = null

    init {
        println()
    }

    fun attachView(view: MvpView) {
        this.view = view
    }

    fun detachView() {
        viewScope.coroutineContext.cancelChildren()
        view = null
    }

    fun destroy() {
        viewScope.cancel()
        scope.cancel()
    }

    fun onButtonClicked() {
        view?.showToast()
        updateUI()
    }

    private fun updateUI() {
        viewScope.launch {
            val items = withContext(Dispatchers.IO) { dataStore.fetchItems() }
            runIfAttached {
                showItems(items)
            }
        }
    }

    private fun runIfAttached(action: MvpView.() -> Unit) {
        view?.let(action)
    }
}
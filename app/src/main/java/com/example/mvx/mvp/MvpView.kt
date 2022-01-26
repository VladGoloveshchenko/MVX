package com.example.mvx.mvp

import com.example.mvx.model.Entity

interface MvpView {

    fun showToast()

    fun showItems(items: List<Entity>)
}
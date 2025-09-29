package com.github.purofle.sandauschool

import android.app.Application
import android.content.Context

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: MyApp

        val context: Context
            get() = instance.applicationContext
    }
}
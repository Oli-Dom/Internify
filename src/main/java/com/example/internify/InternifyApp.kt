package com.example.internify

import android.app.Application
import com.example.internify.data.AppContainer
import com.example.internify.data.DefaultAppDataContainer

class InternifyApp: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppDataContainer(this)

    }
}
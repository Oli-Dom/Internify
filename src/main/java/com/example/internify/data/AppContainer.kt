package com.example.internify.data

import android.content.Context

interface AppContainer{
    val appRepository: AppRepository
}

class DefaultAppDataContainer(private val context: Context): AppContainer{
    override val appRepository: AppRepository by lazy {
        InternshipRepositoryImpl(
            InternshipDatabase.getDatabase(context).internifyDao()
        )
    }
}
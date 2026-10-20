package ru.maxgog.pagebook

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PageBookApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @Volatile private var instance: PageBookApplication? = null

        fun getApplication(): PageBookApplication =
            instance ?: throw IllegalStateException("Application not initialized")
    }
}
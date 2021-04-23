package com.example.xplore

import android.app.Application
import com.example.xplore.BuildConfig.MAPS_API_KEY
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class XploreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, MAPS_API_KEY)
    }
}
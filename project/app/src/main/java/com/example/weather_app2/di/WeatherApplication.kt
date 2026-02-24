package com.example.weather_app2.di

import android.app.Application
import com.example.weather_app2.utils.CrashLogManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashLogManager.install(this)
    }
}

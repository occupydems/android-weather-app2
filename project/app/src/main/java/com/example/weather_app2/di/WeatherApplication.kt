package com.example.weather_app2.di

import android.app.Application
import com.example.weather_app2.utils.CrashLogManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApplication : Application() {
    override fun onCreate() {
        CrashLogManager.install(this)
        val processName = if (android.os.Build.VERSION.SDK_INT >= 28) {
            getProcessName()
        } else {
            val pid = android.os.Process.myPid()
            val am = getSystemService(ACTIVITY_SERVICE) as? android.app.ActivityManager
            am?.runningAppProcesses?.firstOrNull { it.pid == pid }?.processName
        }
        if (processName != null && processName.contains(":")) {
            return
        }
        super.onCreate()
        com.example.weather_app2.utils.AdManager.initialize(this)
    }
}

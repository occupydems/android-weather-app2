package com.example.weather_app2.notifications

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.weather_app2.database.dao.CityShortcutDao
import com.example.weather_app2.webservices.OpenMeteoService

class WeatherNotificationWorkerFactory(
    private val cityShortcutDao: CityShortcutDao,
    private val openMeteoService: OpenMeteoService
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            WeatherNotificationWorker::class.java.name -> {
                WeatherNotificationWorker(
                    appContext,
                    workerParameters,
                    cityShortcutDao,
                    openMeteoService
                )
            }
            else -> null
        }
    }
}

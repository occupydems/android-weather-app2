package com.example.weather_app2.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather_app2.database.dao.CityShortcutDao
import com.example.weather_app2.utils.WeatherCodeMapper
import com.example.weather_app2.webservices.OpenMeteoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherNotificationWorker(
    context: Context,
    params: WorkerParameters,
    private val cityShortcutDao: CityShortcutDao,
    private val openMeteoService: OpenMeteoService
) : CoroutineWorker(context, params) {

    private val severityWeatherCodes = setOf(
        95, 96, 99,
        65, 82,
        75, 86,
        77
    )

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val cities = cityShortcutDao.getAllCityShortcutsSync()

            if (cities.isEmpty()) {
                return@withContext Result.success()
            }

            for (city in cities) {
                try {
                    val response = openMeteoService.getForecast(
                        lat = city.latitude,
                        lon = city.longitude,
                        current = "temperature_2m,weather_code"
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.let { forecastResponse ->
                            val weatherCode = forecastResponse.current?.weather_code
                            val temperature = forecastResponse.current?.temperature_2m?.toInt()

                            if (weatherCode != null && weatherCode in severityWeatherCodes) {
                                val condition = WeatherCodeMapper.getDescription(weatherCode)
                                NotificationHelper.showWeatherAlert(
                                    applicationContext,
                                    city.cityName,
                                    condition,
                                    temperature ?: 0
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}

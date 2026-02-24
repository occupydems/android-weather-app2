package com.example.weather_app2.di

import android.content.Context
import androidx.work.WorkerFactory
import com.example.weather_app2.database.dao.CityShortcutDao
import com.example.weather_app2.notifications.WeatherNotificationWorkerFactory
import com.example.weather_app2.webservices.OpenMeteoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Singleton
    @Provides
    fun provideWorkerFactory(
        cityShortcutDao: CityShortcutDao,
        openMeteoService: OpenMeteoService
    ): WorkerFactory = WeatherNotificationWorkerFactory(cityShortcutDao, openMeteoService)
}

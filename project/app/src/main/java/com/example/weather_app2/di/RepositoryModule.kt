package com.example.weather_app2.di

import android.content.SharedPreferences
import com.example.weather_app2.database.dao.CityShortcutDao
import com.example.weather_app2.repository.RepositoryImpl
import com.example.weather_app2.webservices.OpenMeteoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        webservice: OpenMeteoService,
        cityShortcutDao: CityShortcutDao,
        unitOfMeasurementSP: SharedPreferences
    ): RepositoryImpl = RepositoryImpl(
        webservice,
        cityShortcutDao,
        unitOfMeasurementSP
    )
}

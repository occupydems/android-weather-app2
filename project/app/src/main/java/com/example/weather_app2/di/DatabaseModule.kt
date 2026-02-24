package com.example.weather_app2.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weather_app2.database.dao.CityShortcutDao
import com.example.weather_app2.database.CityShortcutDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE cities_shortcuts ADD COLUMN latitude REAL NOT NULL DEFAULT 0.0")
            database.execSQL("ALTER TABLE cities_shortcuts ADD COLUMN longitude REAL NOT NULL DEFAULT 0.0")
        }
    }

    @Singleton
    @Provides
    fun provideCityShortcutDao(
        cityShortcutDatabase: CityShortcutDatabase
    ): CityShortcutDao = cityShortcutDatabase.cityShortcutDao()

    @Singleton
    @Provides
    fun provideCityShortcutDatabase(
        applicationContext: WeatherApplication
    ): CityShortcutDatabase = Room.databaseBuilder(
        applicationContext,
        CityShortcutDatabase::class.java,
        "cities_shortcuts",
    )
        .addMigrations(MIGRATION_3_4)
        .fallbackToDestructiveMigration()
        .build()
}

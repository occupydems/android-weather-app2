package com.example.weather_app2.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_app2.database.dao.CityShortcutDao
import com.example.weather_app2.database.entities.CityShortcut

@Database(
    entities = [CityShortcut::class],
    version = 4,
    exportSchema = false
)
abstract class  CityShortcutDatabase : RoomDatabase() {

    abstract fun cityShortcutDao(): CityShortcutDao
}

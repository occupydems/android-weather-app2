package com.example.weather_app2.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities_shortcuts")
data class CityShortcut(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val cityName: String,
    var localTime: String,
    var temp: Int,
    var icon: String,
    var highTemp: Int = 0,
    var lowTemp: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

package com.example.weather_app2.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weather_app2.database.entities.CityShortcut

@Dao
interface CityShortcutDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCityShortcut(cityShortcut: CityShortcut)

    @Delete
    suspend fun deleteCityShortcut(cityShortcut: CityShortcut)

    @Update
    suspend fun updateCityShortcut(cityShortcut: CityShortcut)

    @Query("SELECT * FROM cities_shortcuts ORDER BY id")
    fun getAllCityShortcuts(): LiveData<List<CityShortcut>>

    @Query("SELECT * FROM cities_shortcuts ORDER BY id")
    suspend fun getAllCityShortcutsSync(): List<CityShortcut>

    @Query("SELECT COUNT(*) FROM cities_shortcuts WHERE LOWER(cityName) = LOWER(:name)")
    suspend fun countByName(name: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCityShortcutReturnId(cityShortcut: CityShortcut): Long
}

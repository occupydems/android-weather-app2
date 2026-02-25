package com.example.weather_app2.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather_app2.models.UnitOfMeasurement
import com.example.weather_app2.database.dao.CityShortcutDao
import com.example.weather_app2.database.entities.CityShortcut
import com.example.weather_app2.utils.WeatherCodeMapper
import com.example.weather_app2.webservices.OpenMeteoService
import com.example.weather_app2.webservices.entities.currentweatherdata.*
import com.example.weather_app2.webservices.entities.weatherforecastdata.*
import com.example.weather_app2.webservices.entities.openmeteo.OpenMeteoForecastResponse
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class RepositoryImpl(
    private val webservice: OpenMeteoService,
    private val cityShortcutDao: CityShortcutDao,
    unitOfMeasurementSP: SharedPreferences
) : Repository {

    var deviceTimezone = TimeZone.getDefault().rawOffset / 1000
    var isTimezoneSet = false

    val deviceLocation: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val unitOfMeasurement: MutableLiveData<String> by lazy {
        MutableLiveData<String>(UnitOfMeasurement.IMPERIAL.value)
    }

    val mainForecastLocation: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var mainForecastLat: Double = 0.0
    var mainForecastLon: Double = 0.0

    val allCityShortcutList: LiveData<List<CityShortcut>> by lazy {
        cityShortcutDao.getAllCityShortcuts()
    }

    init {
        unitOfMeasurement.value = unitOfMeasurementSP.getString(
            "unitOfMeasurement",
            UnitOfMeasurement.IMPERIAL.value
        )
    }

    private fun getTemperatureUnit(unitsSystem: String): String {
        return if (unitsSystem == UnitOfMeasurement.METRIC.value) "celsius" else "fahrenheit"
    }

    private fun getWindSpeedUnit(unitsSystem: String): String {
        return if (unitsSystem == UnitOfMeasurement.METRIC.value) "kmh" else "mph"
    }

    private fun getPrecipitationUnit(unitsSystem: String): String {
        return if (unitsSystem == UnitOfMeasurement.METRIC.value) "mm" else "inch"
    }

    private fun parseIsoToUnix(isoTime: String, utcOffsetSeconds: Int): Int {
        return try {
            val format = if (isoTime.length > 13) "yyyy-MM-dd'T'HH:mm" else "yyyy-MM-dd"
            val sdf = SimpleDateFormat(format, Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(isoTime) ?: return 0
            ((date.time / 1000) - utcOffsetSeconds).toInt()
        } catch (e: Exception) {
            0
        }
    }

    private fun convertForecastToCurrentWeather(
        forecast: OpenMeteoForecastResponse,
        cityName: String,
        countryCode: String
    ): CurrentWeatherDataResponse {
        val current = forecast.current!!
        val isDay = (current.is_day ?: 1) == 1
        val wmoCode = current.weather_code

        val daily = forecast.daily
        val tempMax = daily?.temperature_2m_max?.getOrNull(0) ?: current.temperature_2m
        val tempMin = daily?.temperature_2m_min?.getOrNull(0) ?: current.temperature_2m

        val dtUnix = parseIsoToUnix(current.time, forecast.utc_offset_seconds)

        var sunriseUnix = 0
        var sunsetUnix = 0
        if (daily?.sunrise != null && daily.sunrise.isNotEmpty()) {
            sunriseUnix = parseIsoToUnix(daily.sunrise[0], forecast.utc_offset_seconds)
        }
        if (daily?.sunset != null && daily.sunset.isNotEmpty()) {
            sunsetUnix = parseIsoToUnix(daily.sunset[0], forecast.utc_offset_seconds)
        }

        val iconCode = WeatherCodeMapper.getIconCode(wmoCode, isDay, dtUnix, sunriseUnix, sunsetUnix)
        val description = WeatherCodeMapper.getDescription(wmoCode)
        val mainDesc = WeatherCodeMapper.getMainDescription(wmoCode)

        return CurrentWeatherDataResponse(
            base = "open-meteo",
            clouds = Clouds(0),
            cod = 200,
            coord = Coord(forecast.latitude, forecast.longitude),
            dt = dtUnix,
            id = 0,
            main = Main(
                feels_like = current.apparent_temperature ?: current.temperature_2m,
                humidity = current.relative_humidity_2m ?: 0,
                pressure = current.surface_pressure?.toInt() ?: 0,
                temp = current.temperature_2m,
                temp_max = tempMax,
                temp_min = tempMin
            ),
            name = cityName,
            sys = Sys(
                country = countryCode,
                id = 0,
                sunrise = sunriseUnix,
                sunset = sunsetUnix,
                type = 0
            ),
            timezone = forecast.utc_offset_seconds,
            visibility = (current.visibility ?: 10000.0).toInt(),
            weather = listOf(
                Weather(
                    description = description,
                    icon = iconCode,
                    id = wmoCode,
                    main = mainDesc
                )
            ),
            wind = Wind(
                deg = current.wind_direction_10m ?: 0,
                speed = current.wind_speed_10m ?: 0.0,
                gust = current.wind_gusts_10m ?: 0.0
            ),
            uv_index = current.uv_index ?: 0.0
        )
    }

    private fun convertForecastToWeatherForecast(
        forecast: OpenMeteoForecastResponse
    ): WeatherForecastDataResponse {
        val hourlyData = mutableListOf<Hourly>()
        if (forecast.hourly != null) {
            val times = forecast.hourly.time
            val temps = forecast.hourly.temperature_2m
            val codes = forecast.hourly.weather_code
            val isDayList = forecast.hourly.is_day
            for (i in times.indices) {
                val isDay = isDayList?.getOrNull(i)?.let { it == 1 } ?: true
                val iconCode = WeatherCodeMapper.getIconCode(codes[i], isDay)
                val desc = WeatherCodeMapper.getDescription(codes[i])
                val mainDesc = WeatherCodeMapper.getMainDescription(codes[i])
                hourlyData.add(
                    Hourly(
                        clouds = 0,
                        dew_point = 0.0,
                        dt = parseIsoToUnix(times[i], forecast.utc_offset_seconds),
                        feels_like = temps[i],
                        humidity = 0,
                        pop = 0.0,
                        pressure = 0,
                        rain = Rain(0.0),
                        temp = temps[i],
                        uvi = 0.0,
                        visibility = 10000,
                        weather = listOf(WeatherXX(desc, iconCode, codes[i], mainDesc)),
                        wind_deg = 0,
                        wind_gust = 0.0,
                        wind_speed = 0.0
                    )
                )
            }
        }

        val dailyData = mutableListOf<Daily>()
        if (forecast.daily != null) {
            val times = forecast.daily.time
            val codes = forecast.daily.weather_code
            val maxTemps = forecast.daily.temperature_2m_max
            val minTemps = forecast.daily.temperature_2m_min
            val sunrises = forecast.daily.sunrise
            val sunsets = forecast.daily.sunset
            for (i in times.indices) {
                val iconCode = WeatherCodeMapper.getIconCode(codes[i], true)
                val desc = WeatherCodeMapper.getDescription(codes[i])
                val mainDesc = WeatherCodeMapper.getMainDescription(codes[i])
                val sunriseUnix = sunrises?.getOrNull(i)?.let {
                    parseIsoToUnix(it, forecast.utc_offset_seconds)
                } ?: 0
                val sunsetUnix = sunsets?.getOrNull(i)?.let {
                    parseIsoToUnix(it, forecast.utc_offset_seconds)
                } ?: 0
                dailyData.add(
                    Daily(
                        clouds = 0,
                        dew_point = 0.0,
                        dt = parseIsoToUnix(times[i], forecast.utc_offset_seconds),
                        feels_like = FeelsLike(0.0, 0.0, 0.0, 0.0),
                        humidity = 0,
                        moon_phase = 0.0,
                        moonrise = 0,
                        moonset = 0,
                        pop = 0.0,
                        pressure = 0,
                        rain = 0.0,
                        sunrise = sunriseUnix,
                        sunset = sunsetUnix,
                        temp = Temp(0.0, 0.0, maxTemps[i], minTemps[i], 0.0, 0.0),
                        uvi = 0.0,
                        weather = listOf(WeatherX(desc, iconCode, codes[i], mainDesc)),
                        wind_deg = 0,
                        wind_gust = 0.0,
                        wind_speed = 0.0
                    )
                )
            }
        }

        return WeatherForecastDataResponse(
            daily = dailyData,
            hourly = hourlyData,
            lat = forecast.latitude,
            lon = forecast.longitude,
            timezone = forecast.timezone,
            timezone_offset = forecast.utc_offset_seconds
        )
    }

    override suspend fun getCurrentWeatherByCoords(
        lat: Double,
        lon: Double,
        cityName: String,
        countryCode: String,
        unitsSystem: String
    ): Response<CurrentWeatherDataResponse> {
        try {
            val forecastResponse = webservice.getForecast(
                lat = lat,
                lon = lon,
                tempUnit = getTemperatureUnit(unitsSystem),
                windUnit = getWindSpeedUnit(unitsSystem),
                precipUnit = getPrecipitationUnit(unitsSystem)
            )

            if (!forecastResponse.isSuccessful || forecastResponse.body() == null) {
                return Response.error(
                    500,
                    ResponseBody.create(MediaType.parse("text/plain"), "Forecast error")
                )
            }

            val converted = convertForecastToCurrentWeather(
                forecastResponse.body()!!,
                cityName,
                countryCode
            )
            return Response.success(converted)
        } catch (e: Exception) {
            return Response.error(
                500,
                ResponseBody.create(MediaType.parse("text/plain"), e.message ?: "Unknown error")
            )
        }
    }

    override suspend fun getCurrentWeatherDataResponse(
        forecastLocation: String,
        unitsSystem: String
    ): Response<CurrentWeatherDataResponse> {
        try {
            val searchName = forecastLocation.split(",").first().trim()
            val geocodeResponse = webservice.searchCities(searchName, 10)
            if (!geocodeResponse.isSuccessful || geocodeResponse.body()?.results.isNullOrEmpty()) {
                return Response.error(
                    404,
                    ResponseBody.create(MediaType.parse("text/plain"), "City not found")
                )
            }

            val results = geocodeResponse.body()!!.results!!
            val city = if (forecastLocation.contains(",")) {
                val parts = forecastLocation.split(",").map { it.trim() }
                val targetState = parts.getOrNull(1) ?: ""
                val targetCountry = parts.getOrNull(2) ?: ""
                results.find { result ->
                    (targetState.isEmpty() || result.admin1?.equals(targetState, ignoreCase = true) == true) &&
                    (targetCountry.isEmpty() || result.country?.equals(targetCountry, ignoreCase = true) == true)
                } ?: results[0]
            } else {
                results[0]
            }

            val forecastResponse = webservice.getForecast(
                lat = city.latitude,
                lon = city.longitude,
                tempUnit = getTemperatureUnit(unitsSystem),
                windUnit = getWindSpeedUnit(unitsSystem),
                precipUnit = getPrecipitationUnit(unitsSystem)
            )

            if (!forecastResponse.isSuccessful || forecastResponse.body() == null) {
                return Response.error(
                    500,
                    ResponseBody.create(MediaType.parse("text/plain"), "Forecast error")
                )
            }

            val converted = convertForecastToCurrentWeather(
                forecastResponse.body()!!,
                city.name,
                city.country_code ?: ""
            )
            return Response.success(converted)
        } catch (e: Exception) {
            return Response.error(
                500,
                ResponseBody.create(MediaType.parse("text/plain"), e.message ?: "Unknown error")
            )
        }
    }

    override suspend fun getWeatherForecastDataResponse(
        lat: Double,
        lon: Double,
        exclude: String,
        unitsSystem: String
    ): Response<WeatherForecastDataResponse> {
        try {
            val forecastResponse = webservice.getForecast(
                lat = lat,
                lon = lon,
                tempUnit = getTemperatureUnit(unitsSystem),
                windUnit = getWindSpeedUnit(unitsSystem),
                precipUnit = getPrecipitationUnit(unitsSystem)
            )

            if (!forecastResponse.isSuccessful || forecastResponse.body() == null) {
                return Response.error(
                    500,
                    ResponseBody.create(MediaType.parse("text/plain"), "Forecast error")
                )
            }

            val converted = convertForecastToWeatherForecast(forecastResponse.body()!!)
            return Response.success(converted)
        } catch (e: Exception) {
            return Response.error(
                500,
                ResponseBody.create(MediaType.parse("text/plain"), e.message ?: "Unknown error")
            )
        }
    }

    override suspend fun getGeocodingSuggestions(
        query: String
    ): List<String> {
        return try {
            val response = webservice.searchCities(query, 5)
            if (response.isSuccessful && response.body()?.results != null) {
                response.body()!!.results!!.map { result ->
                    val parts = mutableListOf(result.name)
                    if (!result.admin1.isNullOrEmpty()) {
                        parts.add(result.admin1)
                    }
                    if (!result.country.isNullOrEmpty()) {
                        parts.add(result.country)
                    }
                    parts.joinToString(", ")
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addCityShortcutToDatabase(
        cityShortcut: CityShortcut
    ) {
        cityShortcutDao.addCityShortcut(cityShortcut)
    }

    override suspend fun deleteCityShortcutFromDatabase(
        cityShortcut: CityShortcut
    ) {
        cityShortcutDao.deleteCityShortcut(cityShortcut)
    }

    suspend fun updateCityShortcutInDatabase(cityShortcut: CityShortcut) {
        cityShortcutDao.updateCityShortcut(cityShortcut)
    }

    suspend fun cityExistsByName(name: String): Boolean {
        return cityShortcutDao.countByName(name) > 0
    }

    suspend fun getAllCityShortcutsSync(): List<CityShortcut> {
        return cityShortcutDao.getAllCityShortcutsSync()
    }

    suspend fun addCityShortcutReturnId(cityShortcut: CityShortcut): Long {
        return cityShortcutDao.addCityShortcutReturnId(cityShortcut)
    }

    suspend fun searchCitiesRaw(
        query: String,
        count: Int = 10
    ): List<com.example.weather_app2.webservices.entities.openmeteo.OpenMeteoGeocodingResult> {
        val response = webservice.searchCities(query, count)
        return if (response.isSuccessful && response.body()?.results != null) {
            response.body()!!.results!!
        } else {
            emptyList()
        }
    }
}

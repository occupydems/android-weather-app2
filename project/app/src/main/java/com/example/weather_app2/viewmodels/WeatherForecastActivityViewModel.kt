package com.example.weather_app2.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app2.R
import com.example.weather_app2.di.WeatherApplication
import com.example.weather_app2.models.*
import com.example.weather_app2.repository.RepositoryImpl
import com.example.weather_app2.utils.ClockUtils
import com.example.weather_app2.utils.DummyWeatherData
import com.example.weather_app2.utils.UiUtils
import com.example.weather_app2.webservices.entities.currentweatherdata.CurrentWeatherDataResponse
import com.example.weather_app2.webservices.entities.weatherforecastdata.WeatherForecastDataResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherForecastActivityViewModel @Inject constructor(
    private val repository: RepositoryImpl,
    private val application: WeatherApplication
) : ViewModel() {

    val errorStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val mainForecastLocation: String?
        get() = repository.mainForecastLocation.value

    val mainForecastLat: Double
        get() = repository.mainForecastLat

    val mainForecastLon: Double
        get() = repository.mainForecastLon

    val currentWeatherData: MutableLiveData<CurrentWeatherDataResponse> by lazy {
        MutableLiveData<CurrentWeatherDataResponse>()
    }

    val weatherForecastData: MutableLiveData<WeatherForecastDataResponse> by lazy {
        MutableLiveData<WeatherForecastDataResponse>()
    }

    private val deviceLocationObserver = Observer<String> {
        viewModelScope.launch {
            if (repository.mainForecastLocation.value != null){
                downloadWeatherData()
            }
        }
    }

    private val weatherForecastLocationObserver = Observer<String> {
        viewModelScope.launch {
            if (repository.mainForecastLocation.value != null){
                downloadWeatherData()
            }
        }
    }

    private val unitOfMeasurementObserver = Observer<String> {
        viewModelScope.launch {
            if (repository.mainForecastLocation.value != null){
                downloadWeatherData()
            }
        }
    }

    init {
        repository.deviceLocation.observeForever(deviceLocationObserver)
        repository.mainForecastLocation.observeForever(weatherForecastLocationObserver)
        repository.unitOfMeasurement.observeForever(unitOfMeasurementObserver)
    }

     suspend fun downloadWeatherData(){
         val location = repository.mainForecastLocation.value!!

         if (DummyWeatherData.isDummyLocation(location)) {
             val iconCode = DummyWeatherData.getIconCodeFromDummyLocation(location)
             currentWeatherData.value = DummyWeatherData.generateCurrentWeatherData(iconCode)
             weatherForecastData.value = DummyWeatherData.generateForecastData(iconCode)
             return
         }

         val lat = repository.mainForecastLat
         val lon = repository.mainForecastLon
         val currentWeatherDayResponse = if (lat != 0.0 || lon != 0.0) {
             val displayName = location.split(",").first().trim()
             repository.getCurrentWeatherByCoords(
                 lat, lon, displayName, "",
                 repository.unitOfMeasurement.value!!
             )
         } else {
             repository.getCurrentWeatherDataResponse(
                 location,
                 repository.unitOfMeasurement.value!!
             )
         }
         if (currentWeatherDayResponse.isSuccessful &&
             currentWeatherDayResponse.body() != null ) {
                 currentWeatherData.value = currentWeatherDayResponse.body()
             if (!repository.isTimezoneSet) {
                 repository.deviceTimezone = currentWeatherData.value!!.timezone
                 repository.isTimezoneSet = true
             }
             val weatherForecastDataResponse = repository.getWeatherForecastDataResponse(
                     currentWeatherData.value?.coord!!.lat,
                     currentWeatherData.value?.coord!!.lon,
                     "current,minutely,alerts",
                     repository.unitOfMeasurement.value!!
                 )
             if (weatherForecastDataResponse.isSuccessful &&
                 currentWeatherDayResponse.body() != null) {
                     weatherForecastData.value = weatherForecastDataResponse.body()
             }
         }
    }

    fun updateDeviceLocation(
        location: String
    ) {
        viewModelScope.launch {
            val currentWeatherDataResponse = repository.getCurrentWeatherDataResponse(
                location,
                repository.unitOfMeasurement.value!!
            )
            if ( currentWeatherDataResponse.isSuccessful &&
                currentWeatherDataResponse.body() != null ) {
                val body = currentWeatherDataResponse.body()!!
                val displayName = location.split(",").first().trim()
                repository.deviceLocation.value = displayName
                repository.mainForecastLat = body.coord.lat
                repository.mainForecastLon = body.coord.lon
                repository.mainForecastLocation.value = location
                application.getSharedPreferences("weather_prefs", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putString("last_location", location)
                    .putFloat("last_lat", body.coord.lat.toFloat())
                    .putFloat("last_lon", body.coord.lon.toFloat())
                    .apply()
            } else {
                errorStatus.value = true
            }
        }
    }

    fun updateDeviceLocationWithCoords(
        displayName: String,
        lat: Double,
        lon: Double
    ) {
        repository.deviceLocation.value = displayName
        repository.mainForecastLat = lat
        repository.mainForecastLon = lon
        repository.mainForecastLocation.value = displayName
        application.getSharedPreferences("weather_prefs", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("last_location", displayName)
            .putFloat("last_lat", lat.toFloat())
            .putFloat("last_lon", lon.toFloat())
            .apply()
    }

    suspend fun getGeocodingSuggestions(query: String): List<String> {
        return repository.getGeocodingSuggestions(query)
    }

    fun getApiCallTime(): String {
        return application.getString(R.string.lastUpdateHeader) +
                ClockUtils.getTimeFromUnixTimestamp(
                    currentWeatherData.value!!.dt * 1000L,
                    repository.deviceTimezone * 1000L,
                    repository.deviceTimezone * 1000L,
                    true,
                    clockPeriodMode = true
                )
    }

    fun getHourlyForecastList(): List<HourlyForecastData> {
        val list: MutableList<HourlyForecastData> = mutableListOf()
        list.add(
            HourlyForecastData(
                application.getString(R.string.hourlyForecastNow),
                currentWeatherData.value?.main?.temp!!.toInt(),
                UiUtils.getWeatherIcon(currentWeatherData.value!!.weather[0].icon)
            )
        )
        
        // Find the index of the current hour in the hourly data
        val currentTimeSeconds = System.currentTimeMillis() / 1000
        val hourlyList = weatherForecastData.value!!.hourly
        var startIndex = 1
        for (i in hourlyList.indices) {
            if (hourlyList[i].dt >= currentTimeSeconds) {
                startIndex = i
                break
            }
        }
        // If startIndex is 0, skip to 1 since index 0 would be the current hour (we already show "Now")
        if (startIndex == 0) startIndex = 1
        
        // Show next 24 hours starting from current hour
        val endIndex = minOf(startIndex + 24, hourlyList.size)
        for (i in startIndex until endIndex) {
            list.add(
                HourlyForecastData(
                    ClockUtils.getTimeFromUnixTimestamp(
                        hourlyList[i].dt * 1000L,
                        weatherForecastData.value!!.timezone_offset * 1000L,
                        repository.deviceTimezone * 1000L,
                        false,
                        clockPeriodMode = true
                    ),
                    hourlyList[i].temp.toInt(),
                    UiUtils.getWeatherIcon(hourlyList[i].weather[0].icon)
                )
            )
        }
        return list
    }

    fun getVerticalWeatherDataList(): VerticalWeatherData {
        val dailyForecastList: List<DailyForecastData> = getDailyWeatherForecastList()
        val currentWeatherDataList: List<CurrentWeatherData> = getCurrentWeatherDataList(
            repository.unitOfMeasurement.value!!
        )
        return VerticalWeatherData(dailyForecastList,currentWeatherDataList)
    }

    fun getDailyForecastList(): List<DailyForecastData> = getDailyWeatherForecastList()

    fun getWeatherDetailItems(): List<WeatherDetailItem> {
        val items = mutableListOf<WeatherDetailItem>()
        val unit = repository.unitOfMeasurement.value!!
        val data = currentWeatherData.value!!

        val sunriseTime = ClockUtils.getTimeFromUnixTimestamp(
            data.sys.sunrise * 1000L,
            data.timezone * 1000L,
            repository.deviceTimezone * 1000L,
            true, clockPeriodMode = true
        )
        val sunsetTime = ClockUtils.getTimeFromUnixTimestamp(
            data.sys.sunset * 1000L,
            data.timezone * 1000L,
            repository.deviceTimezone * 1000L,
            true, clockPeriodMode = true
        )

        val uvIndex = data.uv_index
        val uvLevel = when {
            uvIndex <= 2 -> "Low"
            uvIndex <= 5 -> "Moderate"
            uvIndex <= 7 -> "High"
            uvIndex <= 10 -> "Very High"
            else -> "Extreme"
        }
        items.add(WeatherDetailItem(
            header = application.getString(R.string.uvIndexHeader),
            value = String.format("%.1f", uvIndex),
            subtitle = uvLevel,
            type = DetailCardType.UV_INDEX
        ))

        val currentTimeMs = System.currentTimeMillis()
        val sunriseMs = data.sys.sunrise * 1000L
        val sunsetMs = data.sys.sunset * 1000L
        val isDaytime = currentTimeMs in sunriseMs..sunsetMs
        items.add(WeatherDetailItem(
            header = if (isDaytime) application.getString(R.string.sunsetHeader) else application.getString(R.string.sunriseHeader),
            value = if (isDaytime) sunsetTime else sunriseTime,
            subtitle = if (isDaytime) "Sunrise: $sunriseTime" else "Sunset: $sunsetTime",
            type = DetailCardType.SUNRISE_SUNSET,
            sunriseTimestamp = sunriseMs,
            sunsetTimestamp = sunsetMs,
            currentTimestamp = currentTimeMs,
            timezoneOffset = data.timezone
        ))

        val feelsLike = data.main.feels_like
        val actualTemp = data.main.temp
        val feelsLikeSubtitle = when {
            Math.abs(feelsLike - actualTemp) < 2 -> "Similar to the actual temperature."
            feelsLike < actualTemp -> "Wind is making it feel cooler."
            else -> "Humidity is making it feel warmer."
        }
        items.add(WeatherDetailItem(
            header = application.getString(R.string.feelsLikeHeader),
            value = application.getString(R.string.temp, feelsLike.toInt()),
            subtitle = feelsLikeSubtitle
        ))

        val humidity = data.main.humidity
        val dewPoint = actualTemp - ((100 - humidity) / 5.0)
        items.add(WeatherDetailItem(
            header = application.getString(R.string.humidityHeader),
            value = "${humidity}%",
            subtitle = "The dew point is ${String.format("%.0f", dewPoint)}° right now."
        ))

        val visibility = data.visibility
        val visibilityValue = if (unit == UnitOfMeasurement.METRIC.value) {
            if (visibility >= 1000) "${visibility / 1000} km" else "$visibility m"
        } else {
            val miles = visibility / 1609.0
            "${String.format("%.1f", miles)} mi"
        }
        val visibilitySubtitle = when {
            visibility >= 10000 -> "It's perfectly clear right now."
            visibility >= 5000 -> "Moderate visibility."
            else -> "Low visibility conditions."
        }
        items.add(WeatherDetailItem(
            header = application.getString(R.string.visibilityHeader),
            value = visibilityValue,
            subtitle = visibilitySubtitle
        ))

        items.add(WeatherDetailItem(
            header = application.getString(R.string.pressureHeader),
            value = application.getString(R.string.pascals, data.main.pressure),
            subtitle = "Standard pressure is 1013 hPa."
        ))

        val windDeg = data.wind.deg
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val dirIndex = ((windDeg + 22.5) / 45.0).toInt() % 8
        val direction = directions[dirIndex]
        val windGust = data.wind.gust

        if (unit == UnitOfMeasurement.METRIC.value) {
            val gustSubtitle = if (windGust > 0) "Gusts up to ${windGust.toInt()} km/h" else ""
            items.add(WeatherDetailItem(
                header = application.getString(R.string.windHeader),
                value = "${data.wind.speed.toInt()} km/h $direction",
                subtitle = gustSubtitle
            ))
        } else {
            val gustSubtitle = if (windGust > 0) "Gusts up to ${windGust.toInt()} mph" else ""
            items.add(WeatherDetailItem(
                header = application.getString(R.string.windHeader),
                value = "${data.wind.speed.toInt()} mph $direction",
                subtitle = gustSubtitle
            ))
        }

        return items
    }

    fun getDailyTempRange(): Pair<Int, Int> {
        val daily = weatherForecastData.value?.daily ?: return Pair(0, 100)
        if (daily.size <= 1) return Pair(0, 100)
        var globalMin = Int.MAX_VALUE
        var globalMax = Int.MIN_VALUE
        for (i in 0 until minOf(10, daily.size)) {
            val lo = daily[i].temp.min.toInt()
            val hi = daily[i].temp.max.toInt()
            if (lo < globalMin) globalMin = lo
            if (hi > globalMax) globalMax = hi
        }
        if (globalMin >= globalMax) return Pair(globalMin - 1, globalMax + 1)
        return Pair(globalMin, globalMax)
    }

    private fun getDailyWeatherForecastList(): List<DailyForecastData> {
        val dailyForecastList: MutableList<DailyForecastData> = mutableListOf()
        val dailyData = weatherForecastData.value!!.daily
        val count = minOf(10, dailyData.size)
        for (i in 0 until count) {
            dailyForecastList.add(
                DailyForecastData(
                    ClockUtils.getDayFromUnixTimestamp(
                        dailyData[i].dt*1000L,
                        currentWeatherData.value!!.timezone * 1000L,
                        repository.deviceTimezone * 1000L,
                    ),
                    dailyData[i].temp.max.toInt(),
                    dailyData[i].temp.min.toInt(),
                    UiUtils.getWeatherIcon(
                        dailyData[i].weather[0].icon
                    )
                )
            )
        }
        return dailyForecastList
    }

    private fun getCurrentWeatherDataList(
        unitOfMeasurement: String
    ): List<CurrentWeatherData> {
        val weatherDataList = mutableListOf(
            CurrentWeatherData(
                application.getString(
                    R.string.sunriseHeader
                ),
                ClockUtils.getTimeFromUnixTimestamp(
                    currentWeatherData.value!!.sys.sunrise * 1000L,
                    currentWeatherData.value!!.timezone * 1000L,
                    repository.deviceTimezone * 1000L,
                    true,
                    clockPeriodMode = true
                ),
                application.getString(
                    R.string.sunsetHeader
                ),
                ClockUtils.getTimeFromUnixTimestamp(
                    currentWeatherData.value!!.sys.sunset * 1000L,
                    currentWeatherData.value!!.timezone * 1000L ,
                    repository.deviceTimezone * 1000L,
                    true,
                    clockPeriodMode = true
                )
            ),
            CurrentWeatherData(
                application.getString(
                    R.string.localTimeHeader
                ),
                ClockUtils.getTimeFromUnixTimestamp(
                    System.currentTimeMillis(),
                    currentWeatherData.value!!.timezone * 1000L,
                    repository.deviceTimezone * 1000L,
                    true,
                    clockPeriodMode = true
                ),
                application.getString(
                    R.string.feelsLikeHeader
                ),
                application.getString(
                    R.string.temp,
                    currentWeatherData.value!!.main.feels_like.toInt()
                )
            ),
            CurrentWeatherData(
                application.getString(
                    R.string.pressureHeader
                ),
                application.getString(
                    R.string.pascals,
                    currentWeatherData.value!!.main.pressure
                ),
                application.getString(
                    R.string.humidityHeader
                ),
                "${currentWeatherData.value!!.main.humidity}%"
            )
        )
        if (unitOfMeasurement == UnitOfMeasurement.METRIC.value) {
            weatherDataList.add(
                CurrentWeatherData(
                    application.getString(
                        R.string.windHeader
                    ),
                    application.getString(
                        R.string.kph,
                        currentWeatherData.value!!.wind.speed.toInt()
                    ),
                    application.getString(
                        R.string.visibilityHeader
                    ),
                    application.getString(
                        R.string.meters,
                        currentWeatherData.value!!.visibility
                    )
                )
            )
            return weatherDataList
        }
        weatherDataList.add(
            CurrentWeatherData(
                application.getString(
                    R.string.windHeader
                ),
                application.getString(
                    R.string.miph,
                    currentWeatherData.value!!.wind.speed.toInt()
                ),
                application.getString(
                    R.string.visibilityHeader
                ),
                application.getString(
                    R.string.meters,
                    currentWeatherData.value!!.visibility
                )
            )
        )
        return weatherDataList
    }
}

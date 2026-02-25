package com.example.weather_app2.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app2.models.CityShortcutData
import com.example.weather_app2.models.GeoSearchResult
import com.example.weather_app2.models.UnitOfMeasurement
import com.example.weather_app2.database.entities.CityShortcut
import com.example.weather_app2.repository.RepositoryImpl
import com.example.weather_app2.utils.ClockUtils
import com.example.weather_app2.utils.DummyWeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CitySelectionActivityViewModel @Inject constructor(
    private val repository: RepositoryImpl,
    private val unitOfMeasurementSP: SharedPreferences,
    private val unitOfMeasurementSPEditor: SharedPreferences.Editor
) : ViewModel() {

    val citySelectionList: MutableLiveData<MutableList<CityShortcut>> by lazy {
        MutableLiveData<MutableList<CityShortcut>>()
    }

    val errorStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    private var hasLoadedOnce = false
    private var isUpdating = false
    private val deletedCityIds = mutableSetOf<Int>()
    private var searchJob: Job? = null

    val searchResults: MutableLiveData<List<GeoSearchResult>> by lazy {
        MutableLiveData<List<GeoSearchResult>>(emptyList())
    }

    private val unitOfMeasurementObserver = Observer<String> {
        if (hasLoadedOnce && !isUpdating) {
            refreshWeatherData()
        }
    }

    private val deviceLocationObserver = Observer<String> {
        if (!hasLoadedOnce && repository.deviceLocation.value != null) {
            loadCityList()
        }
    }

    init {
        repository.deviceLocation.observeForever(deviceLocationObserver)
        repository.unitOfMeasurement.observeForever(unitOfMeasurementObserver)
        
        viewModelScope.launch {
            loadCityList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.deviceLocation.removeObserver(deviceLocationObserver)
        repository.unitOfMeasurement.removeObserver(unitOfMeasurementObserver)
    }

    private fun loadCityList() {
        if (isUpdating) return
        isUpdating = true
        hasLoadedOnce = true

        viewModelScope.launch {
            try {
                val cachedCities = withContext(Dispatchers.IO) {
                    repository.getAllCityShortcutsSync()
                }
                val initialList = cachedCities.filter { it.id !in deletedCityIds }.toMutableList()

                if (com.example.weather_app2.BuildConfig.ENABLE_DUMMY_CARDS) {
                    initialList.addAll(DummyWeatherData.getDummyCityShortcuts())
                }

                val deviceLocation = repository.deviceLocation.value
                if (deviceLocation != null) {
                    val lastDevice = citySelectionList.value?.find { it.id == 1000 }
                    if (lastDevice != null) {
                        initialList.add(lastDevice)
                    } else {
                        initialList.add(CityShortcut(1000, deviceLocation, "", 0, "clear_d", 0, 0))
                    }
                }
                if (initialList.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        citySelectionList.value = initialList
                    }
                }

                refreshWeatherDataInternal()
            } catch (_: Exception) {
                isUpdating = false
            }
        }
    }

    private fun refreshWeatherData() {
        if (!hasLoadedOnce) return
        isUpdating = true

        viewModelScope.launch {
            refreshWeatherDataInternal()
        }
    }

    private suspend fun refreshWeatherDataInternal() {
        try {
            val citiesList = withContext(Dispatchers.IO) {
                repository.getAllCityShortcutsSync()
            }

            val freshCities: List<CityShortcut>
            val deviceResult: CityShortcut?

            coroutineScope {
                val deferredResults = citiesList.map { city ->
                    async(Dispatchers.IO) {
                        try {
                            val response = if (city.latitude != 0.0 || city.longitude != 0.0) {
                                repository.getCurrentWeatherByCoords(
                                    city.latitude,
                                    city.longitude,
                                    city.cityName,
                                    "",
                                    repository.unitOfMeasurement.value!!
                                )
                            } else {
                                repository.getCurrentWeatherDataResponse(
                                    city.cityName,
                                    repository.unitOfMeasurement.value!!
                                )
                            }
                            if (response.isSuccessful && response.body() != null) {
                                val body = response.body()!!
                                CityShortcut(
                                    city.id,
                                    city.cityName,
                                    ClockUtils.getTimeFromUnixTimestamp(
                                        System.currentTimeMillis(),
                                        body.timezone * 1000L,
                                        repository.deviceTimezone * 1000L,
                                        true,
                                        clockPeriodMode = true
                                    ),
                                    body.main.temp.toInt(),
                                    body.weather[0].icon,
                                    body.main.temp_max.toInt(),
                                    body.main.temp_min.toInt(),
                                    if (city.latitude != 0.0 || city.longitude != 0.0) city.latitude else body.coord.lat,
                                    if (city.latitude != 0.0 || city.longitude != 0.0) city.longitude else body.coord.lon
                                )
                            } else null
                        } catch (_: Exception) { null }
                    }
                }

                val deviceLocationDeferred = async(Dispatchers.IO) {
                    try {
                        val devLoc = repository.deviceLocation.value ?: return@async null
                        val response = repository.getCurrentWeatherDataResponse(
                            devLoc,
                            repository.unitOfMeasurement.value!!
                        )
                        if (response.isSuccessful && response.body() != null) {
                            CityShortcut(
                                1000,
                                devLoc,
                                ClockUtils.getTimeFromUnixTimestamp(
                                    System.currentTimeMillis(),
                                    response.body()!!.timezone * 1000L,
                                    repository.deviceTimezone * 1000L,
                                    true,
                                    clockPeriodMode = true
                                ),
                                response.body()!!.main.temp.toInt(),
                                response.body()!!.weather[0].icon,
                                response.body()!!.main.temp_max.toInt(),
                                response.body()!!.main.temp_min.toInt()
                            )
                        } else null
                    } catch (_: Exception) { null }
                }

                freshCities = deferredResults.mapNotNull { it.await() }
                deviceResult = deviceLocationDeferred.await()
            }

            val latestDbCities = withContext(Dispatchers.IO) {
                repository.getAllCityShortcutsSync()
            }
            val latestDbIds = latestDbCities.map { it.id }.toSet()

            val currentUiList = citySelectionList.value?.toMutableList() ?: mutableListOf()
            val finalList = mutableListOf<CityShortcut>()
            val seenNames = mutableSetOf<String>()

            for (dbCity in latestDbCities) {
                if (dbCity.id in deletedCityIds) continue
                val freshVersion = freshCities.find { it.id == dbCity.id }
                val cityToAdd = freshVersion ?: currentUiList.find { it.id == dbCity.id } ?: dbCity
                if (seenNames.add(cityToAdd.cityName.lowercase())) {
                    finalList.add(cityToAdd)
                }
            }

            for (fresh in freshCities) {
                if (fresh.id in latestDbIds && fresh.id !in deletedCityIds) {
                    withContext(Dispatchers.IO) {
                        try {
                            repository.updateCityShortcutInDatabase(fresh)
                        } catch (_: Exception) {}
                    }
                }
            }

            if (com.example.weather_app2.BuildConfig.ENABLE_DUMMY_CARDS) {
                finalList.addAll(DummyWeatherData.getDummyCityShortcuts())
            }

            if (deviceResult != null) {
                finalList.add(deviceResult)
            } else {
                val existing = currentUiList.find { it.id == 1000 }
                if (existing != null) {
                    finalList.add(existing)
                } else if (repository.deviceLocation.value != null) {
                    finalList.add(CityShortcut(1000, repository.deviceLocation.value!!, "", 0, "clear_d", 0, 0))
                }
            }

            withContext(Dispatchers.Main) {
                citySelectionList.value = finalList
            }
        } finally {
            isUpdating = false
        }
    }

    fun updateMainWeatherForecastLocation(
        newLocation: String,
        lat: Double = 0.0,
        lon: Double = 0.0
    ) {
        repository.mainForecastLat = lat
        repository.mainForecastLon = lon
        repository.mainForecastLocation.value = newLocation
    }

    fun addNewCityShortCutClickListener(
        cityName: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val cityShortcutData = getCityShortcutData(cityName) ?: return@launch
            val resolvedName = cityShortcutData.cityName

            val existsInDb = withContext(Dispatchers.IO) {
                repository.cityExistsByName(resolvedName)
            }
            if (existsInDb) return@launch

            val alreadyInList = citySelectionList.value?.any {
                it.cityName.equals(resolvedName, ignoreCase = true) &&
                    !DummyWeatherData.isDummyId(it.id)
            } ?: false
            if (alreadyInList) return@launch

            val isDeviceLocation = repository.deviceLocation.value?.equals(resolvedName, ignoreCase = true) == true
            if (isDeviceLocation) return@launch

            if (repository.deviceLocation.value == null) {
                repository.deviceLocation.value = resolvedName
                repository.mainForecastLocation.value = resolvedName
            } else {
                val newCity = CityShortcut(
                    0,
                    resolvedName,
                    cityShortcutData.localTime,
                    cityShortcutData.temp,
                    cityShortcutData.icon,
                    cityShortcutData.highTemp,
                    cityShortcutData.lowTemp,
                    cityShortcutData.latitude,
                    cityShortcutData.longitude
                )
                val insertedId = withContext(Dispatchers.IO) {
                    repository.addCityShortcutReturnId(newCity)
                }
                if (insertedId == -1L) return@launch
                val cityWithId = newCity.copy(id = insertedId.toInt())
                deletedCityIds.remove(cityWithId.id)
                val currentList = citySelectionList.value?.toMutableList() ?: mutableListOf()
                val deviceEntry = currentList.find { it.id == 1000 }
                if (deviceEntry != null) {
                    currentList.remove(deviceEntry)
                }
                currentList.add(cityWithId)
                if (deviceEntry != null) {
                    currentList.add(deviceEntry)
                }
                citySelectionList.value = currentList
            }
        }
    }

    private suspend fun getCityShortcutData(
        cityName: String
    ): CityShortcutData? {
        val currentWeatherDataResponse = repository.getCurrentWeatherDataResponse(
            cityName,
            repository.unitOfMeasurement.value!!
        )
        if (currentWeatherDataResponse.isSuccessful && currentWeatherDataResponse.body() != null) {
            val body = currentWeatherDataResponse.body()!!
            return CityShortcutData(
                body.name,
                ClockUtils.getTimeFromUnixTimestamp(
                    System.currentTimeMillis(),
                    body.timezone * 1000L,
                    repository.deviceTimezone * 1000L,
                    true,
                    clockPeriodMode = true
                ),
                body.main.temp.toInt(),
                body.weather[0].icon,
                body.main.temp_max.toInt(),
                body.main.temp_min.toInt(),
                body.coord.lat,
                body.coord.lon
            )
        }
        errorStatus.value = true
        return null
    }

    fun deleteCityShortCutClickListener(
        cityShortcut: CityShortcut
    ) {
        if (DummyWeatherData.isDummyId(cityShortcut.id)) return
        deletedCityIds.add(cityShortcut.id)

        val currentList = citySelectionList.value?.toMutableList() ?: mutableListOf()
        currentList.removeAll { it.id == cityShortcut.id }
        citySelectionList.value = currentList

        if (repository.mainForecastLocation.value == cityShortcut.cityName) {
            repository.mainForecastLocation.value = repository.deviceLocation.value
        }

        viewModelScope.launch {
            withContext(NonCancellable + Dispatchers.IO) {
                repository.deleteCityShortcutFromDatabase(cityShortcut)
            }
        }
    }

    fun changeUnitClickListener(): String {
        if (repository.unitOfMeasurement.value == UnitOfMeasurement.METRIC.value) {
            repository.unitOfMeasurement.value = UnitOfMeasurement.IMPERIAL.value
        } else {
            repository.unitOfMeasurement.value = UnitOfMeasurement.METRIC.value
        }
        unitOfMeasurementSPEditor.apply {
            putString("unitOfMeasurement", repository.unitOfMeasurement.value)
            apply()
        }
        return repository.unitOfMeasurement.value!!
    }

    suspend fun getGeocodingSuggestions(query: String): List<String> {
        return repository.getGeocodingSuggestions(query)
    }

    fun onSearchTextChanged(query: String) {
        searchJob?.cancel()
        if (query.length < 2) {
            searchResults.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            val results = searchCities(query)
            searchResults.value = results
        }
    }

    suspend fun searchCities(query: String): List<GeoSearchResult> {
        return try {
            val response = repository.searchCitiesRaw(query, 10)
            response.map { result ->
                GeoSearchResult(
                    name = result.name,
                    admin1 = result.admin1,
                    country = result.country,
                    latitude = result.latitude,
                    longitude = result.longitude
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addCityFromSearchResult(result: GeoSearchResult) {
        val displayName = result.name
        addNewCityShortCutClickListener(result.displayString())
    }

    fun getUnitMode() = repository.unitOfMeasurement.value!!

    fun getOpaqueHeaders(): Boolean {
        return unitOfMeasurementSP.getBoolean("opaqueHeaders", false)
    }

    fun setOpaqueHeaders(opaque: Boolean) {
        unitOfMeasurementSPEditor.putBoolean("opaqueHeaders", opaque).apply()
    }
}

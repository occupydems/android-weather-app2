package com.example.weather_app2.views

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app2.R
import com.example.weather_app2.adapters.HourlyForecastAdapter
import com.example.weather_app2.databinding.ActivityWeatherForecastBinding
import com.example.weather_app2.models.DailyForecastData
import com.example.weather_app2.models.DetailCardType
import com.example.weather_app2.models.WeatherDetailItem
import com.example.weather_app2.views.detail.*
import com.example.weather_app2.utils.UiUtils
import com.example.weather_app2.viewmodels.WeatherForecastActivityViewModel
import com.example.weather_app2.webservices.entities.currentweatherdata.CurrentWeatherDataResponse
import com.example.weather_app2.webservices.entities.weatherforecastdata.WeatherForecastDataResponse
import com.google.android.gms.location.LocationServices
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

private const val PERMISSION_LOCATION_REQUEST_CODE = 10
@AndroidEntryPoint
class WeatherForecastActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityWeatherForecastBinding
    private val viewModel: WeatherForecastActivityViewModel by viewModels()
    private val stickyCards = mutableListOf<StickyHeaderCardLayout>()
    private var headerCollapseDistance = 0f
    private val lottieEffectManager = StickyHeaderEffectManager()
    private var oppoRenderer: com.example.weather_app2.engine.WeatherEffectsRenderer? = null
    private var currentWeatherTag: String = ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerViews()
        setUpClickListeners()
        initOppoEngine()

        checkPermissions()
        getDeviceLocation()

        val currentWeatherDataObserver = Observer<CurrentWeatherDataResponse> { newData ->
            updateBackground()
            updateTextViews(newData)
        }
        viewModel.currentWeatherData.observe(this,currentWeatherDataObserver)
        val weatherForecastDataObserver = Observer<WeatherForecastDataResponse> {
            updateHourlyForecast()
            updateDailyForecast()
            if (viewModel.currentWeatherData.value != null) {
                updateWeatherDetails()
            }
        }
        viewModel.weatherForecastData.observe(this,weatherForecastDataObserver)
        val errorStatusObserver = Observer<Boolean> { status ->
            if (status){
                viewModel.errorStatus.value = false
                Toast.makeText(this, getString(R.string.didnt_found_city), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.errorStatus.observe(this,errorStatusObserver)

        binding.hourlyForecastContainer.stickyHeaderCount = 1
        binding.dailyForecastCard.stickyHeaderCount = 1
        stickyCards.add(binding.hourlyForecastContainer)
        stickyCards.add(binding.dailyForecastCard)

        binding.nsvWeatherDetails.post {
            binding.weatherEffectsView.setLandingLine(binding.nsvWeatherDetails.top.toFloat())
            updateStickyHeaders(binding.nsvWeatherDetails.scrollY)

            headerCollapseDistance = binding.nsvWeatherDetails.top.toFloat().coerceAtLeast(1f)

            binding.nsvWeatherDetails.setOnScrollChangeListener { v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                binding.weatherEffectsView.setLandingLine(binding.nsvWeatherDetails.top.toFloat())
                updateStickyHeaders(scrollY)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applyStickyHeaderPreference()
        oppoRenderer?.onActivityResume()
        if (hasLocationPermission()) {
            getDeviceLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        oppoRenderer?.onActivityPause()
    }

    override fun onDestroy() {
        lottieEffectManager.cleanup()
        oppoRenderer?.onActivityDestroy()
        super.onDestroy()
    }

    private fun initOppoEngine() {
        try {
            val renderer = com.example.weather_app2.engine.WeatherEffectsRenderer(this)
            renderer.initialize()

            val container = binding.motionContainer
            container.addView(renderer, 0,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                    androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                    startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                    endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                }
            )
            oppoRenderer = renderer
        } catch (e: Exception) {
            oppoRenderer = null
        } catch (e: UnsatisfiedLinkError) {
            oppoRenderer = null
        }
    }

    private fun applyStickyHeaderPreference() {
        val prefs = getSharedPreferences("unitOfMeasurement", MODE_PRIVATE)
        val opaque = prefs.getBoolean("opaqueHeaders", false)
        val alpha = if (opaque) 255 else 180
        for (card in stickyCards) {
            card.opaqueHeaders = opaque
            card.setCardOpacityAlpha(alpha)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }

    override fun onPermissionsDenied(
        requestCode: Int,
        perms: List<String>
    ) {
        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        val savedLocation = prefs.getString("last_location", null)
        if (savedLocation != null) {
            restoreFromSavedPrefs(prefs, savedLocation)
        }
    }

    override fun onPermissionsGranted(
        requestCode: Int
        , perms: List<String>
    ) {
        Toast.makeText(
            this,
            "Location permission granted.",
            Toast.LENGTH_SHORT
        ).show()
        getDeviceLocation()
    }

    private fun checkPermissions() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission() = EasyPermissions.hasPermissions(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application works best with location permission",
            PERMISSION_LOCATION_REQUEST_CODE,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (hasLocationPermission()) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location == null) {
                    requestFreshLocation(fusedLocationProviderClient)
                } else {
                    resolveAndUpdateLocation(location.latitude, location.longitude)
                }
            }
        } else {
            val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
            val savedLocation = prefs.getString("last_location", null)
            if (savedLocation != null) {
                restoreFromSavedPrefs(prefs, savedLocation)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(client: com.google.android.gms.location.FusedLocationProviderClient) {
        val request = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).setMaxUpdates(1).build()
        client.requestLocationUpdates(request, object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                client.removeLocationUpdates(this)
                val loc = result.lastLocation
                if (loc != null) {
                    resolveAndUpdateLocation(loc.latitude, loc.longitude)
                } else {
                    val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
                    val savedLocation = prefs.getString("last_location", null)
                    if (savedLocation != null) {
                        restoreFromSavedPrefs(prefs, savedLocation)
                    }
                }
            }
        }, mainLooper)
    }

    private fun resolveAndUpdateLocation(lat: Double, lon: Double) {
        val address = try {
            Geocoder(this).getFromLocation(lat, lon, 1)?.firstOrNull()
        } catch (_: Exception) { null }
        val name = address?.locality
            ?: address?.subAdminArea
            ?: address?.adminArea
            ?: String.format("%.2f, %.2f", lat, lon)
        viewModel.updateDeviceLocationWithCoords(name, lat, lon)
    }

    private fun restoreFromSavedPrefs(prefs: android.content.SharedPreferences, savedLocation: String) {
        val savedLat = prefs.getFloat("last_lat", 0f).toDouble()
        val savedLon = prefs.getFloat("last_lon", 0f).toDouble()
        if (savedLat != 0.0 || savedLon != 0.0) {
            viewModel.updateDeviceLocationWithCoords(savedLocation, savedLat, savedLon)
        } else {
            viewModel.updateDeviceLocation(savedLocation)
        }
    }

    private fun setUpClickListeners() {
        binding.btnCitySelection.setOnClickListener {
            val intent = Intent(this, CitySelectionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up_in, R.anim.zoom_out_exit)
        }
        binding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                viewModel.downloadWeatherData()
            }
        }
    }

    private fun setUpRecyclerViews(){
        binding.rvHourlyForecast.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun updateBackground(){
        val weatherTag = viewModel.currentWeatherData.value!!.weather[0].icon
        currentWeatherTag = weatherTag
        val headerColorInt = UiUtils.getHeaderColor(weatherTag)
        val cardBg = UiUtils.getCardBackground(weatherTag)
        window.statusBarColor = ContextCompat.getColor(
            this,
            UiUtils.getStatusBarColor(weatherTag)
        )
        binding.apply {
            motionContainer.setBackgroundResource(
                UiUtils.getWeatherForecastBackground(
                    weatherTag
                )
            )
            divider.setBackgroundResource(headerColorInt)
            hourlyForecastContainer.setBackgroundResource(cardBg)
            dailyForecastCard.setBackgroundResource(cardBg)
            weatherEffectsView.setWeatherCondition(weatherTag)
        }
        for (card in stickyCards) {
            card.setWeatherCondition(weatherTag)
        }

        lottieEffectManager.updateWeatherCondition(weatherTag)

        try {
            val wmoCode = iconToWmoCode(weatherTag)
            val sceneManager = com.example.weather_app2.engine.WeatherSceneManager(this)
            val timePeriod = sceneManager.getTimePeriod(-1, null)
            oppoRenderer?.setWeatherCondition(wmoCode, timePeriod)

            val windDir = viewModel.currentWeatherData.value?.wind?.deg?.toFloat() ?: 0f
            val windSpeed = viewModel.currentWeatherData.value?.wind?.speed?.toFloat() ?: 0f
            oppoRenderer?.setWindData(windDir, windSpeed)
        } catch (_: Exception) {}

        applyStickyHeaderPreference()
    }

    private fun updateTextViews(
        data: CurrentWeatherDataResponse
    ) {
        binding.apply {
            tvCity.textSize = 34f
            tvCity.text = data.name
            tvWeatherDescription.text = data.weather[0].description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            tvTemp.text = getString(
                R.string.temp,
                data.main.temp.toInt()
            )
            tvH.text = getString(
                R.string.main_H_temp,
                data.main.temp_max.toInt()
            )
            tvL.text = getString(
                R.string.main_L_temp,
                data.main.temp_min.toInt()
            )
            tvApiCallTime.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    UiUtils.getHeaderColor(viewModel.currentWeatherData.value!!.weather[0].icon
                    )
                )
            )
            tvFeelsLike.text = "Feels like ${data.main.feels_like.toInt()}°"
            tvApiCallTime.text = viewModel.getApiCallTime()
            tvHourlyHeader.text = getString(R.string.hourlyForecastHeader)
            val condDescription = data.weather[0].description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            tvCondensedInfo.text = "${data.main.temp.toInt()}° | $condDescription\nFeels like ${data.main.feels_like.toInt()}°"
        }
    }

    private fun updateHourlyForecast() {
        binding.rvHourlyForecast.adapter = HourlyForecastAdapter(viewModel.getHourlyForecastList())
    }

    private fun updateDailyForecast() {
        val dailyList = viewModel.getDailyForecastList()
        val (globalMin, globalMax) = viewModel.getDailyTempRange()
        val container = binding.llDailyRows
        container.removeAllViews()

        for (i in dailyList.indices) {
            if (i > 0) {
                val divider = View(this)
                val dividerParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (0.5f * resources.displayMetrics.density).toInt()
                )
                dividerParams.marginStart = (14 * resources.displayMetrics.density).toInt()
                dividerParams.marginEnd = (14 * resources.displayMetrics.density).toInt()
                divider.layoutParams = dividerParams
                divider.setBackgroundColor(0x44FFFFFF)
                container.addView(divider)
            }

            val rowView = LayoutInflater.from(this).inflate(
                R.layout.item_daily_forecast_row, container, false
            )
            val item = dailyList[i]

            rowView.findViewById<TextView>(R.id.tvDay).text = item.day
            rowView.findViewById<ImageView>(R.id.ivDailyWeatherIcon).setImageResource(item.icon)
            rowView.findViewById<TextView>(R.id.tvDailyL).text = getString(R.string.temp, item.tempL)
            rowView.findViewById<TextView>(R.id.tvDailyH).text = getString(R.string.temp, item.tempH)

            val tempBarContainer = rowView.findViewById<FrameLayout>(R.id.tempBarContainer)
            val tempBarFill = rowView.findViewById<View>(R.id.tempBarFill)

            tempBarContainer.post {
                val totalWidth = tempBarContainer.width
                if (totalWidth > 0 && globalMax > globalMin) {
                    val range = globalMax - globalMin
                    val startFraction = ((item.tempL - globalMin).toFloat() / range).coerceIn(0f, 1f)
                    val endFraction = ((item.tempH - globalMin).toFloat() / range).coerceIn(0f, 1f)
                    val fillWidth = ((endFraction - startFraction) * totalWidth).toInt().coerceAtLeast(4)
                    val startMargin = (startFraction * totalWidth).toInt()

                    val params = FrameLayout.LayoutParams(
                        fillWidth,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    params.marginStart = startMargin
                    tempBarFill.layoutParams = params
                }
            }

            container.addView(rowView)
        }
    }

    private fun updateWeatherDetails() {
        val detailItems = viewModel.getWeatherDetailItems()
        val grid = binding.glWeatherDetails
        grid.removeAllViews()
        grid.columnCount = 2

        val dp4 = (4 * resources.displayMetrics.density).toInt()
        val weatherTag = viewModel.currentWeatherData.value?.weather?.get(0)?.icon ?: "clear_d"
        val cardBg = UiUtils.getCardBackground(weatherTag)

        for (i in detailItems.indices) {
            val cardView = LayoutInflater.from(this).inflate(
                R.layout.item_weather_detail_card, grid, false
            )
            cardView.setBackgroundResource(cardBg)
            cardView.findViewById<TextView>(R.id.tvDetailHeader).text = detailItems[i].header
            cardView.findViewById<TextView>(R.id.tvDetailValue).text = detailItems[i].value

            val tvSubtitle = cardView.findViewById<TextView>(R.id.tvDetailSubtitle)
            if (detailItems[i].subtitle.isNotEmpty()) {
                tvSubtitle.text = detailItems[i].subtitle
                tvSubtitle.visibility = View.VISIBLE
            } else {
                tvSubtitle.visibility = View.GONE
            }

            val arcContainer = cardView.findViewById<FrameLayout>(R.id.sunArcContainer)
            val uvBarContainer = cardView.findViewById<LinearLayout>(R.id.uvBarContainer)

            when (detailItems[i].type) {
                com.example.weather_app2.models.DetailCardType.SUNRISE_SUNSET -> {
                    arcContainer.visibility = View.VISIBLE
                    val arcView = SunriseSunsetArcView(this)
                    arcView.setSunTimes(
                        detailItems[i].sunriseTimestamp,
                        detailItems[i].sunsetTimestamp,
                        detailItems[i].currentTimestamp,
                        detailItems[i].timezoneOffset
                    )
                    arcContainer.addView(arcView, FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        (80 * resources.displayMetrics.density).toInt()
                    ))
                }
                com.example.weather_app2.models.DetailCardType.UV_INDEX -> {
                    uvBarContainer.visibility = View.VISIBLE
                    val uvValue = viewModel.currentWeatherData.value?.uv_index ?: 0.0
                    val uvBar = cardView.findViewById<View>(R.id.uvBarFill)
                    val uvBarBg = cardView.findViewById<View>(R.id.uvBarBackground)
                    uvBarBg.post {
                        val totalWidth = uvBarBg.width
                        val fraction = (uvValue / 11.0).coerceIn(0.0, 1.0)
                        val fillWidth = (fraction * totalWidth).toInt().coerceAtLeast(4)
                        uvBar.layoutParams = FrameLayout.LayoutParams(fillWidth, uvBar.layoutParams.height)
                    }
                }
                else -> {}
            }

            val detailItem = detailItems[i]
            cardView.setOnClickListener {
                val targetActivity = when (detailItem.type) {
                    DetailCardType.UV_INDEX -> UVIndexDetailActivity::class.java
                    DetailCardType.SUNRISE_SUNSET -> SunriseSunsetDetailActivity::class.java
                    DetailCardType.WIND -> WindDetailActivity::class.java
                    DetailCardType.HUMIDITY -> HumidityDetailActivity::class.java
                    DetailCardType.PRESSURE -> PressureDetailActivity::class.java
                    DetailCardType.VISIBILITY -> VisibilityDetailActivity::class.java
                    DetailCardType.FEELS_LIKE -> FeelsLikeDetailActivity::class.java
                    DetailCardType.PRECIPITATION -> PrecipitationDetailActivity::class.java
                    else -> DetailBaseActivity::class.java
                }
                val intent = Intent(this, targetActivity).apply {
                    putExtra(DetailBaseActivity.EXTRA_CARD_TYPE, detailItem.header)
                    putExtra(DetailBaseActivity.EXTRA_WEATHER_DATA, detailItem)
                }
                startActivity(intent)
            }

            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(i / 2)
            params.columnSpec = GridLayout.spec(i % 2, 1f)
            params.width = 0
            params.setMargins(dp4, dp4, dp4, dp4)
            cardView.layoutParams = params
            cardView.post {
                val measuredWidth = cardView.width
                if (measuredWidth > 0) {
                    val lp = cardView.layoutParams
                    lp.height = measuredWidth
                    cardView.layoutParams = lp
                }
            }
            grid.addView(cardView)
        }

        stickyCards.removeAll { it !== binding.hourlyForecastContainer && it !== binding.dailyForecastCard }
        for (i in 0 until grid.childCount) {
            val card = grid.getChildAt(i) as? StickyHeaderCardLayout
            if (card != null) {
                card.stickyHeaderCount = 1
                card.setWeatherCondition(weatherTag)
                stickyCards.add(card)
            }
        }
        applyStickyHeaderPreference()

        grid.post {
            updateStickyHeaders(binding.nsvWeatherDetails.scrollY)
        }
    }

    private fun iconToWmoCode(icon: String): Int {
        val base = icon.removeSuffix("_d").removeSuffix("_n")
        return when (base) {
            "clear" -> 0
            "mainly_clear" -> 1
            "partly_cloudy" -> 2
            "overcast" -> 3
            "fog" -> 45
            "light_drizzle" -> 51
            "drizzle" -> 53
            "freezing_drizzle" -> 56
            "light_rain" -> 61
            "rain" -> 63
            "heavy_rain" -> 65
            "sleet" -> 66
            "light_snow" -> 71
            "snow" -> 73
            "heavy_snow" -> 75
            "thunderstorm" -> 95
            else -> 2
        }
    }

    private fun updateStickyHeaders(scrollY: Int) {
        val scrollViewLoc = IntArray(2)
        binding.nsvWeatherDetails.getLocationOnScreen(scrollViewLoc)

        var topPinnedCard: StickyHeaderCardLayout? = null
        var topPinnedHeaderView: View? = null

        for (card in stickyCards) {
            if (card.height == 0) continue
            val cardLoc = IntArray(2)
            card.getLocationOnScreen(cardLoc)
            val cardTopOnScreen = cardLoc[1] - scrollViewLoc[1]
            val headerHeight = card.getHeaderLayoutBottom()
            val maxPin = (card.height - headerHeight).toFloat().coerceAtLeast(0f)
            val offset = (-cardTopOnScreen.toFloat()).coerceIn(0f, maxPin)
            card.pinOffset = offset

            if (offset > 0f && card.childCount > 0) {
                topPinnedCard = card
                topPinnedHeaderView = card.getChildAt(0)
            }
        }

        if (topPinnedHeaderView != null && currentWeatherTag.isNotEmpty()) {
            lottieEffectManager.onHeaderPinned(topPinnedHeaderView, currentWeatherTag)
        } else {
            lottieEffectManager.onHeaderUnpinned()
        }
    }

}

package com.example.weather_app2.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
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
import com.example.weather_app2.utils.AdManager
import com.google.android.gms.ads.AdView
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
    private var bannerAdView: AdView? = null
    private var currentWeatherTag: String = ""
    private var initialLocationDone = false

    private fun isMyLocationMode(): Boolean {
        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        return prefs.getBoolean("is_my_location", true)
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        binding = ActivityWeatherForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val statusBarHeight = run {
            val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resId > 0) resources.getDimensionPixelSize(resId) else 0
        }
        binding.weatherEffectsView.statusBarInset = statusBarHeight
        binding.tvCity.post {
            val startSet = binding.motionContainer.getConstraintSet(R.id.start)
            val endSet = binding.motionContainer.getConstraintSet(R.id.swipeDataEnd)
            val existingMargin = (32 * resources.displayMetrics.density).toInt()
            startSet.setMargin(R.id.tvCity, androidx.constraintlayout.widget.ConstraintSet.TOP, existingMargin + statusBarHeight)
            val endMargin = (8 * resources.displayMetrics.density).toInt()
            endSet.setMargin(R.id.tvCity, androidx.constraintlayout.widget.ConstraintSet.TOP, endMargin + statusBarHeight)
            binding.motionContainer.updateState()
        }

        setUpRecyclerViews()
        setUpClickListeners()
        initOppoEngine()

        if (isMyLocationMode()) {
            checkPermissions()
            getDeviceLocation()
        } else {
            val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
            val savedLocation = prefs.getString("last_location", null)
            if (savedLocation != null) {
                restoreFromSavedPrefs(prefs, savedLocation)
            } else {
                checkPermissions()
                getDeviceLocation()
            }
        }
        initialLocationDone = true

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
        binding.adCard.stickyHeaderCount = 1
        stickyCards.add(binding.hourlyForecastContainer)
        stickyCards.add(binding.dailyForecastCard)
        stickyCards.add(binding.adCard)

        binding.adContainer.post {
            bannerAdView = AdManager.loadBannerAd(
                context = this,
                container = binding.adContainer
            )
        }

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
        AdManager.resumeAd(bannerAdView)
        if (initialLocationDone) {
            if (isMyLocationMode() && hasLocationPermission()) {
                getDeviceLocation()
            } else {
                lifecycleScope.launch {
                    viewModel.downloadWeatherData()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        oppoRenderer?.onActivityPause()
        AdManager.pauseAd(bannerAdView)
    }

    override fun onDestroy() {
        lottieEffectManager.cleanup()
        oppoRenderer?.onActivityDestroy()
        AdManager.destroyAd(bannerAdView)
        bannerAdView = null
        super.onDestroy()
    }

    private fun initOppoEngine() {
        oppoRenderer = null
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
        val neighborhood = address?.subLocality
        val city = address?.locality
        if (neighborhood != null) {
            val name = if (city != null && neighborhood != city) "$neighborhood, $city" else neighborhood
            viewModel.updateDeviceLocationWithCoordsAndRefresh(name, lat, lon)
        } else {
            lifecycleScope.launch {
                val name = resolveNeighborhoodFromNominatim(lat, lon)
                    ?: when {
                        city != null -> city
                        else -> address?.subAdminArea
                            ?: address?.adminArea
                            ?: String.format("%.2f, %.2f", lat, lon)
                    }
                viewModel.updateDeviceLocationWithCoordsAndRefresh(name, lat, lon)
            }
        }
    }

    private suspend fun resolveNeighborhoodFromNominatim(lat: Double, lon: Double): String? {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val url = "https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lon&format=json&zoom=18&addressdetails=1"
                val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                conn.setRequestProperty("User-Agent", "WeatherApp2/1.0")
                conn.connectTimeout = 3000
                conn.readTimeout = 3000
                val json = conn.inputStream.bufferedReader().readText()
                conn.disconnect()
                val obj = org.json.JSONObject(json)
                val addr = obj.optJSONObject("address") ?: return@withContext null
                val neighbourhood = addr.optString("neighbourhood", "")
                    .ifEmpty { addr.optString("suburb", "") }
                    .ifEmpty { addr.optString("quarter", "") }
                val city = addr.optString("city", "")
                    .ifEmpty { addr.optString("town", "") }
                    .ifEmpty { addr.optString("village", "") }
                    .ifEmpty { addr.optString("municipality", "") }
                when {
                    neighbourhood.isNotEmpty() && city.isNotEmpty() -> "$neighbourhood, $city"
                    neighbourhood.isNotEmpty() -> neighbourhood
                    city.isNotEmpty() -> city
                    else -> null
                }
            } catch (_: Exception) { null }
        }
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
    }

    private fun setUpRecyclerViews(){
        binding.rvHourlyForecast.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private var currentBgResId: Int = 0

    private fun crossfadeBackground(newResId: Int) {
        if (newResId == currentBgResId) {
            binding.motionContainer.setBackgroundResource(newResId)
            return
        }
        val oldDrawable = binding.motionContainer.background?.mutate()?.constantState?.newDrawable()?.mutate()
        currentBgResId = newResId
        if (oldDrawable == null) {
            binding.motionContainer.setBackgroundResource(newResId)
            return
        }
        val newDrawable = ContextCompat.getDrawable(this, newResId)?.mutate() ?: return
        newDrawable.alpha = 0
        val layer = LayerDrawable(arrayOf(oldDrawable, newDrawable))
        binding.motionContainer.background = layer
        ValueAnimator.ofInt(0, 255).apply {
            duration = 250
            interpolator = DecelerateInterpolator()
            addUpdateListener { anim ->
                val v = anim.animatedValue as Int
                newDrawable.alpha = v
                oldDrawable.alpha = 255 - v
                layer.invalidateSelf()
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    binding.motionContainer.setBackgroundResource(newResId)
                }
            })
            start()
        }
    }

    private fun updateBackground(){
        val data = viewModel.currentWeatherData.value ?: return
        val weatherTag = data.weather.firstOrNull()?.icon ?: return
        currentWeatherTag = weatherTag
        val headerColorInt = UiUtils.getHeaderColor(weatherTag)
        val cardBg = UiUtils.getCardBackground(weatherTag)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        binding.apply {
            crossfadeBackground(UiUtils.getWeatherForecastBackground(weatherTag))
            divider.setBackgroundResource(headerColorInt)
            hourlyForecastContainer.setBackgroundResource(cardBg)
            dailyForecastCard.setBackgroundResource(cardBg)
            adCard.setBackgroundResource(cardBg)
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
            val iconTag = viewModel.currentWeatherData.value?.weather?.firstOrNull()?.icon ?: "clear_d"
            tvApiCallTime.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    UiUtils.getHeaderColor(iconTag)
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

            val tvPrecip = rowView.findViewById<TextView>(R.id.tvPrecipChance)
            if (item.pop > 0) {
                tvPrecip.text = "${item.pop}%"
                tvPrecip.visibility = View.VISIBLE
            } else {
                tvPrecip.visibility = View.GONE
            }

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
            .removeSuffix("_dawn").removeSuffix("_morning")
            .removeSuffix("_afternoon").removeSuffix("_sunset")
            .removeSuffix("_evening")
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
            "hail" -> 99
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

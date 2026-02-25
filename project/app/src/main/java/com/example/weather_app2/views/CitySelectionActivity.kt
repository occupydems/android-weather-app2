package com.example.weather_app2.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app2.R
import com.example.weather_app2.adapters.CitySelectionAdapter
import com.example.weather_app2.databinding.ActivityCitySelectionBinding
import com.example.weather_app2.database.entities.CityShortcut
import com.example.weather_app2.utils.DummyWeatherData
import com.example.weather_app2.utils.UiUtils
import com.example.weather_app2.viewmodels.CitySelectionActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CitySelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCitySelectionBinding
    private val viewModel: CitySelectionActivityViewModel by viewModels()

    private var searchJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private lateinit var cityAdapter: CitySelectionAdapter

    private lateinit var searchBar: SearchBarAnimView
    private lateinit var searchOverlay: SearchResultOverlay

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewsSetup()
        setupAnimatedSearchBar()

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val citySelectionListObserver = Observer<MutableList<CityShortcut>> {
            updateStatusBarColor()
            updateRecyclerView()
        }
        viewModel.citySelectionList.observe(this, citySelectionListObserver)
        val errorStatusObserver = Observer<Boolean> { status ->
            if (status) {
                showErrorDialogWindow()
                viewModel.errorStatus.value = false
            }
        }
        viewModel.errorStatus.observe(this, errorStatusObserver)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_out_close)
    }

    private fun recyclerViewsSetup() {
        binding.rvCitySelection.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        cityAdapter = CitySelectionAdapter(
            mutableListOf(),
            itemClickListener = { item ->
                val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
                if (DummyWeatherData.isDummyId(item.id)) {
                    prefs.edit().putBoolean("is_my_location", false).apply()
                    viewModel.updateMainWeatherForecastLocation("${DummyWeatherData.DUMMY_PREFIX}${item.icon}", 0.0, 0.0)
                } else if (item.id == 1000) {
                    prefs.edit().putBoolean("is_my_location", true).apply()
                } else {
                    prefs.edit().putBoolean("is_my_location", false).apply()
                    viewModel.updateMainWeatherForecastLocation(item.cityName, item.latitude, item.longitude)
                }
                finish()
            },
            deleteButtonClickListener = { cityShortcut ->
                viewModel.deleteCityShortCutClickListener(cityShortcut)
            }
        )
        binding.rvCitySelection.adapter = cityAdapter
    }

    private fun setupAnimatedSearchBar() {
        searchBar = binding.searchBarAnim
        searchOverlay = binding.searchResultOverlay

        searchBar.onSearchTextChanged = label@{ query ->
            if (query.length < 2) {
                searchOverlay.showResults(emptyList())
                return@label
            }

            searchRunnable?.let { handler.removeCallbacks(it) }
            searchRunnable = Runnable {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val results = viewModel.searchCities(query)
                        withContext(Dispatchers.Main) {
                            searchOverlay.showResults(results)
                        }
                    } catch (_: Exception) {}
                }
            }
            handler.postDelayed(searchRunnable!!, 300)
        }

        searchBar.onSearchExpanded = {
            searchOverlay.showOverlay()
        }

        searchBar.onSearchDismissed = {
            searchOverlay.hideOverlay()
            searchJob?.cancel()
            searchRunnable?.let { handler.removeCallbacks(it) }
        }

        searchOverlay.onHotCityClicked = { result ->
            viewModel.addCityFromSearchResult(result)
            searchBar.collapse()
        }
    }

    private fun updateStatusBarColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.transparent
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                UiUtils.getCityShortcutBackground(viewModel.citySelectionList.value!!.last().icon)
            )
        )
    }

    private fun updateRecyclerView() {
        cityAdapter.updateData(viewModel.citySelectionList.value!!.toMutableList())
    }

    private fun showErrorDialogWindow() {
        AlertDialog.Builder(this)
            .setTitle("Error message")
            .setMessage(
                applicationContext.getString(
                    R.string.didnt_found_city
                )
            )
            .setPositiveButton("OK") { _, _ -> }
            .create()
            .show()
    }
}

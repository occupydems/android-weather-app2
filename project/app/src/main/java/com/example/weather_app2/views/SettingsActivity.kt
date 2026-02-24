package com.example.weather_app2.views

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.weather_app2.R
import com.example.weather_app2.models.UnitOfMeasurement
import com.example.weather_app2.viewmodels.CitySelectionActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private val viewModel: CitySelectionActivityViewModel by viewModels()

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.coui_zoom_fade_enter, R.anim.coui_push_down_exit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupUnitToggle()
        setupHeaderToggle()
    }

    private fun setupUnitToggle() {
        val tvCelsius = findViewById<TextView>(R.id.tvCelsius)
        val tvFahrenheit = findViewById<TextView>(R.id.tvFahrenheit)
        val clUnitSelection = findViewById<LinearLayout>(R.id.clUnitSelection)

        val unitMode = viewModel.getUnitMode()
        if (unitMode == UnitOfMeasurement.METRIC.value) {
            tvFahrenheit.setTextColor(Color.GRAY)
        } else {
            tvCelsius.setTextColor(Color.GRAY)
        }

        clUnitSelection.setOnClickListener {
            if (viewModel.changeUnitClickListener() == UnitOfMeasurement.METRIC.value) {
                tvFahrenheit.setTextColor(Color.GRAY)
                tvCelsius.setTextColor(Color.WHITE)
            } else {
                tvCelsius.setTextColor(Color.GRAY)
                tvFahrenheit.setTextColor(Color.WHITE)
            }
        }
    }

    private fun setupHeaderToggle() {
        val tvTransparent = findViewById<TextView>(R.id.tvTransparent)
        val tvOpaque = findViewById<TextView>(R.id.tvOpaque)
        val clHeaderToggle = findViewById<LinearLayout>(R.id.clHeaderToggle)

        val opaque = viewModel.getOpaqueHeaders()
        updateHeaderUI(opaque, tvTransparent, tvOpaque)

        clHeaderToggle.setOnClickListener {
            val newOpaque = !viewModel.getOpaqueHeaders()
            viewModel.setOpaqueHeaders(newOpaque)
            updateHeaderUI(newOpaque, tvTransparent, tvOpaque)
        }
    }

    private fun updateHeaderUI(opaque: Boolean, tvTransparent: TextView, tvOpaque: TextView) {
        if (opaque) {
            tvOpaque.setTextColor(Color.WHITE)
            tvTransparent.setTextColor(Color.GRAY)
        } else {
            tvTransparent.setTextColor(Color.WHITE)
            tvOpaque.setTextColor(Color.GRAY)
        }
    }
}

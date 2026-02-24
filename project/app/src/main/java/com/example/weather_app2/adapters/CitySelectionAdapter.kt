package com.example.weather_app2.adapters

import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app2.R
import com.example.weather_app2.databinding.ItemCityShortcutBinding
import com.example.weather_app2.database.entities.CityShortcut
import com.example.weather_app2.engine.CityCardGradients
import com.example.weather_app2.utils.DummyWeatherData
import com.example.weather_app2.utils.UiUtils

class CitySelectionAdapter(
    private var data: MutableList<CityShortcut>,
    private val itemClickListener: (CityShortcut) -> Unit,
    private val deleteButtonClickListener: (CityShortcut) -> Unit
) : RecyclerView.Adapter<CitySelectionAdapter.CityShortcutViewHolder>() {

    class CityShortcutViewHolder(
        val binding: ItemCityShortcutBinding
        ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityShortcutViewHolder {
        return CityShortcutViewHolder(
            ItemCityShortcutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CityShortcutViewHolder,
        position: Int
    ) {
        val reversePosition = data.size - position - 1
        val item = data[reversePosition]
        if (reversePosition == data.size - 1) {
            bindMyLocationItem(holder, reversePosition)
        } else if (DummyWeatherData.isDummyId(item.id)) {
            bindDummyCityItem(holder, reversePosition)
        } else {
            bindCityShortcutItem(holder, reversePosition)
        }
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<CityShortcut>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun getData(): List<CityShortcut> = data.toList()

    private fun bindMyLocationItem(
        holder: CityShortcutViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            mlCityShortcut.getTransition(R.id.transitionCityShortcut).setEnable(false)
            tvLocalTime.text = data[position].cityName
            tvCityName.text = holder.itemView.context.getString(R.string.my_location_header)
            tvCityTemp.text = holder.itemView.context.getString(R.string.temp, data[position].temp)
            tvConditionLabel.text = getConditionLabel(data[position].icon)
            tvHiLo.text = "H:${data[position].highTemp}° L:${data[position].lowTemp}°"
            clCityShortcutHandle.setOnClickListener {
                itemClickListener(data[position])
            }
            applyGradientBackground(this, data[position].icon)
            weatherEffectsCity.setCompactMode(true)
            weatherEffectsCity.setWeatherCondition(data[position].icon)
        }
    }

    private fun bindDummyCityItem(
        holder: CityShortcutViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            mlCityShortcut.getTransition(R.id.transitionCityShortcut).setEnable(false)
            tvCityName.text = data[position].cityName
            tvCityTemp.text = holder.itemView.context.getString(R.string.temp, data[position].temp)
            tvLocalTime.text = "Test"
            tvConditionLabel.text = getConditionLabel(data[position].icon)
            tvHiLo.text = "H:${data[position].highTemp}° L:${data[position].lowTemp}°"
            clCityShortcutHandle.setOnClickListener {
                itemClickListener(data[position])
            }
            applyGradientBackground(this, data[position].icon)
            weatherEffectsCity.setCompactMode(true)
            weatherEffectsCity.setWeatherCondition(data[position].icon)
        }
    }

    private fun bindCityShortcutItem(
        holder: CityShortcutViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            mlCityShortcut.getTransition(R.id.transitionCityShortcut).setEnable(true)
            mlCityShortcut.progress = 0f
            tvCityName.text = data[position].cityName
            tvCityTemp.text = holder.itemView.context.getString(R.string.temp, data[position].temp)
            tvLocalTime.text = data[position].localTime
            tvConditionLabel.text = getConditionLabel(data[position].icon)
            tvHiLo.text = "H:${data[position].highTemp}° L:${data[position].lowTemp}°"
            clCityShortcutHandle.setOnClickListener {
                itemClickListener(data[position])
            }
            btnDelete.setOnClickListener {
                deleteButtonClickListener(data[position])
            }
            applyGradientBackground(this, data[position].icon)
            weatherEffectsCity.setCompactMode(true)
            weatherEffectsCity.setWeatherCondition(data[position].icon)
        }
    }

    private fun applyGradientBackground(binding: ItemCityShortcutBinding, icon: String) {
        val colors = CityCardGradients.getCardGradient(icon)
        val positions = floatArrayOf(0f, 0.5f, 1f)

        val cornerRadiusPx = 16f * binding.mlCityShortcut.resources.displayMetrics.density
        val drawable = PaintDrawable().apply {
            shape = RoundRectShape(
                floatArrayOf(
                    cornerRadiusPx, cornerRadiusPx,
                    cornerRadiusPx, cornerRadiusPx,
                    cornerRadiusPx, cornerRadiusPx,
                    cornerRadiusPx, cornerRadiusPx
                ), null, null
            )
            shaderFactory = object : ShapeDrawable.ShaderFactory() {
                override fun resize(width: Int, height: Int): Shader {
                    return LinearGradient(
                        0f, 0f, width.toFloat(), height.toFloat(),
                        colors, positions,
                        Shader.TileMode.CLAMP
                    )
                }
            }
        }
        binding.mlCityShortcut.background = drawable
        binding.mlCityShortcut.clipToOutline = true
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

    private fun getConditionLabel(icon: String): String {
        return when (icon) {
            "clear_d" -> "Sunny"
            "clear_n" -> "Clear"
            "mainly_clear_d" -> "Mostly Sunny"
            "mainly_clear_n" -> "Mostly Clear"
            "partly_cloudy_d", "partly_cloudy_n" -> "Partly Cloudy"
            "overcast_d", "overcast_n" -> "Overcast"
            "fog_d", "fog_n" -> "Fog"
            "light_drizzle_d", "light_drizzle_n" -> "Light Drizzle"
            "drizzle_d", "drizzle_n" -> "Drizzle"
            "freezing_drizzle_d", "freezing_drizzle_n" -> "Freezing Drizzle"
            "light_rain_d", "light_rain_n" -> "Light Rain"
            "rain_d", "rain_n" -> "Rain"
            "heavy_rain_d", "heavy_rain_n" -> "Heavy Rain"
            "sleet_d", "sleet_n" -> "Sleet"
            "light_snow_d", "light_snow_n" -> "Light Snow"
            "snow_d", "snow_n" -> "Snow"
            "heavy_snow_d", "heavy_snow_n" -> "Heavy Snow"
            "thunderstorm_d", "thunderstorm_n" -> "Thunderstorm"
            else -> "Cloudy"
        }
    }
}

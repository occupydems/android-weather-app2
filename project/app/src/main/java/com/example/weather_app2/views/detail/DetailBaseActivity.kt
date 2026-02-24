package com.example.weather_app2.views.detail

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weather_app2.models.WeatherDetailItem
import java.io.Serializable

open class DetailBaseActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CARD_TYPE = "extra_card_type"
        const val EXTRA_WEATHER_DATA = "extra_weather_data"
    }

    protected var cardType: String = ""
    protected var weatherData: WeatherDetailItem? = null
    protected lateinit var contentLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cardType = intent.getStringExtra(EXTRA_CARD_TYPE) ?: ""
        weatherData = intent.getSerializableExtra(EXTRA_WEATHER_DATA) as? WeatherDetailItem

        window.statusBarColor = Color.parseColor("#1C1C1E")
        window.navigationBarColor = Color.parseColor("#1C1C1E")

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#1C1C1E"))
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(24), dpToPx(48), dpToPx(24), dpToPx(24))
        }

        val backButton = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setBackgroundColor(0x00000000)
            setColorFilter(0xFFFFFFFF.toInt())
            val size = dpToPx(40)
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                bottomMargin = dpToPx(16)
            }
            setOnClickListener { finish() }
        }
        layout.addView(backButton)

        val titleView = TextView(this).apply {
            text = getCardTitle()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(8)
            }
        }
        layout.addView(titleView)

        weatherData?.let { data ->
            val valueView = TextView(this).apply {
                text = data.value
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
                setTextColor(0xFFFFFFFF.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(8)
                }
            }
            layout.addView(valueView)

            if (data.subtitle.isNotEmpty()) {
                val subtitleView = TextView(this).apply {
                    text = data.subtitle
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setTextColor(0x99FFFFFF.toInt())
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = dpToPx(16)
                    }
                }
                layout.addView(subtitleView)
            }
        }

        val divider = View(this).apply {
            setBackgroundColor(0x33FFFFFF)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(1)
            ).apply {
                topMargin = dpToPx(8)
                bottomMargin = dpToPx(16)
            }
        }
        layout.addView(divider)

        contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        layout.addView(contentLayout)

        buildDetailContent()

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    protected open fun getCardTitle(): String {
        return cardType.ifEmpty { "Detail" }
    }

    protected open fun buildDetailContent() {}

    protected fun addInfoCard(title: String, description: String) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val bg = GradientDrawable().apply {
                setColor(Color.parseColor("#2C2C2E"))
                cornerRadius = dpToPx(12).toFloat()
            }
            background = bg
            setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(12)
            }
        }

        val titleText = TextView(this).apply {
            text = title
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(0x99FFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(6)
            }
        }
        card.addView(titleText)

        val descText = TextView(this).apply {
            text = description
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(0xFFFFFFFF.toInt())
            lineHeight = dpToPx(22)
        }
        card.addView(descText)

        contentLayout.addView(card)
    }

    protected fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

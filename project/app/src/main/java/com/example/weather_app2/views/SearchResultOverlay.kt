package com.example.weather_app2.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app2.adapters.SearchResultAdapter
import com.example.weather_app2.models.GeoSearchResult

class SearchResultOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mainContainer: LinearLayout
    private val hotCitiesSection: LinearLayout
    private val searchResultsRecyclerView: RecyclerView
    lateinit var searchAdapter: SearchResultAdapter
        private set

    var onHotCityClicked: ((GeoSearchResult) -> Unit)? = null

    private val hotCities = listOf(
        GeoSearchResult("New York", "New York", "United States", 40.7128, -74.0060),
        GeoSearchResult("London", "England", "United Kingdom", 51.5074, -0.1278),
        GeoSearchResult("Tokyo", "Tokyo", "Japan", 35.6762, 139.6503),
        GeoSearchResult("Paris", "Île-de-France", "France", 48.8566, 2.3522),
        GeoSearchResult("Sydney", "New South Wales", "Australia", -33.8688, 151.2093),
        GeoSearchResult("Beijing", null, "China", 39.9042, 116.4074),
        GeoSearchResult("Dubai", "Dubai", "United Arab Emirates", 25.2048, 55.2708),
        GeoSearchResult("Mumbai", "Maharashtra", "India", 19.0760, 72.8777),
        GeoSearchResult("São Paulo", "São Paulo", "Brazil", -23.5505, -46.6333),
        GeoSearchResult("Lagos", "Lagos", "Nigeria", 6.5244, 3.3792)
    )

    init {
        setBackgroundColor(Color.parseColor("#E5000000"))
        visibility = View.GONE

        val scrollView = ScrollView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            isVerticalScrollBarEnabled = false
        }

        mainContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dpToPx(8f).toInt(), 0, dpToPx(16f).toInt())
        }

        hotCitiesSection = createHotCitiesSection()
        mainContainer.addView(hotCitiesSection)

        searchResultsRecyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }

        searchAdapter = SearchResultAdapter { result ->
            onHotCityClicked?.invoke(result)
        }
        searchResultsRecyclerView.adapter = searchAdapter

        mainContainer.addView(searchResultsRecyclerView)
        scrollView.addView(mainContainer)
        addView(scrollView)
    }

    private fun createHotCitiesSection(): LinearLayout {
        val section = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16f).toInt(), dpToPx(8f).toInt(), dpToPx(16f).toInt(), dpToPx(8f).toInt())
        }

        val title = TextView(context).apply {
            text = "Popular Cities"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, dpToPx(8f).toInt())
        }
        section.addView(title)

        val grid = GridLayout(context).apply {
            columnCount = 2
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        for (city in hotCities) {
            val dp4 = dpToPx(4f).toInt()
            val dp12 = dpToPx(12f).toInt()

            val bg = GradientDrawable().apply {
                setColor(Color.parseColor("#33FFFFFF"))
                cornerRadius = dpToPx(12f)
            }

            val chip = TextView(context).apply {
                text = city.name
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                gravity = Gravity.CENTER
                background = bg
                setPadding(dp12, dpToPx(10f).toInt(), dp12, dpToPx(10f).toInt())
                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(dp4, dp4, dp4, dp4)
                }
                layoutParams = params
                setOnClickListener {
                    onHotCityClicked?.invoke(city)
                }
            }
            grid.addView(chip)
        }

        section.addView(grid)
        return section
    }

    fun showOverlay() {
        visibility = View.VISIBLE
        hotCitiesSection.visibility = View.VISIBLE
    }

    fun hideOverlay() {
        visibility = View.GONE
        searchAdapter.clear()
    }

    fun showResults(results: List<GeoSearchResult>) {
        if (results.isNotEmpty()) {
            hotCitiesSection.visibility = View.GONE
        } else {
            hotCitiesSection.visibility = View.VISIBLE
        }
        searchAdapter.updateResults(results)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }
}

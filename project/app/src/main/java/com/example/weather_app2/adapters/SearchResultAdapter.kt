package com.example.weather_app2.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app2.models.GeoSearchResult

class SearchResultAdapter(
    private val onItemClick: (GeoSearchResult) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private val items = mutableListOf<GeoSearchResult>()

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dp12 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 12f, parent.resources.displayMetrics
        ).toInt()
        val dp16 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 16f, parent.resources.displayMetrics
        ).toInt()
        val dp4 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 4f, parent.resources.displayMetrics
        ).toInt()

        val bg = GradientDrawable().apply {
            setColor(Color.parseColor("#F2F2F7"))
            cornerRadius = dp12.toFloat()
        }

        val tv = TextView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dp16, dp4, dp16, dp4)
            }
            setPadding(dp16, dp12, dp16, dp12)
            background = bg
            setTextColor(Color.parseColor("#1C1C1E"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.displayString()
        holder.textView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateResults(newItems: List<GeoSearchResult>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }
}

package com.example.weather_app2.views

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class SearchBarAnimView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val collapsedContainer: LinearLayout
    private val expandedContainer: LinearLayout
    val searchEditText: EditText
    private val cancelButton: TextView
    private val searchIcon: ImageView
    private val searchHintText: TextView
    private val settingsIcon: ImageView

    var isExpanded = false
        private set

    var onSearchTextChanged: ((String) -> Unit)? = null
    var onSearchDismissed: (() -> Unit)? = null
    var onSearchExpanded: (() -> Unit)? = null

    private val bgColor = Color.parseColor("#F2F2F7")
    private val cornerRadius = dpToPx(24f)
    private val animDuration = 250L

    init {
        collapsedContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val bg = GradientDrawable().apply {
                setColor(bgColor)
                this.cornerRadius = this@SearchBarAnimView.cornerRadius
            }
            background = bg
            setPadding(dpToPx(12f).toInt(), dpToPx(8f).toInt(), dpToPx(12f).toInt(), dpToPx(8f).toInt())
        }

        searchIcon = ImageView(context).apply {
            setImageResource(android.R.drawable.ic_menu_search)
            setColorFilter(Color.parseColor("#8E8E93"))
            val iconSize = dpToPx(20f).toInt()
            layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                marginEnd = dpToPx(8f).toInt()
            }
        }

        searchHintText = TextView(context).apply {
            text = "Search"
            setTextColor(Color.parseColor("#8E8E93"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        settingsIcon = ImageView(context).apply {
            setImageResource(android.R.drawable.ic_menu_manage)
            setColorFilter(Color.WHITE)
            val iconSize = dpToPx(24f).toInt()
            layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                marginStart = dpToPx(8f).toInt()
            }
            setOnClickListener {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }
        }

        collapsedContainer.addView(searchIcon)
        collapsedContainer.addView(searchHintText)
        collapsedContainer.addView(settingsIcon)

        expandedContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            visibility = View.GONE
            alpha = 0f
        }

        val editTextContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val bg = GradientDrawable().apply {
                setColor(bgColor)
                this.cornerRadius = this@SearchBarAnimView.cornerRadius
            }
            background = bg
            setPadding(dpToPx(12f).toInt(), dpToPx(8f).toInt(), dpToPx(12f).toInt(), dpToPx(8f).toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val expandedSearchIcon = ImageView(context).apply {
            setImageResource(android.R.drawable.ic_menu_search)
            setColorFilter(Color.parseColor("#8E8E93"))
            val iconSize = dpToPx(20f).toInt()
            layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                marginEnd = dpToPx(8f).toInt()
            }
        }

        searchEditText = EditText(context).apply {
            hint = "Search city"
            setHintTextColor(Color.parseColor("#8E8E93"))
            setTextColor(Color.parseColor("#1C1C1E"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            background = null
            isSingleLine = true
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        editTextContainer.addView(expandedSearchIcon)
        editTextContainer.addView(searchEditText)

        cancelButton = TextView(context).apply {
            text = "Cancel"
            setTextColor(Color.parseColor("#007AFF"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = dpToPx(12f).toInt()
            }
            setOnClickListener { collapse() }
        }

        expandedContainer.addView(editTextContainer)
        expandedContainer.addView(cancelButton)

        addView(collapsedContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(expandedContainer, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        collapsedContainer.setOnClickListener { expand() }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                onSearchTextChanged?.invoke(s?.toString() ?: "")
            }
        })
    }

    fun expand() {
        if (isExpanded) return
        isExpanded = true

        collapsedContainer.animate()
            .alpha(0f)
            .setDuration(animDuration / 2)
            .withEndAction {
                collapsedContainer.visibility = View.GONE
            }
            .start()

        expandedContainer.visibility = View.VISIBLE
        expandedContainer.animate()
            .alpha(1f)
            .setDuration(animDuration)
            .setInterpolator(DecelerateInterpolator())
            .start()

        searchEditText.requestFocus()
        post {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        onSearchExpanded?.invoke()
    }

    fun collapse() {
        if (!isExpanded) return
        isExpanded = false

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

        searchEditText.setText("")
        searchEditText.clearFocus()

        expandedContainer.animate()
            .alpha(0f)
            .setDuration(animDuration / 2)
            .withEndAction {
                expandedContainer.visibility = View.GONE
            }
            .start()

        collapsedContainer.visibility = View.VISIBLE
        collapsedContainer.alpha = 0f
        collapsedContainer.animate()
            .alpha(1f)
            .setDuration(animDuration)
            .setInterpolator(DecelerateInterpolator())
            .start()

        onSearchDismissed?.invoke()
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }
}

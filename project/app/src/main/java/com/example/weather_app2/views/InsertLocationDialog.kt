package com.example.weather_app2.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.weather_app2.databinding.DialogInsertLocationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InsertLocationDialog(
    private val dialogMessage: String,
    private val btnSubmitClickListener: (String) -> Unit,
    private val citySearchProvider: (suspend (String) -> List<String>)? = null
): DialogFragment() {
    lateinit var binding: DialogInsertLocationBinding
    private var searchJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var isSettingText = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogInsertLocationBinding.inflate(inflater, container, false)
        binding.apply {
            tvInsertLocationDialogDescription.text = dialogMessage
            btnSubmitLocality.setOnClickListener {
                val locality = etInsertLocality.text.toString()
                btnSubmitClickListener(locality)
                dismiss()
            }
            btnCalncelLocationInsert.setOnClickListener {
                dismiss()
            }

            if (citySearchProvider != null) {
                val adapter = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    mutableListOf()
                )
                etInsertLocality.setAdapter(adapter)

                etInsertLocality.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, _, position, _ ->
                        val selected = parent.getItemAtPosition(position) as String
                        val cityName = selected.split(",").first().trim()
                        isSettingText = true
                        etInsertLocality.setText(cityName)
                        etInsertLocality.setSelection(cityName.length)
                        isSettingText = false
                        btnSubmitClickListener(selected)
                        dismiss()
                    }

                etInsertLocality.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if (isSettingText) return
                        val query = s?.toString() ?: return
                        if (query.length < 2) return

                        searchRunnable?.let { handler.removeCallbacks(it) }
                        searchRunnable = Runnable {
                            searchJob?.cancel()
                            searchJob = CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val suggestions = citySearchProvider.invoke(query)
                                    withContext(Dispatchers.Main) {
                                        adapter.clear()
                                        adapter.addAll(suggestions)
                                        adapter.notifyDataSetChanged()
                                        if (suggestions.isNotEmpty() && etInsertLocality.hasFocus()) {
                                            etInsertLocality.showDropDown()
                                        }
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                        handler.postDelayed(searchRunnable!!, 300)
                    }
                })
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }
}
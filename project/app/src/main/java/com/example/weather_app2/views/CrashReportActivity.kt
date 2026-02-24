package com.example.weather_app2.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.weather_app2.utils.CrashLogManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashReportActivity : AppCompatActivity() {

    private var crashText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crashText = CrashLogManager.readPendingCrash(this) ?: "No crash data found."

        val density = resources.displayMetrics.density
        val pad = (16 * density).toInt()
        val padSmall = (8 * density).toInt()

        val scrollView = ScrollView(this).apply {
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(0xFF1A1A2E.toInt())
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
        }

        val titleView = TextView(this).apply {
            text = "App Crashed"
            setTextColor(0xFFFF6B6B.toInt())
            textSize = 22f
            setPadding(0, 0, 0, padSmall)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

        val subtitleView = TextView(this).apply {
            text = "A crash log has been captured. You can share it or save it to your device."
            setTextColor(0xFFCCCCCC.toInt())
            textSize = 14f
            setPadding(0, 0, 0, pad)
        }

        val buttonContainer = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, pad)
        }

        val shareButton = Button(this).apply {
            text = "Share Log"
            setOnClickListener { shareCrashLog() }
        }

        val saveButton = Button(this).apply {
            text = "Save to Downloads"
            setOnClickListener { saveCrashLogToDownloads() }
        }

        val dismissButton = Button(this).apply {
            text = "Dismiss"
            setOnClickListener {
                CrashLogManager.clearPendingCrash(this@CrashReportActivity)
                finishAndRemoveTask()
            }
        }

        val logPreviewLabel = TextView(this).apply {
            text = "Crash Log Preview:"
            setTextColor(0xFFAAAAAA.toInt())
            textSize = 12f
            setPadding(0, padSmall, 0, padSmall)
        }

        val logView = TextView(this).apply {
            val preview = if (crashText.length > 8000) crashText.take(8000) + "\n\n[Truncated — use Share or Save for full log]" else crashText
            text = preview
            setTextColor(0xFF88FF88.toInt())
            textSize = 10f
            setTypeface(android.graphics.Typeface.MONOSPACE)
            setTextIsSelectable(true)
            setBackgroundColor(0xFF0D0D1A.toInt())
            setPadding(padSmall, padSmall, padSmall, padSmall)
        }

        val lp = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginEnd = padSmall
        }

        buttonContainer.addView(shareButton, lp)
        buttonContainer.addView(saveButton, lp)
        buttonContainer.addView(dismissButton, android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        container.addView(titleView)
        container.addView(subtitleView)
        container.addView(buttonContainer)
        container.addView(logPreviewLabel)
        container.addView(logView)

        scrollView.addView(container)
        setContentView(scrollView)
    }

    private fun shareCrashLog() {
        try {
            val file = writeTempCrashFile()
            val uri = FileProvider.getUriForFile(this, "$packageName.crashprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Weather App Crash Log")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share crash log"))
        } catch (e: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, crashText)
                putExtra(Intent.EXTRA_SUBJECT, "Weather App Crash Log")
            }
            startActivity(Intent.createChooser(fallbackIntent, "Share crash log"))
        }
    }

    private fun saveCrashLogToDownloads() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val filename = "crash_log_$timestamp.txt"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsDir.mkdirs()
            val outFile = File(downloadsDir, filename)
            outFile.writeText(crashText)
            Toast.makeText(this, "Saved to Downloads/$filename", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val filename = "crash_log_$timestamp.txt"
                val outFile = File(getExternalFilesDir(null), filename)
                outFile.writeText(crashText)
                Toast.makeText(this, "Saved to ${outFile.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e2: Exception) {
                Toast.makeText(this, "Could not save: ${e2.message}. Use Share instead.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun writeTempCrashFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val dir = File(cacheDir, "crash_share")
        dir.mkdirs()
        val file = File(dir, "crash_log_$timestamp.txt")
        file.writeText(crashText)
        return file
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CrashLogManager.clearPendingCrash(this)
        finishAndRemoveTask()
    }
}

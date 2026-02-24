package com.example.weather_app2.utils

import android.content.Context
import android.content.Intent
import android.os.Process
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CrashLogManager {

    private const val CRASH_DIR = "crash_logs"
    private const val PENDING_CRASH = "pending_crash.txt"

    fun install(context: Context) {
        val appContext = context.applicationContext
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val crashReport = buildCrashReport(thread, throwable)
                savePendingCrash(appContext, crashReport)
                launchCrashReporter(appContext)
            } catch (_: Throwable) {
            }

            try {
                Thread.sleep(500)
            } catch (_: Throwable) {
            }

            defaultHandler?.uncaughtException(thread, throwable)
                ?: Process.killProcess(Process.myPid())
        }
    }

    private fun launchCrashReporter(context: Context) {
        try {
            val intent = Intent(context, Class.forName("com.example.weather_app2.views.CrashReportActivity"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            context.startActivity(intent)
        } catch (_: Throwable) {
        }
    }

    private fun buildCrashReport(thread: Thread, throwable: Throwable): String {
        val sb = StringBuilder()
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())

        sb.appendLine("=== CRASH REPORT ===")
        sb.appendLine("Timestamp: $timestamp")
        sb.appendLine("Thread: ${thread.name} (id=${thread.id})")
        sb.appendLine()

        sb.appendLine("=== EXCEPTION ===")
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        sb.appendLine(sw.toString())

        sb.appendLine("=== LOGCAT (last 500 lines) ===")
        try {
            val process = Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-t", "500"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.appendLine(line)
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            sb.appendLine("[Failed to capture logcat: ${e.message}]")
        }

        sb.appendLine()
        sb.appendLine("=== DEVICE INFO ===")
        sb.appendLine("Manufacturer: ${android.os.Build.MANUFACTURER}")
        sb.appendLine("Model: ${android.os.Build.MODEL}")
        sb.appendLine("Android: ${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})")
        sb.appendLine("ABI: ${android.os.Build.SUPPORTED_ABIS.joinToString()}")

        val runtime = Runtime.getRuntime()
        val usedMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val totalMB = runtime.totalMemory() / (1024 * 1024)
        val maxMB = runtime.maxMemory() / (1024 * 1024)
        sb.appendLine("Memory: ${usedMB}MB used / ${totalMB}MB total / ${maxMB}MB max")

        return sb.toString()
    }

    private fun savePendingCrash(context: Context, report: String) {
        try {
            val dir = File(context.filesDir, CRASH_DIR)
            dir.mkdirs()
            File(dir, PENDING_CRASH).writeText(report)
        } catch (_: Throwable) {
        }
    }

    fun hasPendingCrash(context: Context): Boolean {
        return getPendingCrashFile(context).exists()
    }

    fun readPendingCrash(context: Context): String? {
        val file = getPendingCrashFile(context)
        return if (file.exists()) file.readText() else null
    }

    fun clearPendingCrash(context: Context) {
        getPendingCrashFile(context).delete()
    }

    private fun getPendingCrashFile(context: Context): File {
        return File(File(context.filesDir, CRASH_DIR), PENDING_CRASH)
    }
}

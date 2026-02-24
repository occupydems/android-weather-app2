package com.example.weather_app2.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

object AdManager {
    private const val TAG = "AdManager"

    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"

    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        MobileAds.initialize(context) { initStatus ->
            Log.d(TAG, "Mobile Ads SDK initialized: $initStatus")
        }
    }

    fun loadBannerAd(
        context: Context,
        container: ViewGroup,
        adUnitId: String = TEST_BANNER_AD_UNIT_ID,
        onAdLoaded: (() -> Unit)? = null,
        onAdFailed: ((String) -> Unit)? = null
    ): AdView {
        val adView = AdView(context)
        adView.adUnitId = adUnitId

        val displayMetrics = context.resources.displayMetrics
        val adWidthPixels = container.width.let { w ->
            if (w > 0) w else displayMetrics.widthPixels
        }
        val adWidthDp = (adWidthPixels / displayMetrics.density).toInt()
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthDp))

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Banner ad loaded")
                onAdLoaded?.invoke()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Banner ad failed to load: ${error.message}")
                onAdFailed?.invoke(error.message)
            }
        }

        container.removeAllViews()
        container.addView(adView, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        return adView
    }

    fun destroyAd(adView: AdView?) {
        adView?.destroy()
    }

    fun pauseAd(adView: AdView?) {
        adView?.pause()
    }

    fun resumeAd(adView: AdView?) {
        adView?.resume()
    }
}

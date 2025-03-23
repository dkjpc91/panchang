package com.mithilakshar.mithilapanchang.Utility

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mithilakshar.mithilapanchang.R

class InterstitialAdManager(
    private val context: Context,
    adUnitId: String
) {

    private var interstitialAd: InterstitialAd? = null

    // AdMob test ID, replace with real ad unit ID for production
    private val adUnitId = adUnitId

    // Load the interstitial ad
     fun loadAd(onFailureCallback: () -> Unit) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.d("InterstitialAdManager", "Ad loaded successfully")
                setFullScreenContentCallback(onFailureCallback)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
                Log.d("InterstitialAdManager", "Ad failed to load: ${adError.message}")
                onFailureCallback() // Move to the next task when ad fails to load
            }
        })
    }

    // Method to either show the ad or invoke a failure callback
    fun showAd(activity: Activity, onFailureCallback: () -> Unit) {
        if (interstitialAd != null) {
            // If the ad is loaded, show it
            interstitialAd?.show(activity)
        } else {
            // Try loading the ad, if failed, invoke the failure callback
            loadAd(onFailureCallback)
        }
    }

    // Set callback for when the ad is dismissed or failed to show
    private fun setFullScreenContentCallback(onFailureCallback: () -> Unit) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("InterstitialAdManager", "Ad dismissed, loading a new one")
                loadAd(onFailureCallback) // Load a new ad after the current one is dismissed
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("InterstitialAdManager", "Ad failed to show: ${adError.message}")
                interstitialAd = null
                onFailureCallback() // Invoke failure callback if ad fails to show
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null // Reset the interstitial ad after it is shown
            }
        }
    }
}

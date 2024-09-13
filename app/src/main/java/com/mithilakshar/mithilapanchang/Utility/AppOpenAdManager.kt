package com.mithilakshar.mithilapanchang.Utility
import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.FullScreenContentCallback

object AppOpenAdManager : Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isAdShowing = false
    private var isAdLoading = false

    fun loadAd(application: Application) {
        if (isAdLoading || appOpenAd != null) {
            return // Ad is already loading or loaded
        }

        isAdLoading = true

        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            application,
            "ca-app-pub-3940256099942544/9257395921", // Replace with your actual ad unit ID
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isAdLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                    isAdLoading = false
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity, onAdDismissed: () -> Unit) {
        if (appOpenAd != null && !isAdShowing) {
            isAdShowing = true
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    isAdShowing = false
                    onAdDismissed() // Call the callback after the ad is dismissed
                    appOpenAd = null
                    loadAd(activity.application) // Preload the next ad
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    isAdShowing = false
                    onAdDismissed() // Call the callback if the ad fails to show
                }

                override fun onAdShowedFullScreenContent() {
                    appOpenAd = null
                }
            }
            appOpenAd?.show(activity)
        } else {
            onAdDismissed() // Proceed to the next screen if the ad is not available
            loadAd(activity.application) // Optionally preload the next ad
        }
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}
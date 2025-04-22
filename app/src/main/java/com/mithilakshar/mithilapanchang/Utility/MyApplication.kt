package com.mithilakshar.mithilapanchang.Utility



import android.app.Application
import com.google.android.gms.ads.MobileAds

import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mithilakshar.mithilapanchang.BuildConfig


import com.mithilakshar.mithilapanchang.Repository.RingtoneRepository
import com.mithilakshar.mithilapanchang.Room.RingtoneDatabase

import android.util.Log
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class MyApplication: Application() {



    private lateinit var mFirebaseAnalytics: FirebaseAnalytics




    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { RingtoneDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { RingtoneRepository(database.ringtonedao()) }
    private val os = com.mithilakshar.mithilapanchang.BuildConfig.os

    override fun onCreate() {
        super.onCreate()

/*        OneSignal.Debug.logLevel = LogLevel.VERBOSE*/
        // Initialize with your OneSignal App ID
        OneSignal.initWithContext(this, os)
        // Use this method to prompt for push notifications.
        // We recommend removing this method after testing and instead use In-App Messages to prompt for notification permission.
/*
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
*/



        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        FirebaseApp.initializeApp(this)



        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = settings

        MobileAds.initialize(this) { initializationStatus ->
            // Log or handle initialization status if needed
        }

        AppOpenAdManager.loadAd(this)
        registerActivityLifecycleCallbacks(AppOpenAdManager)

        if (BuildConfig.FIREBASE_ANALYTICS_ENABLED) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
            // Now you can log events and use Firebase Analytics as needed
        } else {
            // Optionally log a message or take alternative action
            // Firebase Analytics is disabled in debug mode
        }




    }



}











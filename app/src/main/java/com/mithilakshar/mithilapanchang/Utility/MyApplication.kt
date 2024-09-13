package com.mithilakshar.mithilapanchang.Utility


import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


import com.mithilakshar.mithilapanchang.Repository.RingtoneRepository
import com.mithilakshar.mithilapanchang.Room.RingtoneDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class MyApplication: Application() {



   private lateinit var analytics: FirebaseAnalytics




    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { RingtoneDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { RingtoneRepository(database.ringtonedao()) }


    override fun onCreate() {
        super.onCreate()

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
      analytics = Firebase.analytics

    }



}











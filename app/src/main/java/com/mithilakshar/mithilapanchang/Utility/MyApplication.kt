package com.mithilakshar.mithilapanchang.Utility


import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics


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
      analytics = Firebase.analytics

        FirebaseApp.initializeApp(this)
    }



}











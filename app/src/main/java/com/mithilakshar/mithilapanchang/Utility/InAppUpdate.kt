package com.mithilakshar.mithilapanchang.Utility

import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import android.content.Intent
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import android.app.Activity


class InAppUpdate(private val activity: Activity, private val requestCode: Int) {

    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(activity.applicationContext)
    }

    fun checkForAppUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                startAppUpdate(appUpdateInfo)
            }
        }
    }

    private fun startAppUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            activity,
            requestCode
        )
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == this.requestCode) {
            if (resultCode != Activity.RESULT_OK) {
                // If the update is cancelled or fails, you can retry or handle it accordingly.
            }
        }
    }
}
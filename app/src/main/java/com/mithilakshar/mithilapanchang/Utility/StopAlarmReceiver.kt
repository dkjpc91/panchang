package com.mithilakshar.mithilapanchang.Utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "STOP_AUDIO_ACTION") {
            // Stop and release MediaPlayer instance here
            NotificationHelper.mediaPlayer?.stop()
            NotificationHelper.mediaPlayer?.release()
            NotificationHelper.mediaPlayer = null
        }
    }
}
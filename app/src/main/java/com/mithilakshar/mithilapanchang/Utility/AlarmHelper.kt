package com.mithilakshar.mithilapanchang.Utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlin.math.absoluteValue
import java.util.Calendar

class AlarmHelper(val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm(calendar: Calendar, title: String, message: String, selectedRingtone: Int, imageUrl: String) {
        val futureInMillis = calendar.timeInMillis
        val intent = createAlarmIntent(title, message, selectedRingtone, imageUrl)
        val requestCode = (title.hashCode() + message.hashCode() + selectedRingtone + imageUrl.hashCode()).absoluteValue

        // Handle backward compatibility for PendingIntent flags
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        // Handle backward compatibility for AlarmManager APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent)
        }
    }

    fun cancelAlarm(title: String, message: String, selectedRingtone: Int, imageUrl: String) {
        val intent = createAlarmIntent(title, message, selectedRingtone, imageUrl)
        val requestCode = (title.hashCode() + message.hashCode() + selectedRingtone + imageUrl.hashCode()).absoluteValue

        // Handle backward compatibility for PendingIntent flags
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_NO_CREATE
            )
        }

        pendingIntent?.let { alarmManager.cancel(it) }
    }

    private fun createAlarmIntent(title: String, message: String, selectedRingtone: Int, imageUrl: String): Intent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "com.example.app.ALARM_ACTION" // Replace with your desired action
        intent.putExtra("title", title)
        intent.putExtra("imageUrl", imageUrl)
        intent.putExtra("selectedRingtone", selectedRingtone)
        intent.putExtra("message", message)
        return intent
    }
}

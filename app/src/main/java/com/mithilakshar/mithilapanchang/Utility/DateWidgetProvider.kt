package com.mithilakshar.mithilapanchang.Utility

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.mithilakshar.mithilapanchang.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import com.mithilakshar.mithilapanchang.UI.View.HomeActivity
import java.time.LocalDate

class DateWidgetProvider : AppWidgetProvider() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context,
        appWidgetManager, appWidgetIds)


        // Update all widgets
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        // Set up periodic updates
        setUpdateAlarm(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName,
        R.layout.date_widget)

        // Get current time


        //date

        val currentDate = LocalDate.now()
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = dateFormat.format(Date())

        // Separate hours and minutes
        val timeParts = currentTime.split(' ')
        val hoursMinutes = timeParts[0].split(':')
        val hours = hoursMinutes[0]
        val minutes = hoursMinutes[1]
        val amPm = timeParts[1]

        Log.d("DateWidgetProvider", "Current time: $currentTime")
        val hindiMonth = TranslationUtils.translateToHindimonth(currentDate.month.toString())
        val hindiDay = TranslationUtils.translateToHindiday(currentDate.dayOfWeek.toString())
        val hindidate = TranslationUtils.translateToHindidate(currentDate.dayOfMonth.toString())
        val hindiyear = TranslationUtils.translateToHindidate(currentDate.year.toString())
        val hindihour = TranslationUtils.translateToHindidate(hours.toString())
        val hindiminutes= TranslationUtils.translateToHindidate(minutes.toString())
        val ampmhindi= TranslationUtils.translateToHindiday(amPm)

        val widgetText = "$hindiDay,$hindidate $hindiMonth $hindiyear $ampmhindi  $hindihour : $hindiminutes "

        views.setTextViewText(R.id.widgettext, widgetText)

        val clickIntent = Intent(context, HomeActivity::class.java)
        val clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        views.setOnClickPendingIntent(R.id.widgetRoot, clickPendingIntent)

        // Update the widget.
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setUpdateAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DateWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, DateWidgetProvider::class.java)
            ))
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val intervalMillis = 10000L // Update every 10 seconds
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), intervalMillis, pendingIntent)

        Log.d("DateWidgetProvider", "Alarm set for every 60 seconds")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Log the intent action
        Log.d("DateWidgetProvider", "onReceive called with action: ${intent.action}")

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
            Log.d("DateWidgetProvider", "Updating widget(s)")

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

            if (appWidgetIds == null) {
                Log.d("DateWidgetProvider", "Widget IDs are null")
            } else if (appWidgetIds.isEmpty()) {
                Log.d("DateWidgetProvider", "No widget IDs found")
            } else {
                Log.d("DateWidgetProvider", "Widget IDs: ${appWidgetIds.joinToString(", ")}")
                appWidgetIds.forEach { appWidgetId ->
                    Log.d("DateWidgetProvider", "Updating widget with ID: $appWidgetId")
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
        } else {
            Log.d("DateWidgetProvider", "Unhandled action: ${intent.action}")
        }
    }




}

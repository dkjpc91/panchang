    package com.mithilakshar.mithilapanchang.Utility

    import android.app.AlarmManager
    import android.app.PendingIntent
    import android.content.Context
    import android.content.Intent
    import android.content.SharedPreferences
    import android.widget.Toast
    import java.util.Calendar
    import kotlin.math.absoluteValue


    class AlarmHelper(val context: Context) {

        private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        fun setAlarm(calendar: Calendar,  title: String, message: String,selectedRingtone: Int,imageUrl:String) {
            val futureInMillis = calendar.timeInMillis
            val intent = createAlarmIntent(title,message,selectedRingtone,imageUrl)
            val requestCode = (title.hashCode() + message.hashCode() + selectedRingtone + imageUrl.hashCode()).absoluteValue
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode , intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent)
        }


            fun cancelAlarm(title: String, message: String, selectedRingtone: Int,imageUrl:String) {

                val intent = createAlarmIntent(title, message, selectedRingtone,imageUrl)
                val requestCode = (title.hashCode() + message.hashCode() + selectedRingtone + imageUrl.hashCode()).absoluteValue
                val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
                pendingIntent?.let { alarmManager.cancel(it) } // Only cancel if pendingIntent exists
            }



        private fun createAlarmIntent(title: String, message: String,selectedRingtone: Int,imageUrl:String): Intent {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.action = "com.example.app.ALARM_ACTION" // Replace with your desired action
            intent.putExtra("title", title)
            intent.putExtra("imageUrl", imageUrl)
            intent.putExtra("selectedRingtone",selectedRingtone)
            intent.putExtra("message", message) // Add any additional data here
            return intent
        }
    }
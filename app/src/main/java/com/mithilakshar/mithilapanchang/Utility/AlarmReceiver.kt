package com.mithilakshar.mithilapanchang.Utility
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.UI.View.HomeActivity


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Do your task here, like showing a toast
        Toast.makeText(context, "मिथिला पंचांग अलार्म", Toast.LENGTH_SHORT).show()
        val notificationHelper = NotificationHelper(context, R.drawable.logo)

        val activityToLaunch = HomeActivity::class.java
        if (intent != null && intent.action == "com.example.app.ALARM_ACTION") {

            var title = intent.getStringExtra("title").toString()
            var message = intent.getStringExtra("message").toString()
            var selectedRingtone=intent.getIntExtra("selectedRingtone",R.raw.ram)
            val imageUrl =intent.getStringExtra("imageUrl").toString()

            val imageResource = if (imageUrl=="empty") {
                "https://i.pinimg.com/564x/c6/0f/6f/c60f6f38a9b6a18f5981ce224d1625bd.jpg"
            } else {
                imageUrl // or any default URL or identifier you have
            }


            notificationHelper.createNotificationWithImage(imageResource, title, message, activityToLaunch,selectedRingtone)

        }



        



    }
}
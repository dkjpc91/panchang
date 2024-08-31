package com.mithilakshar.mithilapanchang.Notification

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mithilakshar.mithilapanchang.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationData = message.notification

        getfirebasemessage(this,notificationData?.title.toString(),notificationData?.body.toString())

    }

    fun getfirebasemessage (context: Context, title:String, msg:String ){

        val notificationBuilder = NotificationCompat.Builder(this,"notification").setSmallIcon(R.drawable.mp)
            .setContentTitle(title).setContentTitle(msg).setAutoCancel(true)

        val manager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                Activity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                13
            )
            return
        }
        manager.notify(13, notificationBuilder.build())

    }




}
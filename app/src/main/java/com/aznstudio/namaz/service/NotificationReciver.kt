package com.aznstudio.namaz.service

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.aznstudio.namaz.MainActivity
import com.aznstudio.namaz.R
import java.util.*


/**
 * Created by nazar.humeniuk on 10/24/17.
 */
class NotificationReciver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.e("Notification","OnReceive")
        var name= p1?.getStringExtra("NAME")
            val notificationManager = p0!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent1 = Intent(p0, MainActivity::class.java)
            intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            var alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) as Uri
            val pendingIntent = PendingIntent.getActivity(p0, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
            val builder = NotificationCompat.Builder(p0).setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).setContentText("Время намаза").setContentTitle(name).setAutoCancel(true).setSound(alarmSound)
            notificationManager.notify(100, builder.build())

    }
}



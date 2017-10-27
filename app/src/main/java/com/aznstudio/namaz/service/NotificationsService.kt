package com.aznstudio.namaz.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.aznstudio.namaz.MainActivity
import com.aznstudio.namaz.R
import java.util.concurrent.TimeUnit
import android.app.PendingIntent
import android.graphics.BitmapFactory

import java.util.*


class NotificationsService : Service() {

    private lateinit var nm: NotificationManager
    private val NOTIFICATION_ID = 127

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }



    override fun onCreate() {
        super.onCreate()
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //checkTimeNamaz()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            TimeUnit.SECONDS.sleep(5)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        //showNotification()

        return super.onStartCommand(intent, flags, startId)

    }

    private fun checkTimeNamaz(){
        val timer = Timer()
        val task = MyTimerTask
        timer.schedule(task,10,10)
    }

    object MyTimerTask : TimerTask(){
        override fun run() {
            NotificationsService().showNotification()
        }
    }


    private fun showNotification() {
        //val context = applicationContext as MainActivity
        val builder = Notification.Builder(applicationContext)
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent:PendingIntent = PendingIntent.getActivity(applicationContext,0,intent,PendingIntent.FLAG_CANCEL_CURRENT)
        builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_menu_slideshow)
                .setLargeIcon(BitmapFactory.decodeResource(application.resources, R.drawable.ic_menu_slideshow))
                .setTicker("Новое уведомление")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Уведомление")
                .setContentText("Нажмите чтобы узнать")

        val notification = builder.build()
        notification.defaults = Notification.DEFAULT_ALL
        nm.notify(NOTIFICATION_ID, notification)
    }
}

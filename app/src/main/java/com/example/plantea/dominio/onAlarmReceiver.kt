package com.example.plantea.dominio

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.plantea.R

class onAlarmReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "alarm_channel"
            val channelName = "Alarm Channel"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle(intent.extras!!.getString("Evento"))
                .setContentText("Tienes un evento pendiente")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(intent.extras!!.getInt("Id"), builder.build())

    }
    

    companion object {
        private const val CHANNEL_ID = "PlanTEA"
    }
}
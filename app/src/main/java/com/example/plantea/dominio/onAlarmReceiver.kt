package com.example.plantea.dominio

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.plantea.R

class onAlarmReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24)
        builder.setContentTitle(intent.extras!!.getString("Evento"))
        builder.setContentText(intent.extras?.getString("Dia") + " " + intent.extras?.getString("Mes") + ", " + intent.extras?.getString("Hora"))
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(intent.extras!!.getInt("Id"), builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "PlanTEA"
    }
}
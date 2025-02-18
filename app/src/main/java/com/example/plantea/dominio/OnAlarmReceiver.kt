package com.example.plantea.dominio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.plantea.R
import com.example.plantea.presentacion.viewModels.CalendarioViewModel


/***
 * Clase que se encarga de recibir las alarmas programadas y mostrar una notificación
 */
class OnAlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.extras!!.getBoolean("CambiarVisibilidad")){
                val viewModelCalendario = CalendarioViewModel()
                viewModelCalendario.cambiarVisibilidadEvento(intent.extras!!.getInt("IdEvento"), context)
            }else {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channelId = "alarm_channel"
                val channelName = "Eventos de Plantea"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(channel)

                val builder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.logo_plantea)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                    .setContentTitle(intent.extras!!.getString("Evento"))
                    .setContentText("Tienes un evento programado para hoy")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                notificationManager.notify(intent.extras!!.getInt("Id"), builder.build())
            }
    }
}
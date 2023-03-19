package com.example.plantea.dominio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.plantea.R;

import static android.content.ContentValues.TAG;

public class onAlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "PlanTEA";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        builder.setContentTitle(intent.getExtras().getString("Evento"));
        builder.setContentText(intent.getExtras().get("Dia").toString() + " " + intent.getExtras().get("Mes") + ", " + intent.getExtras().get("Hora").toString());
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(intent.getExtras().getInt("Id"),builder.build());

    }
}

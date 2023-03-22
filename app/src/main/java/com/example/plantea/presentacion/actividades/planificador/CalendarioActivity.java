package com.example.plantea.presentacion.actividades.planificador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantea.dominio.Evento;
import com.example.plantea.dominio.onAlarmReceiver;
import com.example.plantea.presentacion.actividades.MenuActivity;
import com.example.plantea.presentacion.fragmentos.EventosFragment;
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment;
import com.example.plantea.R;
import com.example.plantea.dominio.CalendarioUtilidades;
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario;
import com.example.plantea.presentacion.EventoInterface;
import com.example.plantea.presentacion.fragmentos.cuaderno.PrincipalFragment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class CalendarioActivity extends AppCompatActivity implements AdaptadorCalendario.OnItemSelectedListener, EventoInterface {

    FragmentTransaction transaction;
    Fragment fragment_eventos, fragment_crearEvento;
    private RecyclerView calendario;
    private TextView fechaActual;
    private ArrayList<LocalDate> dias;
    private ImageView btn_siguienteMes, btn_anteriorMes;
    AdaptadorCalendario adaptadorCalendario;
    SharedPreferences prefs;

    AlarmManager alarmManager;

    private static final String CHANNEL_ID = "PlanTEA";

    Evento evento = new Evento();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_calendario);
        } else {
            setContentView(R.layout.activity_calendario_portrait);
        }

        //Activamos icono volver atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperamos la informacion sobre notificación
        prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);

        //Crear canal para las notificaciones
        crearCanalNotificación();

        calendario = findViewById(R.id.recycler_calendario);
        fechaActual = findViewById(R.id.lbl_mes);
        btn_siguienteMes = findViewById(R.id.image_calendar_siguiente);
        btn_anteriorMes = findViewById(R.id.image_calendar_anterior);

        //Iniciamos con el fragment principal

        if (savedInstanceState == null) {
            // Activity is not being recreated, so create a new instance of EventosFragment
            fragment_crearEvento = new NuevoEventoFragment();
            fragment_eventos = new EventosFragment();

            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_calendario, fragment_eventos);
            transaction.commitNow();
        } else {
            // Activity is being recreated, so retrieve the existing fragment from the FragmentManager
            Fragment existingFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_calendario);
            if (existingFragment instanceof EventosFragment) {
                fragment_eventos = (EventosFragment) existingFragment;
            } else {
                fragment_eventos = new EventosFragment();
            }

            Fragment existingFragment2 = getSupportFragmentManager().findFragmentById(R.id.btn_desplegar);
            if (existingFragment2 instanceof NuevoEventoFragment) {
                fragment_crearEvento = (NuevoEventoFragment) existingFragment2;
            } else {
                fragment_crearEvento = new NuevoEventoFragment();
            }
        }

        CalendarioUtilidades.fechaSeleccionada = LocalDate.now();
        obtenerVistaMes();

        btn_anteriorMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1);
                obtenerVistaMes();
            }
        });

        btn_siguienteMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.plusMonths(1);
                obtenerVistaMes();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ayuda, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent it = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(it);
                break;
        }
        return true;
    }

    private void obtenerVistaMes(){
        fechaActual.setText(CalendarioUtilidades.formatoMesAnio(CalendarioUtilidades.fechaSeleccionada).toUpperCase());
        //Calcular días del mes y mostrar
        dias = CalendarioUtilidades.obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada);
        calendario.setLayoutManager(new GridLayoutManager(this,7 ));
        adaptadorCalendario = new AdaptadorCalendario(dias, this);
        calendario.setAdapter(adaptadorCalendario);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_calendario, new EventosFragment());
        ft.commit();
    }

    @Override
    public void diaSeleccionado(LocalDate fecha) {
        if(fecha != null){
            CalendarioUtilidades.fechaSeleccionada = fecha;
            obtenerVistaMes();
        }
    }

    @Override
    public void crearEventoFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_calendario, fragment_crearEvento);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void nuevoEvento(Evento cita) {
        int id = evento.crearEvento(this, cita);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_calendario, new EventosFragment());
        ft.addToBackStack(null);
        ft.commit();
        Boolean notificacion = prefs.getBoolean("notificaciones", false);
        if(notificacion){
            crearNotificacion(cita.getFecha(), CalendarioUtilidades.formatoHoraAviso(cita.getHora()), cita.getNombre(), id);
        }
    }

    @Override
    public void planificar() {
        Intent intent = new Intent(getApplicationContext(), CrearPlanActivity.class);
        startActivity(intent);
    }

    @Override
    public void cancelarEvento() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_calendario, new EventosFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
    private void crearCanalNotificación(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence nombre = "Eventos";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, nombre, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void crearNotificacion(LocalDate fecha, LocalTime hora, String evento, int id){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), onAlarmReceiver.class);
        intent.putExtra("Evento", evento);
        intent.putExtra("Dia", fecha.getDayOfMonth());
        intent.putExtra("Mes", CalendarioUtilidades.formatoMesEvento(fecha));
        intent.putExtra("Hora", hora);
        intent.putExtra("Id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Activity.ALARM_SERVICE);

        Calendar aviso = Calendar.getInstance();
        aviso.setTimeInMillis(System.currentTimeMillis());

        if(prefs.getBoolean("notificacion_semana", false)){
            LocalDate nuevafecha = fecha.minusDays(7);
            aviso.set(nuevafecha.getYear(), nuevafecha.getMonth().getValue()-1, nuevafecha.getDayOfMonth(), hora.getHour(), hora.getMinute(),0);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    aviso.getTimeInMillis(),
                    pendingIntent);
        }
        if(prefs.getBoolean("notificacion_dia", false)){
            LocalDate nuevafecha = fecha.minusDays(1);
            aviso.set(nuevafecha.getYear(), nuevafecha.getMonth().getValue()-1, nuevafecha.getDayOfMonth(), hora.getHour(), hora.getMinute(),0);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    aviso.getTimeInMillis(),
                    pendingIntent);
        }
        if(prefs.getBoolean("notificacion_hora", false)){
            aviso.set(fecha.getYear(), fecha.getMonth().getValue()-1, fecha.getDayOfMonth(), hora.getHour()-1, hora.getMinute(),0);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    aviso.getTimeInMillis(),
                    pendingIntent);
        }
    }

    @Override
    public void cancelarNotificacion(int identificador) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), onAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), identificador, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.onAlarmReceiver
import com.example.plantea.presentacion.actividades.planificador.CrearPlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.fragmentos.EventosFragment
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class CalendarioViewModel: ViewModel(), AdaptadorCalendario.OnItemSelectedListener {

    private lateinit var alarmManager: AlarmManager
    private var isClickedReloj = false
    var isNuevoEventoSelected = false
    var fragment_crearEvento = NuevoEventoFragment()
    var evento = Evento()
    var _dias = MutableLiveData<ArrayList<LocalDate?>>()
    lateinit var eventos: ArrayList<Evento>

    var idUsuario = "0"
    val _fechaActual = MutableLiveData<String>()

    var fragmentEventos = EventosFragment()
    var fragmentNuevoEvento = NuevoEventoFragment()

    fun crearNotificacion(context: Context, fecha: LocalDate?, hora: LocalTime, evento: String?, id: Int){
        val intent = Intent()
        intent.setClass(context, onAlarmReceiver::class.java)
        intent.putExtra("Evento", evento)
        intent.putExtra("Dia", fecha!!.dayOfMonth)
        intent.putExtra("Mes", CalendarioUtilidades.formatoMesEvento(fecha))
        intent.putExtra("Hora", hora)
        intent.putExtra("Id", id)

        val prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)

        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager = context.applicationContext.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val aviso = Calendar.getInstance()
        aviso.timeInMillis = System.currentTimeMillis()
        if (prefs.getBoolean("notificacion_semana", false)) {
            val nuevafecha = fecha.minusDays(7)
            aviso[nuevafecha.year, nuevafecha.month.value - 1, nuevafecha.dayOfMonth, hora.hour, hora.minute] = 0
            alarmManager[AlarmManager.RTC_WAKEUP, aviso.timeInMillis] = pendingIntent
        }
        if (prefs.getBoolean("notificacion_dia", false)) {
            val nuevafecha = fecha.minusDays(1)
            aviso[nuevafecha.year, nuevafecha.month.value - 1, nuevafecha.dayOfMonth, hora.hour, hora.minute] = 0
            alarmManager[AlarmManager.RTC_WAKEUP, aviso.timeInMillis] = pendingIntent
        }
        if (prefs.getBoolean("notificacion_hora", false)) {
            aviso[fecha.year, fecha.month.value - 1, fecha.dayOfMonth, hora.hour - 1, hora.minute] = 0
            alarmManager[AlarmManager.RTC_WAKEUP, aviso.timeInMillis] = pendingIntent
        }
    }

    fun cancelarNotificacion(context: Context, identificador: Int) {
        val intent = Intent()
        intent.setClass(context, onAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, identificador, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun crearEventoFragment(context: Context) {
        isNuevoEventoSelected = true
        val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, fragmentNuevoEvento)
        ft.addToBackStack(null)
        ft.commit()
    }

    fun clickReloj() {
        isClickedReloj = true
    }

    fun nuevoEvento(context: Context, cita: Evento) {
        val id = evento.crearEvento(context as Activity, cita)
        val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.addToBackStack(null)
        ft.commit()
        val prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        val notificacion = prefs.getBoolean("notificaciones", false)
        if (notificacion) {
            crearNotificacion(context, cita.fecha, CalendarioUtilidades.formatoHoraAviso(cita.hora), cita.nombre, id)
        }
    }

    fun planificar(context: Context) {
        val intent = Intent(context, CrearPlanActivity::class.java)
        context.startActivity(intent)
    }

    fun cancelarEvento(context: Context) {
        isNuevoEventoSelected = false
        val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.addToBackStack(null)
        ft.commit()
    }

     fun crearCanalNotificacion(context: Context) {
        val notificationChannel = NotificationChannel("PlanTEA", "Eventos", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun obtenerVistaMes() {
        _fechaActual.value = CalendarioUtilidades.formatoMesAnio(CalendarioUtilidades.fechaSeleccionada).uppercase(Locale.getDefault())
        _dias.value = CalendarioUtilidades.obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
    }

    override fun diaSeleccionado(context: Context?, fecha: LocalDate) {
        CalendarioUtilidades.fechaSeleccionada = fecha
        obtenerVistaMes()
    }

    fun configureUser(prefs : android.content.SharedPreferences, context: Context){
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
        eventos = userId?.let { evento.obtenerTodosEventos(it, context as Activity) } as ArrayList<Evento>
    }

}
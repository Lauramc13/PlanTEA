package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.OnAlarmReceiver
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CrearPlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.fragmentos.EventosFragment
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Locale

class CalendarioViewModel: ViewModel(), AdaptadorCalendario.OnItemSelectedListener {
    private var isNuevoEventoSelected = false
    var evento = Evento()
    var _dias = MutableLiveData<ArrayList<LocalDate?>>()
    lateinit var eventos: ArrayList<Evento>
    var eventosDia = ArrayList<Evento>()

    var idUsuario = "0"
    val _fechaActual = MutableLiveData<String>()
    val _fechaSeleccionada = MutableLiveData<LocalDate>()
    val _changedEvent = SingleLiveEvent<Boolean>()

    var isDiaSeleccionado = false

    //Variables de Eventos Fragment
    lateinit var pictogramas: ArrayList<Pictograma>

    //Variables de Nuevo Evento Fragment
    var isClickedReloj = false
    var hora = 0
    var minuto = 0
    var isPlanSeleccionado = false
    var posicionPlan = 0
    var posicionCalendario = 0
    var lastPositionCalendario = 0
    var planSeleccionado = 0
    lateinit var planes: ArrayList<Planificacion>
    var counter: Int = 1
    var plan = Planificacion()

    //Notificaciones
    var checkBoxMin = false
    var checkBoxHora = false
    var checkBoxDia = false
    var checkBoxPer = false
    var selectedHour = 0
    var selectedMin = 0

    private var bottomSheetDialogFragment = NuevoEventoFragment()

    //Create the notification based on the checkbox selected
    private fun crearNotificacion(context: Context, fecha: LocalDate?, hora: LocalTime, evento: String?, id: Int){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, OnAlarmReceiver::class.java)
        notificationIntent.putExtra("Evento", evento)
        notificationIntent.putExtra("Id", id)
        val pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val aviso = Calendar.getInstance()
        aviso.clear()
        aviso.set(fecha!!.year, fecha.monthValue - 1, fecha.dayOfMonth, hora.hour, hora.minute)

        if (checkBoxDia) {
            aviso.add(Calendar.DAY_OF_MONTH, -1)
        }

        if (checkBoxHora) {
            aviso.add(Calendar.HOUR_OF_DAY, -1)
        }

        if(checkBoxMin) {
            if (aviso.get(Calendar.MINUTE) < 5) {
                aviso.add(Calendar.HOUR_OF_DAY, -1)
                aviso.add(Calendar.MINUTE, 55)
            } else {
                aviso.add(Calendar.MINUTE, -5)
            }
        }

        if(checkBoxPer){
            aviso.set(Calendar.HOUR_OF_DAY, selectedHour)
            aviso.set(Calendar.MINUTE, selectedMin)
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, aviso.timeInMillis, pendingIntent)
    }

    //Cancel the notification
    private fun cancelarNotificacion(context: Context, identificador: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent()
        intent.setClass(context, OnAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, identificador, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun cancelarVisibilidad(actividad: Activity, identificador: Int){
        val intent = Intent(actividad, OnAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(actividad, identificador, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = actividad.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun crearEventoFragment(context: Context) {
        isNuevoEventoSelected = true
        val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, NuevoEventoFragment())
        ft.addToBackStack(null)
        ft.commit()
    }

    fun bottomSheetDialog(context: Context){
        bottomSheetDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        bottomSheetDialogFragment.show((context as AppCompatActivity).supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    fun nuevoEvento(context: Context, cita: Evento) {
        val id = evento.crearEvento(context as Activity, cita)
        cita.id = id
        val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.addToBackStack(null)
        ft.commit()

        eventos.add(cita)
        _changedEvent.value = true

        if(cita.cambiar_visibilidad){
            programarVisibilidad(cita, context, id)
        }

        if(checkBoxMin || checkBoxHora || checkBoxDia || checkBoxPer){
            crearNotificacion(context, cita.fecha, CalendarioUtilidades.formatoHoraAviso(cita.hora), cita.nombre, id)
        }
    }

    private fun programarVisibilidad(cita: Evento, context: Context, id: Int){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, OnAlarmReceiver::class.java).apply {
            putExtra("CambiarVisibilidad", true)
            putExtra("IdEvento", id)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val eventCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, cita.fecha!!.year)
            set(Calendar.MONTH, cita.fecha!!.monthValue - 1) // Calendar months are 0-based
            set(Calendar.DAY_OF_MONTH, cita.fecha!!.dayOfMonth)
            val (hour, minute) = cita.hora!!.split(":").map { it.toInt() }
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            add(Calendar.MINUTE, -5)
        }

        val triggerTimeMillis = eventCalendar.timeInMillis

        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent)
        Log.d("AlarmScheduler", "Scheduled for: ${eventCalendar.time}")
    }

    fun cambiarVisibilidadEvento(eventoId: Int, context: Context){
        val evento = Evento()

        evento.cambiarVisibilidad(context, 1, eventoId)
        val prefs = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val userId = prefs.getString("idUsuario", "")
        evento.invisibiliarEvento(context, eventoId, userId)
    }

    fun deleteEvento(actividad: Activity, context: Context, posicion: Int){
        cancelarNotificacion(context, eventosDia[posicion].id)
        cancelarVisibilidad(actividad, eventosDia[posicion].id)
        evento.eliminarEvento(actividad, eventosDia[posicion].id)

        // remove where eventosDia[posicion].id == eventos.id
        for (i in eventos.indices) {
            if (eventos[i].id == eventosDia[posicion].id) {
                eventos.removeAt(i)
                break
            }
        }
        eventosDia.removeAt(posicion)
        _changedEvent.value = true
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

    override fun diaSeleccionado(context: Context?, fecha: LocalDate, posicion: Int, selectedDay: Int) {
        lastPositionCalendario = selectedDay
        posicionCalendario = posicion
        CalendarioUtilidades.fechaSeleccionada = fecha
        _fechaSeleccionada.value = fecha
        isDiaSeleccionado = true
    }

    fun configureUser(prefs : android.content.SharedPreferences, context: Context){
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
        eventos = userId?.let { evento.obtenerTodosEventos(it, context as Activity) } as ArrayList<Evento>
    }

    fun setIdUsario(prefs : android.content.SharedPreferences){
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
    }

    fun createReloj(context: Context): MaterialTimePicker {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime[Calendar.HOUR_OF_DAY]
        val currentMinute = currentTime[Calendar.MINUTE]
        // get string from strings.xml
        val title = getString(context, R.string.selecciona_hora)

        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTheme(R.style.TimePicker)
            .setTitleText(title)
            .build()
    }

    fun configureDataPlanSeleccionado(posicion: Int){
        isPlanSeleccionado = true
        posicionPlan = posicion
        planSeleccionado = planes[posicion].id
    }

    fun editarClick(actividad: Activity, posicion: Int, startForResult: ActivityResultLauncher<Intent>){
        val pictogramas = plan.obtenerPictogramasPlanificacion(actividad, planes[posicion].id, Locale.getDefault().language) as java.util.ArrayList<Pictograma>
        val intent = Intent(actividad, CrearPlanActivity::class.java)
        intent.putExtra("identificador", planes[posicion].id)
        intent.putExtra("titulo", planes[posicion].titulo)
        intent.putExtra("pictogramas", pictogramas)
        startForResult.launch(intent)
    }

    fun exportEventCalendar(posicion: Int): Intent {
        val date = CalendarioUtilidades.fechaSeleccionada
        val time = CalendarioUtilidades.formatoHoraAviso(eventos[posicion].hora)

        //convert date and time to Date object
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.monthValue - 1, date.dayOfMonth, time.hour, time.minute, time.second)

        return Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, eventos[posicion].nombre)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.timeInMillis + (60 * 60 * 1000))

    }
}
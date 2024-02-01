package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.dominio.onAlarmReceiver
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CrearPlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.fragmentos.EventosFragment
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class CalendarioViewModel: ViewModel(), AdaptadorCalendario.OnItemSelectedListener {
    private lateinit var alarmManager: AlarmManager
    var isNuevoEventoSelected = false
    var evento = Evento()
    var _dias = MutableLiveData<ArrayList<LocalDate?>>()
    lateinit var eventos: ArrayList<Evento>

    var idUsuario = "0"
    val _fechaActual = MutableLiveData<String>()

    var isDiaSeleccionado = false

    //Variables de Eventos Fragment
    lateinit var pictogramas: ArrayList<Pictograma>

    //Variables de Nuevo Evento Fragment
    var isClickedReloj = false
    var hora = 0
    var minuto = 0
    var isPlanSeleccionado = false
    var posicionPlan = 0
    var planSeleccionado = 0
    lateinit var nombreEvento: String
    lateinit var planes: ArrayList<Planificacion>
    var counter: Int = 1
    var plan = Planificacion()

    private var bottomSheetDialogFragment = NuevoEventoFragment()


    private fun crearNotificacion(context: Context, fecha: LocalDate?, hora: LocalTime, evento: String?, id: Int){
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
        if(CommonUtils.isMobile(context)){
            bottomSheetDialogFragment.dismiss()
        }else{
            //TODO: CAMBIAR ESTO PARA ACTUALIZAR EL FRAGMENT EN VEZ DE CREAR UNO NUEVO
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_calendario, EventosFragment())
            ft.addToBackStack(null)
            ft.commit()
        }

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
        isDiaSeleccionado = true
        obtenerVistaMes()
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

    fun createReloj(): MaterialTimePicker{
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime[Calendar.HOUR_OF_DAY]
        val currentMinute = currentTime[Calendar.MINUTE]

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTheme(R.style.TimePicker)
            .setTitleText("Selecciona una hora")
            .build()

        return picker
    }

    fun configureDataPlanSeleccionado(posicion: Int){
        isPlanSeleccionado = true
        posicionPlan = posicion
        planSeleccionado = planes[posicion].id
        nombreEvento = planes[posicion].titulo
    }

    fun editarClick(actividad: Activity, posicion: Int){
        val pictogramas = plan.obtenerPictogramasPlanificacion(actividad, planes[posicion].id) as java.util.ArrayList<Pictograma>
        val intent = Intent(actividad, CrearPlanActivity::class.java)
        intent.putExtra("identificador", planes[posicion].id)
        intent.putExtra("titulo", planes[posicion].titulo)
        intent.putExtra("pictogramas", pictogramas)
        actividad.startActivity(intent)
    }

    fun exportEventCalendar(posicion: Int): Intent {
        val date = CalendarioUtilidades.fechaSeleccionada
        val time = CalendarioUtilidades.formatoHoraAviso(eventos[posicion].hora)

        //convert date and time to Date object
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.monthValue - 1, date.dayOfMonth, time.hour, time.minute, time.second)

        //TODO: cuando este hehco lo del recordatorio meterlo aqui tambien
        return Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, eventos[posicion].nombre)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.timeInMillis + (60 * 60 * 1000))

    }
}
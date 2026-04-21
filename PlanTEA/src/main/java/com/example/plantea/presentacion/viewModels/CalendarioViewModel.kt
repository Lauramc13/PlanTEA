package com.example.plantea.presentacion.viewModels

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.presentacion.receivers.OnAlarmReceiver
import com.example.plantea.dominio.CalendarioUtilidades
import com.google.android.material.datepicker.MaterialDatePicker
import com.example.plantea.dominio.objetos.Evento
import com.example.plantea.dominio.gestores.GestionEventos
import com.example.plantea.dominio.gestores.GestionPlanificaciones
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.dominio.objetos.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.EventosPlanificadorActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.fragmentos.EventosFragment
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.ExperimentalTime

class CalendarioViewModel: ViewModel(), AdaptadorCalendario.OnItemSelectedListener {
    private var isNuevoEventoSelected = false
    var evento = Evento()
    val gEvento = GestionEventos()
    lateinit var eventos: ArrayList<Evento>
    var eventosDia = ArrayList<Evento>()

    var idUsuario = "0"
    var idUsuarioPlanner = "0"

   var listaPictosOriginal = ArrayList<Pictograma>() // sin los cambios de pictogramas borrados o cambiados de posicion
    var listaPictosEliminados = mutableListOf<Pair<Int, String>>()

    var isDiaSeleccionado = false
    var sePlanSeleccionado = SingleLiveEvent<Int>()
    val seChangedEvent = SingleLiveEvent<Boolean>()
    val seEventoEditSaved = SingleLiveEvent<Boolean>()
    val mdFechaActual = MutableLiveData<String>()
    val mdFechaSeleccionada = MutableLiveData<LocalDate>()
    var mdDias = MutableLiveData<ArrayList<LocalDate?>>()

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
    var gPlan = GestionPlanificaciones()
    var isEditing = false
    var eventoIdEdited = 0

    //Notificaciones
    var checkBox5Min = false
    var checkBox15Min = false
    var checkBox30Min = false
    var checkBox1Hora = false
    var checkBoxDia = false
    var checkBoxPer = false
    var selectedHour = 0
    var selectedMin = 0

    @OptIn(ExperimentalTime::class)
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private fun crearNotificacion(
        context: Context,
        evento: String?,
        requestCode: Int?,
        aviso: Long
    ) {
        try {
            val now = System.currentTimeMillis()

            if (aviso <= now) {
                Log.w("Notificacion", "No se programa (pasado): $aviso")
                return
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.e("Notificacion", "Exact alarms no permitidas")
                    return
                }
            }

            val intent = Intent(context, OnAlarmReceiver::class.java).apply {
                putExtra("Evento", evento)
                putExtra("Id", requestCode)
                action = "NOTIF_${requestCode}_$aviso"
            }

            val rc = requestCode ?: run {
                Log.e("CalendarioViewModel", "requestCode null")
                return
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                rc,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.e("TEST", "ANTES DE SET ALARM")

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                aviso,
                pendingIntent
            )

            Log.e("TEST", "DESPUÉS DE SET ALARM")

            Log.i("CalendarioViewModel", "Alarma programada para: ${Date(aviso)} (requestCode: $requestCode)")

        } catch (e: Exception) {
            Log.e("Notificacion", "Error creando alarma", e)
        }
    }

    private fun notificationTimesMillis(
        fecha: LocalDate,
        hora: LocalTime
    ): List<Long> {

        val base = Calendar.getInstance().apply {
            set(fecha.year, fecha.monthValue - 1, fecha.dayOfMonth,
                hora.hour, hora.minute, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val result = mutableListOf<Long>()

        fun addOffset(min: Int) {
            val c = base.clone() as Calendar
            c.add(Calendar.MINUTE, min)
            result.add(c.timeInMillis)
        }

        if (checkBox5Min) addOffset(-5)
        if (checkBox15Min) addOffset(-15)
        if (checkBox30Min) addOffset(-30)
        if (checkBox1Hora) addOffset(-60)
        if (checkBoxDia) addOffset(-24 * 60)

        if (checkBoxPer) {
            val c = Calendar.getInstance().apply {
                set(fecha.year, fecha.monthValue - 1, fecha.dayOfMonth,
                    selectedHour, selectedMin, 0)
            }
            result.add(c.timeInMillis)
        }

        return result
    }

    fun cancelarNotificacion(context: Context, citaId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (i in 0..10) {
            val requestCode = citaId * 100 + i

            val intent = Intent(context, OnAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )

            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
    }

    private fun cancelarVisibilidad(actividad: Activity, identificador: Int?){
        try {
            val intent = Intent(actividad, OnAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(actividad, identificador!!, intent, PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = actividad.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun crearEventoFragment(context: Context, evento: Evento?) {
        isNuevoEventoSelected = true
        val fragment = if (evento == null){
            NuevoEventoFragment()
        }else{
            NuevoEventoFragment.newInstance(evento.id, evento.fecha, evento.horaInicio, evento.horaFin, evento.localizacion, evento.notas, evento.idPlan, evento.cambiarVisibilidad)
        }
        val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, fragment)
        ft.addToBackStack(null)
        ft.commit()
    }

    fun bottomSheetDialog(context: Context, evento: Evento?){
        val fragment = if (evento == null){
           NuevoEventoFragment()
        }else{
            NuevoEventoFragment.newInstance(evento.id, evento.fecha, evento.horaInicio, evento.horaFin, evento.localizacion, evento.notas, evento.idPlan, evento.cambiarVisibilidad)
        }
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        fragment.show((context as AppCompatActivity).supportFragmentManager, fragment.tag)
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun nuevoEventoEdit(context: Context, cita: Evento, pictogramasEvento: ArrayList<Pictograma>?, parent: EventosPlanificadorActivity?) {
        if(isEditing){
            gEvento.editarEvento(context as Activity, cita)
            for (i in parent?.viewModel?.eventos?.indices!!) {
                if (parent.viewModel.eventos[i].id == eventoIdEdited) {
                    parent.viewModel.eventos[i] = cita
                    break
                }
            }

            if (parent.currentFocus != null) {
                CommonUtils.hideKeyboard(context, parent.currentFocus!!)
            }
            seEventoEditSaved.value = true
            eliminarPictosEvento(cita.id, parent)

            parent.listaEventos.adapter?.notifyItemChanged(parent.viewModel.posicionEvento)
        }else{
            cita.id = gEvento.crearEvento(context as Activity, cita)
            parent?.viewModel?.eventos?.add(cita)
            parent?.listaEventos?.adapter?.notifyItemInserted(parent.viewModel.eventos.size - 1)
            parent?.findViewById<LinearLayout>(R.id.layout_no_eventos)?.visibility = View.GONE
        }

        parent?.expand(false, CommonUtils.isPortrait(parent), false)

        if (cita.horaInicio.isNullOrBlank() || cita.horaInicio == "null") {
            Log.w("Notificacion", "Sin hora de inicio, no se programan avisos")
            return
        }


        val avisos = notificationTimesMillis(cita.fecha!!, LocalTime.parse(cita.horaInicio!!))
        val now = System.currentTimeMillis()

        avisos.forEachIndexed { index, time ->
            val requestCode = cita.id?.times(100)?.plus(index)
            crearNotificacion(context, planes[posicionPlan].titulo, requestCode, time)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            Log.i("CalendarioViewModel", "Notificación programada para: ${sdf.format(Date(time))}")
            Log.e("CalendarioViewModel", "NOW = ${Date(now)}")
            Log.e("CalendarioViewModel", "AVISO = ${Date(time)}")
        }

        if(cita.cambiarVisibilidad == true){
            programarVisibilidad(cita, context, cita.id)
        }
    }

    fun closeFragment(parent: EventosPlanificadorActivity?){
        if(parent?.viewModel?.eventos?.isEmpty() == true){
            parent.findViewById<LinearLayout>(R.id.layout_no_eventos)?.visibility = View.VISIBLE
        }
        parent?.expand(false, CommonUtils.isPortrait(parent), false)

    }

    private fun programarVisibilidad(cita: Evento, context: Context, id: Int?){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, OnAlarmReceiver::class.java).apply {
            putExtra("CambiarVisibilidad", true)
            putExtra("IdEvento", id)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, id!!, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val eventCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, cita.fecha!!.year)
            set(Calendar.MONTH, cita.fecha!!.monthValue - 1) // Calendar months are 0-based
            set(Calendar.DAY_OF_MONTH, cita.fecha!!.dayOfMonth)
            val (hour, minute) = cita.horaInicio!!.split(":").map { it.toInt() }
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
        gEvento.cambiarVisibilidad(context, 1, eventoId)
        val prefs = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val userId = prefs.getString("idUsuario", "")
        gEvento.invisibilizarEvento(context, eventoId, userId)
    }

    fun deleteEvento(actividad: Activity, context: Context, posicion: Int){
        cancelarNotificacion(context, eventosDia[posicion].id!!)
        cancelarVisibilidad(actividad, eventosDia[posicion].id)
        gEvento.eliminarEvento(actividad, eventosDia[posicion].id)

        // remove where eventosDia[posicion].id == eventos.id
        for (i in eventos.indices) {
            if (eventos[i].id == eventosDia[posicion].id) {
                eventos.removeAt(i)
                break
            }
        }
        eventosDia.removeAt(posicion)
        seChangedEvent.value = true
    }

    fun cancelarEvento(context: Context) {
        isNuevoEventoSelected = false
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        fragmentManager.findFragmentById(R.id.fragment_calendario)?.let {
            ft.remove(it)
        }
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.addToBackStack(null)
        ft.commit()
    }

    fun cancelarEventoEdit(){
        seEventoEditSaved.value = false
    }

    fun crearCanalNotificacion(context: Context) {
        val notificationChannel = NotificationChannel("PlanTEA", "Eventos", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun obtenerVistaMes() {
        mdFechaActual.value = CalendarioUtilidades.formatoMesAnio(CalendarioUtilidades.fechaSeleccionada).uppercase(Locale.getDefault())
        mdDias.value = CalendarioUtilidades.obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
    }

    override fun diaSeleccionado(context: Context?, fecha: LocalDate, posicion: Int, selectedDay: Int) {
        lastPositionCalendario = selectedDay
        posicionCalendario = posicion
        CalendarioUtilidades.fechaSeleccionada = fecha
        mdFechaSeleccionada.value = fecha
        isDiaSeleccionado = true
    }

    fun configureUser(prefs : android.content.SharedPreferences){
        val userId = if(prefs.getString("idUsuarioTEA", "") == null || prefs.getString("idUsuarioTEA", "") == ""){
            prefs.getString("idUsuario", "")
        } else{
            prefs.getString("idUsuarioTEA", "")
        }
        idUsuario = userId.toString()
        idUsuarioPlanner = prefs.getString("idUsuario", "").toString()
    }

    internal fun mostrarCalendario(): MaterialDatePicker<Long> {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.CalendarPicker)
            .build()

        return datePicker
    }

    fun createReloj(context: Context): MaterialTimePicker {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime[Calendar.HOUR_OF_DAY]
        val currentMinute = currentTime[Calendar.MINUTE]
        val title = getString(context, R.string.selecciona_hora)

        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTheme(R.style.TimePicker)
            .setTitleText(title)
            .build()
    }

    fun configureDataPlanSeleccionado(posicion: Int, parent: EventosPlanificadorActivity?){
        isPlanSeleccionado = true
        posicionPlan = posicion
        planSeleccionado = planes[posicion].id!!.toInt()
    }

    fun exportEventCalendar(posicion: Int): Intent {
        val date = CalendarioUtilidades.fechaSeleccionada
        val timeStart = CalendarioUtilidades.formatoHoraAviso(eventos[posicion].horaInicio)
        val timeEnd = CalendarioUtilidades.formatoHoraAviso(eventos[posicion].horaFin)

        //convert date and time to Date object
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.monthValue - 1, date.dayOfMonth, timeStart.hour, timeStart.minute, timeStart.second)

        return Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, eventos[posicion].nombre)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, timeEnd.hour * 60 * 60 * 1000 + timeEnd.minute * 60 * 1000 + timeEnd.second * 1000)
    }

    private fun eliminarPictosEvento(idEvento : Int?, actividad: Activity){
        if (listaPictosEliminados.isNotEmpty()) {
            for (i in listaPictosEliminados) {
                gEvento.eliminarPictoEvento(i.first, idEvento.toString(), i.second, actividad)
            }
            listaPictosEliminados.clear()
        }
    }

}
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
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

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

    //Create the notification based on the checkbox selected
    private fun crearNotificacion(context: Context?, evento: String?, id: Int?, aviso: Long?) {
        try{
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val notificationIntent = Intent(context, OnAlarmReceiver::class.java)
            notificationIntent.putExtra("Evento", evento)
            notificationIntent.putExtra("Id", id)
            val pendingIntent = PendingIntent.getBroadcast(context, id!!, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            if (aviso != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, aviso, pendingIntent)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun notificationTime(fecha: LocalDate?, hora: LocalTime,): LocalDateTime {
        val aviso = Calendar.getInstance()
        aviso.clear()
        aviso.set(fecha!!.year, fecha.monthValue - 1, fecha.dayOfMonth, hora.hour, hora.minute)

        if (checkBoxDia) {
            aviso.add(Calendar.DAY_OF_MONTH, -1)
        }

        if (checkBox1Hora) {
            aviso.add(Calendar.HOUR_OF_DAY, -1)
        }

        if(checkBox5Min) {
            adjustCalendar(aviso, 5)
        }

        if(checkBox15Min) {
            adjustCalendar(aviso, 15)
        }

        if(checkBox30Min) {
            adjustCalendar(aviso, 30)
        }

        if(checkBoxPer){
            aviso.set(Calendar.HOUR_OF_DAY, selectedHour)
            aviso.set(Calendar.MINUTE, selectedMin)
        }
        return LocalDateTime.of(aviso.get(Calendar.YEAR), aviso.get(Calendar.MONTH) + 1, aviso.get(Calendar.DAY_OF_MONTH), aviso.get(Calendar.HOUR_OF_DAY), aviso.get(Calendar.MINUTE))

    }

    //Cancel the notification
    private fun cancelarNotificacion(context: Context, identificador: Int?) {
        try{
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent()
            intent.setClass(context, OnAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, identificador!!, intent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun adjustCalendar(aviso: Calendar, minutes: Int){
        if (aviso.get(Calendar.MINUTE) < minutes) {
            aviso.add(Calendar.HOUR_OF_DAY, -1)
            aviso.add(Calendar.MINUTE, 60 - minutes + aviso.get(Calendar.MINUTE))
        } else {
            aviso.add(Calendar.MINUTE, -minutes)
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
            NuevoEventoFragment.newInstance(evento.id, evento.fecha, evento.horaInicio, evento.horaFin, evento.localizacion, evento.notas, evento.idPlan, evento.recordatorio, evento.cambiarVisibilidad)
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
            NuevoEventoFragment.newInstance(evento.id, evento.fecha, evento.horaInicio, evento.horaFin, evento.localizacion, evento.notas, evento.idPlan, evento.recordatorio, evento.cambiarVisibilidad)
        }
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        fragment.show((context as AppCompatActivity).supportFragmentManager, fragment.tag)
    }

    fun nuevoEventoEdit(context: Context, cita: Evento, pictogramasEvento: ArrayList<Pictograma>?, parent: EventosPlanificadorActivity?) {
        if(checkBoxDia || checkBox1Hora || checkBox5Min || checkBox15Min || checkBox30Min || checkBoxPer){
            cita.recordatorio = notificationTime(cita.fecha, LocalTime.parse(cita.horaInicio))
        }

        if(isEditing){
            gEvento.editarEvento(context as Activity, cita)
            //change evento where id is the same
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
//            if(pictogramasEvento != null){
//                for (i in pictogramasEvento.indices) {
//                    gEvento.actualizarPosicionPictoEvento(i, pictogramasEvento[i].posicion!!, cita.id.toString(), pictogramasEvento[i].id!!, parent)
//                }
//            }

            parent.listaEventos.adapter?.notifyItemChanged(parent.viewModel.posicionEvento)
        }else{
            cita.id = gEvento.crearEvento(context as Activity, cita)
            parent?.viewModel?.eventos?.add(cita)
            parent?.listaEventos?.adapter?.notifyItemInserted(parent.viewModel.eventos.size - 1)
            parent?.findViewById<LinearLayout>(R.id.layout_no_eventos)?.visibility = View.GONE
        }

        crearNotificacion(context, planes[posicionPlan].titulo, cita.id, cita.recordatorio?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli())

        if(cita.cambiarVisibilidad == true){
            programarVisibilidad(cita, context, cita.id)
        }

        parent?.expand(false, CommonUtils.isPortrait(parent), false)
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
        cancelarNotificacion(context, eventosDia[posicion].id)
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

    fun createCalendar(context: Context): MaterialDatePicker<Long> {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("")
            .setTheme(R.style.CalendarPicker)
            .build()

        datePicker.show((context as AppCompatActivity).supportFragmentManager, "DatePicker")
        return datePicker
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
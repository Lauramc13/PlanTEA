package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.OnAlarmReceiver
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.MainActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramaEntretenimiento
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class EventosPlanificadorViewModel: ViewModel(), AdaptadorPictogramaEntretenimiento.OnItemSelectedListener {

    lateinit var idUsuario : String
    val evento = Evento()
    var posicionEvento = 0
    var fragment = NuevoEventoFragment()

    var dialog: Dialog? = null
    lateinit var eventos : ArrayList<Evento>
    lateinit var pictosEvento: ArrayList<Pictograma>
    var seIdPictoEntretenimiento = SingleLiveEvent<Int>()
    private val mdFechaActual = MutableLiveData<String>()
    private val mdDiasMes = MutableLiveData<ArrayList<LocalDate?>>()

    fun checkInitializedVariable(): Boolean {
        return ::pictosEvento.isInitialized
    }

    fun backCallBack(context: Context): OnBackPressedCallback {
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                if ((context as AppCompatActivity).supportFragmentManager.backStackEntryCount == 0) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    (context).supportFragmentManager.popBackStack()
                }
                context.finish()
            }
        }
        return callback
    }

    fun configureUser(prefs : android.content.SharedPreferences){
        val userId = if(prefs.getString("idUsuarioTEA", "") == null || prefs.getString("idUsuarioTEA", "") == ""){
            prefs.getString("idUsuario", "")
        } else{
            prefs.getString("idUsuarioTEA", "")
        }
        idUsuario = userId.toString()
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

    fun deleteEvento(actividad: Activity, context: Context, posicion: Int){
        cancelarNotificacion(context, eventos[posicion].id)
        cancelarVisibilidad(actividad, eventos[posicion].id)
        evento.eliminarEvento(actividad, eventos[posicion].id)

        // remove where eventosDia[posicion].id == eventos.id
        for (i in eventos.indices) {
            if (eventos[i].id == eventos[posicion].id) {
                eventos.removeAt(i)
                break
            }
        }
    }

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

    override fun onItemSeleccionadoEntre(idPicto: Int) {
        seIdPictoEntretenimiento.value = idPicto
    }

    fun obtenerVistaMes() {
        mdFechaActual.value = CalendarioUtilidades.formatoMesAnio(CalendarioUtilidades.fechaSeleccionada).uppercase(
            Locale.getDefault())
        mdDiasMes.value = CalendarioUtilidades.obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
    }


}
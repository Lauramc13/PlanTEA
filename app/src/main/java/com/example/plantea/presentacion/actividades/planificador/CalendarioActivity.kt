package com.example.plantea.presentacion.actividades.planificador

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoHoraAviso
import com.example.plantea.dominio.CalendarioUtilidades.formatoMesAnio
import com.example.plantea.dominio.CalendarioUtilidades.formatoMesEvento
import com.example.plantea.dominio.CalendarioUtilidades.obtenerDiasMes
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.onAlarmReceiver
import com.example.plantea.presentacion.EventoInterface
import com.example.plantea.presentacion.actividades.MenuActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.fragmentos.EventosFragment
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class CalendarioActivity : AppCompatActivity(), AdaptadorCalendario.OnItemSelectedListener, EventoInterface {
    lateinit var transaction: FragmentTransaction
    lateinit var fragment_eventos: Fragment
    lateinit var fragment_crearEvento: Fragment
    lateinit private var calendario: RecyclerView
    lateinit private var fechaActual: TextView
    lateinit private var dias: ArrayList<LocalDate?>
    lateinit private var btn_siguienteMes: ImageView
    lateinit private var btn_anteriorMes: ImageView
    lateinit var adaptadorCalendario: AdaptadorCalendario
    lateinit var prefs: SharedPreferences
    lateinit var alarmManager: AlarmManager
    var evento = Evento()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Recuperamos la informacion sobre notificación
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        //Crear canal para las notificaciones
        crearCanalNotificación()
        calendario = findViewById(R.id.recycler_calendario)
        fechaActual = findViewById(R.id.lbl_mes)
        btn_siguienteMes = findViewById(R.id.image_calendar_siguiente)
        btn_anteriorMes = findViewById(R.id.image_calendar_anterior)

        //Iniciamos con el fragment principal
        if (savedInstanceState == null) {
            // Activity is not being recreated, so create a new instance of EventosFragment
            fragment_crearEvento = NuevoEventoFragment()
            fragment_eventos = EventosFragment()
            transaction = supportFragmentManager.beginTransaction()
            transaction!!.replace(R.id.fragment_calendario, fragment_eventos)
            transaction!!.commitNow()
        } else {
            // Activity is being recreated, so retrieve the existing fragment from the FragmentManager
            val existingFragment = supportFragmentManager.findFragmentById(R.id.fragment_calendario)
            fragment_eventos = if (existingFragment is EventosFragment) {
                existingFragment
            } else {
                EventosFragment()
            }
            val existingFragment2 = supportFragmentManager.findFragmentById(R.id.btn_desplegar)
            fragment_crearEvento = if (existingFragment2 is NuevoEventoFragment) {
                existingFragment2
            } else {
                NuevoEventoFragment()
            }
        }
        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
        obtenerVistaMes()
        btn_anteriorMes.setOnClickListener(View.OnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
            obtenerVistaMes()
        })
        btn_siguienteMes.setOnClickListener(View.OnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
            obtenerVistaMes()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_ayuda, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val it = Intent(applicationContext, MenuActivity::class.java)
                startActivity(it)
            }
        }
        return true
    }

    private fun obtenerVistaMes() {
        fechaActual!!.text = formatoMesAnio(CalendarioUtilidades.fechaSeleccionada!!).uppercase(Locale.getDefault())
        //Calcular días del mes y mostrar
        dias = obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
        calendario!!.layoutManager = GridLayoutManager(this, 7)
        adaptadorCalendario = AdaptadorCalendario(dias!!, this)
        calendario!!.adapter = adaptadorCalendario
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.commit()
    }

    override fun diaSeleccionado(fecha: LocalDate?) {
        if (fecha != null) {
            CalendarioUtilidades.fechaSeleccionada = fecha
            obtenerVistaMes()
        }
    }

    override fun crearEventoFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, fragment_crearEvento!!)
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun nuevoEvento(cita: Evento) {
        val id = evento.crearEvento(this, cita)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.addToBackStack(null)
        ft.commit()
        val notificacion = prefs!!.getBoolean("notificaciones", false)
        if (notificacion) {
            crearNotificacion(cita.fecha, formatoHoraAviso(cita.hora), cita.nombre, id)
        }
    }

    override fun planificar() {
        val intent = Intent(applicationContext, CrearPlanActivity::class.java)
        startActivity(intent)
    }

    override fun cancelarEvento() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun crearCanalNotificación() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre: CharSequence = "Eventos"
            val notificationChannel = NotificationChannel(CHANNEL_ID, nombre, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun crearNotificacion(fecha: LocalDate?, hora: LocalTime, evento: String?, id: Int) {
        val intent = Intent()
        intent.setClass(applicationContext, onAlarmReceiver::class.java)
        intent.putExtra("Evento", evento)
        intent.putExtra("Dia", fecha!!.dayOfMonth)
        intent.putExtra("Mes", formatoMesEvento(fecha))
        intent.putExtra("Hora", hora)
        intent.putExtra("Id", id)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val aviso = Calendar.getInstance()
        aviso.timeInMillis = System.currentTimeMillis()
        if (prefs!!.getBoolean("notificacion_semana", false)) {
            val nuevafecha = fecha.minusDays(7)
            aviso[nuevafecha.year, nuevafecha.month.value - 1, nuevafecha.dayOfMonth, hora.hour, hora.minute] = 0
            alarmManager!![AlarmManager.RTC_WAKEUP, aviso.timeInMillis] = pendingIntent
        }
        if (prefs!!.getBoolean("notificacion_dia", false)) {
            val nuevafecha = fecha.minusDays(1)
            aviso[nuevafecha.year, nuevafecha.month.value - 1, nuevafecha.dayOfMonth, hora.hour, hora.minute] = 0
            alarmManager!![AlarmManager.RTC_WAKEUP, aviso.timeInMillis] = pendingIntent
        }
        if (prefs!!.getBoolean("notificacion_hora", false)) {
            aviso[fecha.year, fecha.month.value - 1, fecha.dayOfMonth, hora.hour - 1, hora.minute] = 0
            alarmManager!![AlarmManager.RTC_WAKEUP, aviso.timeInMillis] = pendingIntent
        }
    }

    override fun cancelarNotificacion(identificador: Int) {
        val intent = Intent()
        intent.setClass(applicationContext, onAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, identificador, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager!!.cancel(pendingIntent)
    }

    companion object {
        private const val CHANNEL_ID = "PlanTEA"
    }
}
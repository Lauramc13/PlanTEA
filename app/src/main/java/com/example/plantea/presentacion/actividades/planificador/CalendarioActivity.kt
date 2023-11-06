package com.example.plantea.presentacion.actividades.planificador

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoHoraAviso
import com.example.plantea.dominio.CalendarioUtilidades.formatoMesAnio
import com.example.plantea.dominio.CalendarioUtilidades.formatoMesEvento
import com.example.plantea.dominio.CalendarioUtilidades.obtenerDiasMes
import com.example.plantea.dominio.Evento
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.example.plantea.dominio.onAlarmReceiver
import com.example.plantea.presentacion.EventoInterface
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.fragmentos.EventosFragment
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class CalendarioActivity : AppCompatActivity(), AdaptadorCalendario.OnItemSelectedListener, EventoInterface {
    private lateinit var transaction: FragmentTransaction
    private lateinit var fragment_eventos: Fragment
    private lateinit var fragment_crearEvento: Fragment
    private lateinit var calendario: RecyclerView
    private lateinit var fechaActual: TextView
    private lateinit var dias: ArrayList<LocalDate?>
    private lateinit var btn_siguienteMes: ImageView
    private lateinit var btn_anteriorMes: ImageView
    private lateinit var adaptadorCalendario: AdaptadorCalendario
    private var isNuevoEventoSelected = false
    private var isClickedReloj = false
    lateinit var prefs: SharedPreferences
    private lateinit var alarmManager: AlarmManager
    var evento = Evento()
    private var navigationHandler = NavegacionUtils()
    private lateinit var backButton: Button
   // private var fechaSeleccionada : LocalDate = LocalDate.now()

    companion object {
        const val FECHA_SELECCIONADA = "FECHA_SELECCIONADA"
        const val NUEVO_EVENTO_KEY = "NUEVO_EVENTO_KEY"
        // const val EVENTO_CREATED = "EVENTO_CREATED"

        private const val CHANNEL_ID = "PlanTEA"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(FECHA_SELECCIONADA, CalendarioUtilidades.fechaSeleccionada.toString())
        outState.putBoolean(NUEVO_EVENTO_KEY, isNuevoEventoSelected)
       // outState.putBoolean(EVENTO_CREATED, isClickedReloj)
        //save if there is a planificacion selected
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val fechaSel = savedInstanceState.getString(FECHA_SELECCIONADA)

        if (!fechaSel.isNullOrEmpty()) {
            diaSeleccionado(LocalDate.parse(fechaSel))
        }

        if (savedInstanceState.getBoolean(NUEVO_EVENTO_KEY)){
            crearEventoFragment()
        }

        /*if (savedInstanceState.getBoolean(EVENTO_CREATED)){
            fragment_crearEvento.
        }*/


    }

    override fun onResume() {
        super.onResume()
        navigationHandler.configurarDatos(this, R.id.calendar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        //Recuperamos la informacion sobre notificación
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)

        navigationHandler.inicializarVariables(this, R.id.calendar, CalendarioActivity::class.java)

        //Crear canal para las notificaciones
        crearCanalNotificacion()
        calendario = findViewById(R.id.recycler_calendario)
        fechaActual = findViewById(R.id.lbl_mes)
        btn_siguienteMes = findViewById(R.id.image_calendar_siguiente)
        btn_anteriorMes = findViewById(R.id.image_calendar_anterior)
        backButton = findViewById(R.id.goBackButton)

        //Iniciamos con el fragment principal
        if (savedInstanceState == null) {
            // Activity is not being recreated, so create a new instance of EventosFragment
            fragment_crearEvento = NuevoEventoFragment()
            fragment_eventos = EventosFragment()
            /*transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_calendario, fragment_eventos)
            transaction.commitNow()*/
            supportFragmentManager.commit {
                replace<EventosFragment>(R.id.fragment_calendario)
                setReorderingAllowed(true)
                addToBackStack("nuevoEvento")
            }
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
           /* val fragment: NuevoEventoFragment = supportFragmentManager.findFragmentById(R.id.fragment_calendario) as NuevoEventoFragment
            Log.d("pruebas", fragment.toString())
            supportFragmentManager.commit {
                replace(R.id.fragment_calendario, fragment)
                setReorderingAllowed(true)
                addToBackStack("nuevoEvento")
            }*/
            //fragment_crearEvento = (supportFragmentManager.findFragmentByTag(NuevoEventoFragment::class.java.simpleName) as? NuevoEventoFragment)
        }

        backButton.setOnClickListener{
            finish()
        }

        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
        obtenerVistaMes()
        btn_anteriorMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada =
                CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
            obtenerVistaMes()
        }
        btn_siguienteMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada =
                CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
            obtenerVistaMes()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }
/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ayuda -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
            R.id.item_perfil -> {
                val popupMenu = PopupMenu(this@CalendarioActivity, findViewById(R.id.item_ayuda) )
                popupMenu.inflate(R.menu.popup_menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.option_1 -> {
                            val perfil = Intent(applicationContext, ConfiguracionActivity::class.java)
                            startActivity(perfil)
                            true
                        }
                        // R.id.option_2 -> {
                        //     val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                        //     val isPlanificadorLogged = prefs.getBoolean("PlanificadorLogged", false)
                        //     if(isPlanificadorLogged){
                        //         val editor = prefs.edit()
                        //         editor.putBoolean("PlanificadorLogged", false)
                        //         editor.commit()
                        //         val plan = Intent(applicationContext, PlanActivity::class.java)
                        //         startActivity(plan)
                        //     }else{
                        //         crearDialogoLogin()
                        //     }
                        //     true
                        // }
                        R.id.option_3 -> {
                            val dialogLogout = Dialog(this)
                            dialogLogout.setContentView(R.layout.dialogo_logout)
                            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                            btn_logout.setOnClickListener {
                                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                                prefs.edit().clear().commit()
                                // val editor = prefs.edit()
                                // editor.putBoolean("userAccount", false)
                                // editor.apply()
                                val login = Intent(applicationContext, PreLoginActivity::class.java)
                                startActivity(login)
                            }
                            icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                            dialogLogout.show()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            android.R.id.home -> finish()
        }
        return true
    }*/
    private fun obtenerVistaMes() {
        fechaActual.text = formatoMesAnio(CalendarioUtilidades.fechaSeleccionada).uppercase(Locale.getDefault())
        //Calcular días del mes y mostrar
        dias = obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
        calendario.layoutManager = GridLayoutManager(this, 7)
        adaptadorCalendario = AdaptadorCalendario(dias, this)
        calendario.adapter = adaptadorCalendario
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.commit()
    }

    override fun diaSeleccionado(fecha: LocalDate) {
        CalendarioUtilidades.fechaSeleccionada = fecha
        obtenerVistaMes()
    }

    override fun crearEventoFragment() {
        isNuevoEventoSelected = true
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, fragment_crearEvento)
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun clickReloj(tiempo: CharSequence?) {
        isClickedReloj = true
    }

    override fun nuevoEvento(cita: Evento) {
        val id = evento.crearEvento(this, cita)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.addToBackStack(null)
        ft.commit()
        val notificacion = prefs.getBoolean("notificaciones", false)
        if (notificacion) {
            crearNotificacion(cita.fecha, formatoHoraAviso(cita.hora), cita.nombre, id)
        }
    }

    override fun planificar() {
        val intent = Intent(applicationContext, CrearPlanActivity::class.java)
        startActivity(intent)
    }

    override fun cancelarEvento() {
        isNuevoEventoSelected = false
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_calendario, EventosFragment())
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun crearCanalNotificacion() {
        val nombre: CharSequence = "Eventos"
        val notificationChannel = NotificationChannel(CHANNEL_ID, nombre, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun crearNotificacion(fecha: LocalDate?, hora: LocalTime, evento: String?, id: Int) {
        val intent = Intent()
        intent.setClass(applicationContext, onAlarmReceiver::class.java)
        intent.putExtra("Evento", evento)
        intent.putExtra("Dia", fecha!!.dayOfMonth)
        intent.putExtra("Mes", formatoMesEvento(fecha))
        intent.putExtra("Hora", hora)
        intent.putExtra("Id", id)

        val pendingIntent = PendingIntent.getBroadcast(applicationContext, id, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
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

    override fun cancelarNotificacion(identificador: Int) {
        val intent = Intent()
        intent.setClass(applicationContext, onAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, identificador, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }


    override fun onDestroy() {
        super.onDestroy()
        navigationHandler.destroyPopup()
    }

}
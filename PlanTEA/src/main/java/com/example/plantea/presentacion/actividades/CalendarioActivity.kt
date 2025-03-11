package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.fragmentos.EventosFragment
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import java.time.LocalDate
import java.util.Locale

class CalendarioActivity : AppCompatActivity() {
    private lateinit var calendario: RecyclerView
    private lateinit var fechaActual: TextView
    lateinit var prefs: SharedPreferences
    private var atras : Button? = null

    private val viewModel by viewModels<CalendarioViewModel>()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs)

        viewModel.crearCanalNotificacion(this)
        calendario = findViewById(R.id.recycler_calendario)
        fechaActual = findViewById(R.id.lbl_mes)
        val btnSiguienteMes = findViewById<Button>(R.id.image_calendar_siguiente)
        val btnAnteriorMes = findViewById<Button>(R.id.image_calendar_anterior)
        val buttonPlanificacionesNueva = findViewById<Button>(R.id.button_planificaciones_nueva)
        atras = findViewById(R.id.atras)

        //set up the fragments
        if (this.intent.extras != null) {
            CalendarioUtilidades.fechaSeleccionada = LocalDate.parse(this.intent.extras!!.getString("date"))
            viewModel.obtenerVistaMes()
        }else{
            if(savedInstanceState == null) {
                CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
                viewModel.obtenerVistaMes()
            }
        }

        observer(savedInstanceState)

        btnSiguienteMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
            viewModel.obtenerVistaMes()
        }

        // Solo se puede ver el mes anterior al actual
        btnAnteriorMes.setOnClickListener {
            val firstDayOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1)

            if (CalendarioUtilidades.fechaSeleccionada.minusMonths(1).isBefore(firstDayOfPreviousMonth)){
                return@setOnClickListener
            }else{
                CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
                viewModel.obtenerVistaMes()
            }
        }

        atras?.setOnClickListener {
            finish()
        }

        buttonPlanificacionesNueva.setOnClickListener {
            val intent = Intent(this, CrearPlanActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observer(savedInstanceState: Bundle?){
        viewModel.mdFechaActual.observe(this) {
            fechaActual.text = it
        }

        viewModel.mdDias.observe(this) {
            calendario.layoutManager = GridLayoutManager(this, 7)

            val listaDays = if(CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
                arrayOf("L", "M", "X", "J", "V", "S", "D")
            }else if (CommonUtils.isMobile(this) && Locale.getDefault().language == "en"){
                arrayOf("M", "T", "W", "T", "F", "S", "S")
            }else if (!CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
                arrayOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
            }else{
                arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            }


            val adaptadorCalendario = AdaptadorCalendario(it, listaDays, viewModel.eventos, viewModel)
            calendario.adapter = adaptadorCalendario
            calendario.requestLayout()

            if (savedInstanceState == null || viewModel.isDiaSeleccionado) {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_calendario, EventosFragment())
                ft.commit()
            }
        }

        viewModel.seChangedEvent.observe(this) {
            if (it) {
                if(viewModel.isEditing){
                    calendario.adapter?.notifyDataSetChanged()
                }
               calendario.adapter?.notifyItemChanged(viewModel.posicionCalendario)
            }
        }

        viewModel.mdFechaSeleccionada.observe(this) {
            CalendarioUtilidades.fechaSeleccionada = it
            calendario.adapter?.notifyItemChanged(viewModel.lastPositionCalendario)
            calendario.adapter?.notifyItemChanged(viewModel.posicionCalendario)

            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_calendario, EventosFragment())
            ft.commit()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.isDiaSeleccionado = false
    }

    override fun onResume() {
        super.onResume()
        if(calendario.isEmpty()){
            viewModel.obtenerVistaMes()
        }
    }

}
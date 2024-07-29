package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs, this)

        viewModel.crearCanalNotificacion(this)
        calendario = findViewById(R.id.recycler_calendario)
        fechaActual = findViewById(R.id.lbl_mes)
        val btnSiguienteMes = findViewById<Button>(R.id.image_calendar_siguiente)
        val btnAnteriorMes = findViewById<Button>(R.id.image_calendar_anterior)
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

        btnAnteriorMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
            viewModel.obtenerVistaMes()
        }

        atras?.setOnClickListener {
            finish()
        }
    }

    private fun observer(savedInstanceState: Bundle?){
        viewModel._fechaActual.observe(this) {
            fechaActual.text = it
        }

        viewModel._dias.observe(this) {
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

            if (savedInstanceState == null || viewModel.isDiaSeleccionado) {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_calendario, EventosFragment())
                ft.commit()
            }
        }

        viewModel._changedEvent.observe(this) {
            if (it) {
               calendario.adapter?.notifyItemChanged(viewModel.posicionCalendario)
            }
        }

        viewModel._fechaSeleccionada.observe(this) {
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

}
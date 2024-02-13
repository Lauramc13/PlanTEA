package com.example.plantea.presentacion.actividades

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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


class CalendarioActivity : AppCompatActivity() {
    private lateinit var calendario: RecyclerView
    private lateinit var fechaActual: TextView
    lateinit var prefs: SharedPreferences

    private val viewModel by viewModels<CalendarioViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        //Recuperamos la informacion sobre notificación
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs, this)

        //Crear canal para las notificaciones
        viewModel.crearCanalNotificacion(this)
        calendario = findViewById(R.id.recycler_calendario)
        fechaActual = findViewById(R.id.lbl_mes)
        val btnSiguienteMes = findViewById<ImageView>(R.id.image_calendar_siguiente)
        val btnAnteriorMes = findViewById<ImageView>(R.id.image_calendar_anterior)

        //set up the fragments
        if (this.intent.extras != null) {
            // selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
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
    }

    private fun observer(savedInstanceState: Bundle?){
        viewModel._fechaActual.observe(this) {
            fechaActual.text = it
        }

        viewModel._dias.observe(this) {
            calendario.layoutManager = GridLayoutManager(this, 7)
            val adaptadorCalendario = AdaptadorCalendario(it, viewModel.eventos, viewModel)
            calendario.adapter = adaptadorCalendario

            if (savedInstanceState == null || viewModel.isDiaSeleccionado) {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_calendario, EventosFragment())
                ft.commit()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.isDiaSeleccionado = false
    }

}
package com.example.plantea.presentacion.actividades.planificador

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoMesAnio
import com.example.plantea.dominio.CalendarioUtilidades.obtenerDiasMes
import com.example.plantea.dominio.Evento
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.fragmentos.EventosFragment
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.example.plantea.presentacion.viewModels.TraductorViewModel
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class CalendarioActivity : AppCompatActivity() {
    private lateinit var fragmentEventos: Fragment
    private lateinit var fragmentNuevoEvento: Fragment
    private lateinit var calendario: RecyclerView
    private lateinit var fechaActual: TextView
    //private lateinit var dias: ArrayList<LocalDate?>
    private lateinit var btn_siguienteMes: ImageView
    private lateinit var btn_anteriorMes: ImageView
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
        btn_siguienteMes = findViewById(R.id.image_calendar_siguiente)
        btn_anteriorMes = findViewById(R.id.image_calendar_anterior)

        // Retrieve or create the fragments using the ViewModel
        fragmentEventos = viewModel.fragmentEventos
        fragmentNuevoEvento = viewModel.fragmentNuevoEvento

        //set up the fragments
        if(savedInstanceState == null) {
            CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
            viewModel.obtenerVistaMes()
        }


        observer(savedInstanceState)
//        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
        //viewModel.obtenerVistaMes()

        btn_anteriorMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
            viewModel.obtenerVistaMes()
        }
        btn_siguienteMes.setOnClickListener {
            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
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
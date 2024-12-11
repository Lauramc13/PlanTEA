package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CrearPlanActivity
import com.example.plantea.presentacion.actividades.EventosPlanificadorActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.google.android.material.transition.MaterialSharedAxis
import java.time.LocalDate
import java.util.Locale

class CalendarioFragment : Fragment() {

    private val viewModel: CalendarioViewModel by activityViewModels()
    private lateinit var calendario: RecyclerView
    private lateinit var fechaActual: TextView
    lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val vista = inflater.inflate(R.layout.fragment_calendario, container, false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

        prefs = this.requireActivity().getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs)

        viewModel.crearCanalNotificacion(this.requireActivity())
        calendario = vista.findViewById(R.id.recycler_calendario)
        fechaActual = vista.findViewById(R.id.lbl_mes)
        val btnSiguienteMes = vista.findViewById<Button>(R.id.image_calendar_siguiente)
        val btnAnteriorMes = vista.findViewById<Button>(R.id.image_calendar_anterior)
        val cerrarFragment = vista.findViewById<ImageView>(R.id.cerrarFragment)

        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
        viewModel.obtenerVistaMes()

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

        cerrarFragment.setOnClickListener {
            val parentActivity = activity as EventosPlanificadorActivity
            parentActivity.expand(false, CommonUtils.isPortrait(parentActivity), false)
        }

        return vista
    }

    private fun observer(savedInstanceState: Bundle?){
        viewModel._fechaActual.observe(requireActivity()) {
            fechaActual.text = it
        }

        viewModel._dias.observe(requireActivity()) {
            calendario.layoutManager = GridLayoutManager(requireActivity(), 7)

            val listaDays = if(CommonUtils.isMobile(requireActivity()) && Locale.getDefault().language == "es"){
                arrayOf("L", "M", "X", "J", "V", "S", "D")
            }else if (CommonUtils.isMobile(requireActivity()) && Locale.getDefault().language == "en"){
                arrayOf("M", "T", "W", "T", "F", "S", "S")
            }else if (!CommonUtils.isMobile(requireActivity()) && Locale.getDefault().language == "es"){
                arrayOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
            }else{
                arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            }

            viewModel.eventos = viewModel.evento.obtenerTodosEventos(viewModel.idUsuario, context as Activity)
            val adaptadorCalendario = AdaptadorCalendario(it, listaDays, viewModel.eventos, viewModel)
            calendario.adapter = adaptadorCalendario
            calendario.requestLayout()

            /*if (savedInstanceState == null || viewModel.isDiaSeleccionado) {
                val ft = requireActivity().supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragment_calendario, EventosFragment())
                ft.commit()
            }*/
        }

        viewModel._changedEvent.observe(requireActivity()) {
            if (it) {
                if(viewModel.isEditing){
                    calendario.adapter?.notifyDataSetChanged()
                }
                calendario.adapter?.notifyItemChanged(viewModel.posicionCalendario)
            }
        }

        viewModel._fechaSeleccionada.observe(requireActivity()) {
          /*  CalendarioUtilidades.fechaSeleccionada = it
            calendario.adapter?.notifyItemChanged(viewModel.lastPositionCalendario)
            calendario.adapter?.notifyItemChanged(viewModel.posicionCalendario)

            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_calendario, EventosFragment())
            ft.commit()*/

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.isDiaSeleccionado = false
    }

    override fun onResume() {
        super.onResume()
        if(calendario.isEmpty()){ // si calendario se minimiza
            viewModel.obtenerVistaMes()
        }
    }

}
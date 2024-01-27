package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoFechaEvento
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.adaptadores.AdaptadorListaPlanes
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import java.util.*

class NuevoEventoFragment : Fragment(), AdaptadorListaPlanes.OnItemSelectedListener {
    lateinit var actividad: Activity
    lateinit var vista: View
    private lateinit var btnHora: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnPlanificar: Button
    private lateinit var horaEvento: TextView
    private lateinit var fechaEvento: TextView
    private lateinit var mensajePlanes: TextView
    private lateinit var cancelarEvento: ImageView
    private lateinit var listaPlanificaciones: RecyclerView
    private lateinit var adaptador: AdaptadorListaPlanes
    private lateinit var layout_planificaciones: ConstraintLayout

    private val viewModel by viewModels<CalendarioViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_nuevo_evento, container, false)
        cancelarEvento = vista.findViewById(R.id.img_cancelarEvento)
        btnHora = vista.findViewById(R.id.btn_horaEvento)
        btnGuardar = vista.findViewById(R.id.btn_guardarEvento)
        btnPlanificar = vista.findViewById(R.id.btn_planificar)
        horaEvento = vista.findViewById(R.id.lbl_horaEvento)
        fechaEvento = vista.findViewById(R.id.lbl_fechaEvento)
        mensajePlanes = vista.findViewById(R.id.lbl_mensajePlanes)
        listaPlanificaciones = vista.findViewById(R.id.recycler_planificaciones)
        layout_planificaciones = vista.findViewById(R.id.layout)

        iniciarListaPlanificaciones()

        if(viewModel.isClickedReloj){
            mostrarPlanificaciones()
            if(viewModel.isPlanSeleccionado){
                adaptador.setItemSelected(viewModel.posicionPlan)
                btnGuardar.isEnabled = true
            }
        }else{
            layout_planificaciones.visibility = View.GONE
        }

        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        viewModel.setIdUsario(prefs)

        fechaEvento.text = formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada)
        //consultas = pictograma.obtenerConsultas(actividad, 1) as ArrayList<String>
        btnHora.setOnClickListener { mostrarReloj() }

        btnGuardar.setOnClickListener {
            val evento = Evento(0, viewModel.idUsuario, viewModel.nombreEvento, CalendarioUtilidades.fechaSeleccionada, horaEvento.text.toString(), viewModel.planSeleccionado)
            context?.let { it1 -> viewModel.nuevoEvento(it1, evento) }
            Toast.makeText(context, "Evento creado", Toast.LENGTH_SHORT).show()
        }
        btnPlanificar.setOnClickListener { context?.let { it1 -> viewModel.planificar(it1) } }
        cancelarEvento.setOnClickListener { context?.let { it1 -> viewModel.cancelarEvento(it1) } }
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    private fun iniciarListaPlanificaciones() {
        listaPlanificaciones.layoutManager = LinearLayoutManager(context)
        viewModel.planes = viewModel.idUsuario.let { viewModel.plan.mostrarPlanificacionesDisponibles(it, actividad) } as ArrayList<Planificacion>
        adaptador = AdaptadorListaPlanes(viewModel.planes, this)
        listaPlanificaciones.adapter = adaptador

        visibilityPlan()

    }

    private fun mostrarReloj() {
        val picker = viewModel.createReloj()

        picker.addOnPositiveButtonClickListener {
            viewModel.isClickedReloj = true
            viewModel.hora = picker.hour
            viewModel.minuto = picker.minute

            mostrarPlanificaciones()
        }

        picker.show(requireFragmentManager(), "TimePicker")
    }

    private fun mostrarPlanificaciones(){
        horaEvento.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.hora, viewModel.minuto)

        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode) {
            horaEvento.setTextColor(Color.rgb(228, 231, 235))
        } else {
            horaEvento.setTextColor(Color.rgb(24, 31, 37))
        }

        layout_planificaciones.visibility = View.VISIBLE
        iniciarListaPlanificaciones()
    }


    override fun deleteClick(posicion: Int) {
        val dialogPlan = Dialog(actividad)
        dialogPlan.setContentView(R.layout.dialogo_eliminar_planificacion)
        dialogPlan.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnEliminar = dialogPlan.findViewById<Button>(R.id.btn_eliminarPlan)
        val iconCerrar = dialogPlan.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val btnCancelar = dialogPlan.findViewById<Button>(R.id.btn_cancelarPlan)

        btnEliminar.setOnClickListener {
            Toast.makeText(context, "Planificación eliminada", Toast.LENGTH_SHORT).show()
            viewModel.plan.eliminarPlanificacion(actividad, viewModel.planes[posicion].id)
            viewModel.planes.removeAt(posicion)
            adaptador.notifyDataSetChanged()
            //Mostramos un mensaje informando si la lista está vacía
            visibilityPlan()
            dialogPlan.dismiss()
        }
        btnCancelar.setOnClickListener {
            dialogPlan.dismiss()
        }

        iconCerrar.setOnClickListener { dialogPlan.dismiss() }
        dialogPlan.show()
    }

    override fun editClick(posicion: Int) {
        viewModel.editarClick(actividad, posicion)
    }

    override fun duplicateClick(posicion: Int) {
        val pictogramas = viewModel.plan.obtenerPictogramasPlanificacion(actividad, viewModel.planes[posicion].id) as ArrayList<Pictograma>
        val creada = viewModel.idUsuario.let { viewModel.plan.crearPlanificacion(actividad, it, viewModel.planes[posicion].titulo + " " + viewModel.counter.toString()) }
        viewModel.plan.addPictogramasPlan(creada, actividad, pictogramas)
        viewModel.counter++ //Incrementamos el contador para que el título de la planificación duplicada sea diferente
        if (creada != 0) {
            Toast.makeText(context, "Planificación duplicada", Toast.LENGTH_LONG).show()
            iniciarListaPlanificaciones()
        } else {
            Toast.makeText(context, "Error al duplicar planificación", Toast.LENGTH_LONG).show()
        }
    }

    override fun planSeleccionado(posicion: Int) {
        viewModel.configureDataPlanSeleccionado(posicion)
        btnGuardar.isEnabled = true
    }

    fun visibilityPlan(){
        if (viewModel.planes.isEmpty()) {
            listaPlanificaciones.visibility = View.GONE
            mensajePlanes.visibility = View.VISIBLE
        } else {
            listaPlanificaciones.visibility = View.VISIBLE
            mensajePlanes.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.planes.clear()
        viewModel.planes.addAll(viewModel.idUsuario.let { viewModel.plan.mostrarPlanificacionesDisponibles(it, actividad) } as ArrayList<Planificacion>)
        visibilityPlan()
        adaptador.notifyDataSetChanged()
    }
}
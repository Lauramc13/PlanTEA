package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoDiaEvento
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.PlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorEvento
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import java.time.LocalDate
import java.util.Locale


class EventosFragment : Fragment(), AdaptadorEvento.OnItemSelectedListener {
    lateinit var vista: View
    private lateinit var diaEvento: TextView
    private lateinit var layoutMensaje : LinearLayout
    private lateinit var mensaje: TextView
    private lateinit var crearEvento: FloatingActionButton
    private lateinit var listaEventos: RecyclerView
    private lateinit var actividad: Activity
    private lateinit var adaptadorEvento: AdaptadorEvento
    private lateinit var btnEliminar: Button
    private lateinit var btnCancelar: Button
    private lateinit var iconoCerrar : ImageView

    //private val viewModel by viewModels<CalendarioViewModel>()
    private val viewModel by activityViewModels<CalendarioViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_eventos, container, false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y,true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)

        diaEvento = vista.findViewById(R.id.txt_dia_eventos)
        crearEvento = vista.findViewById(R.id.btn_nuevo_evento)
        mensaje = vista.findViewById(R.id.lbl_mensaje_evento)
        layoutMensaje = vista.findViewById(R.id.linearLayout3)
        listaEventos = vista.findViewById(R.id.recycler_eventos)

        if(savedInstanceState != null){
            CalendarioUtilidades.fechaSeleccionada = viewModel._fechaSeleccionada.value!!
        }

        iniciarAdaptadorEvento()
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        viewModel.setIdUsario(prefs)

        crearEvento.setOnClickListener {
            if(CommonUtils.isMobile(this.requireContext())){
                context?.let { it1 -> viewModel.bottomSheetDialog(it1) }
            }else{
                context?.let { it1 -> viewModel.crearEventoFragment(it1) }

            }
        }

        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    private fun iniciarAdaptadorEvento() {
        diaEvento.text = formatoDiaEvento(CalendarioUtilidades.fechaSeleccionada).uppercase(Locale.getDefault())
        if(CalendarioUtilidades.fechaSeleccionada.isBefore(LocalDate.now()) ){
            crearEvento.visibility = View.GONE
            mensaje.text = "No se pueden crear eventos en fechas pasadas"
        }
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val userId = prefs.getString("idUsuario", "")
        viewModel.eventosDia = userId?.let { viewModel.evento.obtenerEventos(it, actividad, CalendarioUtilidades.fechaSeleccionada) } as ArrayList<Evento>

        listaEventos.layoutManager = LinearLayoutManager(context)
        adaptadorEvento = AdaptadorEvento(viewModel.eventosDia, this)
        if (viewModel.eventosDia.isEmpty()) {
            listaEventos.visibility = View.GONE
            layoutMensaje.visibility = View.VISIBLE
        } else {
            listaEventos.visibility = View.VISIBLE
            layoutMensaje.visibility = View.GONE
        }
        listaEventos.adapter = adaptadorEvento
    }
    override fun deleteClick(posicion: Int) {
        val dialogEvento = Dialog(actividad)
        dialogEvento.setContentView(R.layout.dialogo_eliminar_evento)
        dialogEvento.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnEliminar = dialogEvento.findViewById(R.id.btn_eliminarEvento)
        iconoCerrar = dialogEvento.findViewById(R.id.icono_CerrarDialogo)
        btnCancelar = dialogEvento.findViewById(R.id.btn_cancelarEvento)

        btnEliminar.setOnClickListener {
            CommonUtils.showSnackbar(vista, actividad, "Evento eliminado")
            adaptadorEvento.notifyItemRemoved(posicion)
            viewModel.deleteEvento(actividad, requireContext(), posicion)

            //Mostramos un mensaje informando si la lista está vacía
            if (viewModel.eventosDia.isEmpty()) {
                listaEventos.visibility = View.GONE
                layoutMensaje.visibility = View.VISIBLE
            } else {
                listaEventos.visibility = View.VISIBLE
                layoutMensaje.visibility = View.GONE
            }
            dialogEvento.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialogEvento.dismiss()
        }
        iconoCerrar.setOnClickListener { dialogEvento.dismiss() }
        dialogEvento.show()
    }

    override fun viewClick(posicion: Int) {
        val fecha = CalendarioUtilidades.fechaSeleccionada.toString()
        val eventoVisible = viewModel.idUsuario.let { viewModel.evento.comprobarEventoVisible(it, fecha, actividad) }

        if (viewModel.eventosDia[posicion].visible == 1) {
            viewModel.evento.cambiarVisibilidad(actividad, 0, viewModel.eventosDia[posicion].id)
        } else {
            viewModel.evento.cambiarVisibilidad(actividad, 0, eventoVisible)
            viewModel.evento.cambiarVisibilidad(actividad, 1, viewModel.eventosDia[posicion].id)
        }

        iniciarAdaptadorEvento()
    }

    override fun viewEventClick(posicion: Int) {
        val pictogramas = viewModel.plan.obtenerPictogramasPlanificacion(actividad, viewModel.eventosDia[posicion].id_plan) as ArrayList<Pictograma>
        val intent = Intent(actividad, PlanActivity::class.java)
        intent.putExtra("titulo", viewModel.eventos[posicion].nombre)
        intent.putExtra("pictogramas", pictogramas)
        startActivity(intent)
    }

    override fun viewExportClick(posicion: Int) {
        val intent = viewModel.exportEventCalendar(posicion)
        if (intent.resolveActivity(actividad.packageManager) != null) {
            startActivity(intent)
        } else {
            CommonUtils.showSnackbar(vista, actividad, "No hay aplicaciones a las que exportar el evento")
        }
    }
}
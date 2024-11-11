package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoDiaEvento
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.EventosActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorEvento
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import java.time.LocalDate
import java.util.Locale

class EventosFragment : Fragment(), AdaptadorEvento.OnItemSelectedListener {
    lateinit var vista: View
    private lateinit var diaEvento: TextView
    private lateinit var layoutMensaje : LinearLayout
    private lateinit var mensaje: TextView
    private lateinit var crearEvento: MaterialButton
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
            if(viewModel._fechaSeleccionada.value != null){
                CalendarioUtilidades.fechaSeleccionada = viewModel._fechaSeleccionada.value!! // NULLPOINTEREXCEPTION
            }else{
                CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
            }
        }

        iniciarAdaptadorEvento()
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        viewModel.setIdUsario(prefs)

        crearEvento.setOnClickListener {
            if(CommonUtils.isMobile(this.requireContext()) || (! CommonUtils.isMobile(this.requireContext()) && CommonUtils.isPortrait(this.requireActivity()))){
                viewModel.bottomSheetDialog(requireContext(), null)
            }else{
                viewModel.crearEventoFragment(requireContext(), null)
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
            mensaje.text = getString(R.string.mensaje_eventos_pasados)
        }
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)

        val userId = if(prefs.getString("idUsuarioTEA", "") == null || prefs.getString("idUsuarioTEA", "") == ""){
            prefs.getString("idUsuario", "")
        } else{
            prefs.getString("idUsuarioTEA", "")
        }

        viewModel.eventosDia = viewModel.evento.obtenerEventos(userId!!, actividad, CalendarioUtilidades.fechaSeleccionada)

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
            Toast.makeText(actividad, R.string.toast_evento_eliminado, Toast.LENGTH_SHORT).show()
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
        val pictogramas = viewModel.plan.obtenerPictogramasPlanificacion(actividad, viewModel.eventosDia[posicion].idPlan, Locale.getDefault().language, viewModel.idUsuario)

        val intent = Intent(actividad, EventosActivity::class.java)
        intent.putExtra("titulo", viewModel.eventosDia[posicion].nombre)
        intent.putExtra("pictogramas", pictogramas)
        pictogramas.forEachIndexed { index, pictogram ->
            intent.putExtra("imagen_$index", CommonUtils.bitmapToByteArray(pictogram.imagen))
        }
        intent.putExtra("fecha", viewModel.eventosDia[posicion].fecha)

        startActivity(intent)
    }

    override fun viewExportClick(posicion: Int) {
        val intent = viewModel.exportEventCalendar(posicion)
        //filter my own package
        val resolvedActivities = actividad.packageManager.queryIntentActivities(intent, 0)

        if (resolvedActivities.isNotEmpty()) {
            val chooserIntent = Intent.createChooser(intent, R.string.toast_exportar_evento.toString())
            val excludeComponentNames = resolvedActivities.map { ComponentName(it.activityInfo.packageName, it.activityInfo.name) }.toTypedArray() //Exclude self
            chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludeComponentNames)
            startActivity(chooserIntent)
        } else {
            Toast.makeText(actividad, R.string.toast_error_exportar_evento, Toast.LENGTH_SHORT).show()
        }
    }

    override fun editEvento(posicion: Int) {
        val evento = viewModel.evento.obtenerInfoEvento(viewModel.eventosDia[posicion].id, actividad)
        //put extras of the event to edit
        if(CommonUtils.isMobile(this.requireContext()) || (! CommonUtils.isMobile(this.requireContext()) && CommonUtils.isPortrait(this.requireActivity()))){
           viewModel.bottomSheetDialog(requireContext(), evento)
        }else{
            viewModel.crearEventoFragment(requireContext(), evento)
        }
    }
}
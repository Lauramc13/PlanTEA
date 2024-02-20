package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


class EventosFragment : Fragment(), AdaptadorEvento.OnItemSelectedListener {
    lateinit var vista: View
    lateinit var diaEvento: TextView
    lateinit var mensaje: TextView
    lateinit var crearEvento: FloatingActionButton
    lateinit var listaEventos: RecyclerView
    lateinit var actividad: Activity
    lateinit var adaptadorEvento: AdaptadorEvento
    lateinit var btn_eliminar: Button
    lateinit var btn_cancelar: Button
    lateinit var icono_cerrar_login : ImageView

    private val viewModel by viewModels<CalendarioViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_eventos, container, false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y,true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        //reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)

        diaEvento = vista.findViewById(R.id.txt_dia_eventos)
        crearEvento = vista.findViewById(R.id.btn_nuevo_evento)
        mensaje = vista.findViewById(R.id.lbl_mensaje_evento)
        listaEventos = vista.findViewById(R.id.recycler_eventos)

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
        viewModel.eventos = userId?.let { viewModel.evento.obtenerEventos(it, actividad, CalendarioUtilidades.fechaSeleccionada) } as ArrayList<Evento>

        listaEventos.layoutManager = LinearLayoutManager(context)
        adaptadorEvento = AdaptadorEvento(viewModel.eventos, this)
        if (viewModel.eventos.isEmpty()) {
            listaEventos.visibility = View.GONE
            mensaje.visibility = View.VISIBLE
        } else {
            listaEventos.visibility = View.VISIBLE
            mensaje.visibility = View.GONE
        }
        listaEventos.adapter = adaptadorEvento
    }
    override fun deleteClick(posicion: Int) {
        val dialogEvento = Dialog(actividad)
        dialogEvento.setContentView(R.layout.dialogo_eliminar_evento)
        dialogEvento.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_eliminar = dialogEvento.findViewById(R.id.btn_eliminarEvento)
        icono_cerrar_login = dialogEvento.findViewById(R.id.icono_CerrarDialogo)
        btn_cancelar = dialogEvento.findViewById(R.id.btn_cancelarEvento)
        btn_eliminar.setOnClickListener {
            CommonUtils.showSnackbar(vista, actividad, "Evento eliminado")
            context?.let { it1 -> viewModel.cancelarNotificacion(it1, viewModel.eventos[posicion].id) }
            viewModel.evento.eliminarEvento(actividad, viewModel.eventos[posicion].id)
            viewModel.eventos.removeAt(posicion)
                    adaptadorEvento.notifyDataSetChanged()
                    //Mostramos un mensaje informando si la lista está vacía
                    if (viewModel.eventos.isEmpty()) {
                        listaEventos.visibility = View.GONE
                        mensaje.visibility = View.VISIBLE
                    } else {
                        listaEventos.visibility = View.VISIBLE
                        mensaje.visibility = View.GONE
                    }
            dialogEvento.dismiss()
            }
        btn_cancelar.setOnClickListener {
            dialogEvento.dismiss()
        }
        icono_cerrar_login.setOnClickListener { dialogEvento.dismiss() }
        dialogEvento.show()
    }

    override fun viewClick(posicion: Int) {
        val fecha = CalendarioUtilidades.fechaSeleccionada.toString()

        val contador = viewModel.idUsuario.let { viewModel.evento.comprobarEventosVisible(it, fecha, actividad) }
        if (viewModel.eventos[posicion].visible == 1) {
            viewModel.evento.cambiarVisibilidad(actividad, 0, viewModel.eventos[posicion].id)
        } else {
            if (contador == 0) {
                viewModel.evento.cambiarVisibilidad(actividad, 1, viewModel.eventos[posicion].id)
            } else {
                CommonUtils.showSnackbar(vista, actividad, "Solo un evento puede ser visible")
            }
        }
        iniciarAdaptadorEvento()
    }

    override fun viewEventClick(posicion: Int) {
        val pictogramas = viewModel.plan.obtenerPictogramasPlanificacion(actividad, viewModel.eventos[posicion].id_plan) as ArrayList<Pictograma>
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
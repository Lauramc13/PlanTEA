package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoDiaEvento
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.EventoInterface
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorEvento
import java.time.LocalDate
import java.util.*

class EventosFragment : Fragment(), AdaptadorEvento.OnItemSelectedListener {
    lateinit var vista: View
    lateinit var diaEvento: TextView
    lateinit var mensaje: TextView
    lateinit var crearEvento: Button
    lateinit var listaEventos: RecyclerView
    lateinit var actividad: Activity
    lateinit var eventoInterface: EventoInterface
    lateinit var eventos: ArrayList<Evento>
    lateinit var pictogramas: ArrayList<Pictograma>
    lateinit var adaptadorEvento: AdaptadorEvento
    var contador = 0
    var evento = Evento()
    var plan = Planificacion()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_eventos, container, false)
        diaEvento = vista.findViewById(R.id.txt_dia_eventos)
        crearEvento = vista.findViewById(R.id.btn_nuevo_evento)
        mensaje = vista.findViewById(R.id.lbl_mensaje_evento)
        listaEventos = vista.findViewById(R.id.recycler_eventos)
        iniciarAdaptadorEvento()
        crearEvento.setOnClickListener( { eventoInterface.crearEventoFragment() })
        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            eventoInterface = (actividad as EventoInterface?)!!
        }
    }

    fun iniciarAdaptadorEvento() {
        diaEvento.text = formatoDiaEvento(CalendarioUtilidades.fechaSeleccionada).uppercase(Locale.getDefault())
        if(CalendarioUtilidades.fechaSeleccionada.isBefore(LocalDate.now()) ){
            crearEvento.isEnabled = false
        }
        eventos = evento.obtenerEventos(actividad, CalendarioUtilidades.fechaSeleccionada) as ArrayList<Evento>
        listaEventos.layoutManager = LinearLayoutManager(context)
        adaptadorEvento = AdaptadorEvento(eventos, this)
        if (eventos.isEmpty()) {
            listaEventos.visibility = View.GONE
            mensaje.visibility = View.VISIBLE
        } else {
            listaEventos.visibility = View.VISIBLE
            mensaje.visibility = View.GONE
        }
        listaEventos.adapter = adaptadorEvento
    }

    override fun deleteClick(posicion: Int) {
        val dialogoEliminar = AlertDialog.Builder(context)
        dialogoEliminar.setTitle("Eliminar Evento")
        dialogoEliminar.setMessage("¿Seguro que deseas eliminar el evento seleccionado?")
        dialogoEliminar.setCancelable(false)
        dialogoEliminar.setPositiveButton("Confirmar") { dialogoEliminar, id ->
            Toast.makeText(context, "Evento eliminado", Toast.LENGTH_SHORT).show()
            eventoInterface.cancelarNotificacion(eventos[posicion].id)
            evento.eliminarEvento(actividad, eventos[posicion].id)
            eventos.removeAt(posicion)
            adaptadorEvento.notifyDataSetChanged()
            //Mostramos un mensaje informando si la lista está vacía
            if (eventos.isEmpty()) {
                listaEventos.visibility = View.GONE
                mensaje.visibility = View.VISIBLE
            } else {
                listaEventos.visibility = View.VISIBLE
                mensaje.visibility = View.GONE
            }
        }
        dialogoEliminar.setNegativeButton("Cancelar") { dialogoEliminar, id -> dialogoEliminar.dismiss() }
        dialogoEliminar.show()
    }

    override fun viewClick(posicion: Int) {
        contador = evento.comprobarEventosVisible(actividad)
        println(contador)
        if (eventos[posicion].visible == 1) {
            evento.cambiarVisibilidad(actividad, 0, eventos[posicion].id)
        } else {
            if (contador == 0) {
                evento.cambiarVisibilidad(actividad, 1, eventos[posicion].id)
            } else {
                Toast.makeText(context, "Solo un evento puede ser visible", Toast.LENGTH_SHORT).show()
            }
        }
        iniciarAdaptadorEvento()
    }

    override fun viewEventClick(posicion: Int) {
        pictogramas = ArrayList()
        pictogramas = plan.obtenerPictogramasPlanificacion(actividad, eventos[posicion].id_plan) as ArrayList<Pictograma>
        val intent = Intent(actividad, PlanActivity::class.java)
        intent.putExtra("titulo", eventos[posicion].nombre)
        intent.putExtra("pictogramas", pictogramas)
        startActivity(intent)
    }
}
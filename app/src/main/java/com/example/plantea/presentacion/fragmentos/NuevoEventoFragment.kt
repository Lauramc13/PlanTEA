package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoFechaEvento
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.EventoInterface
import com.example.plantea.presentacion.actividades.planificador.CrearPlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorListaPlanes
import java.util.*

class NuevoEventoFragment : Fragment(), AdaptadorListaPlanes.OnItemSelectedListener {
    lateinit var actividad: Activity
    lateinit var eventoInterface: EventoInterface
    lateinit var vista: View
    lateinit var btn_hora: Button
    lateinit var btn_guardar: Button
    lateinit var btn_planificar: Button
    lateinit var btn_desplegarPlanes: Button
    lateinit var horaEvento: TextView
    lateinit var fechaEvento: TextView
    lateinit var mensajePlanes: TextView
    lateinit var cancelarEvento: ImageView
    lateinit var listaPlanificaciones: RecyclerView
    lateinit var spinner_consultas: Spinner
    lateinit var adaptador: AdaptadorListaPlanes
    lateinit var planes: ArrayList<Planificacion>
    lateinit var layout_planificaciones: ConstraintLayout
    private lateinit var pictogramas: ArrayList<Pictograma>
    private lateinit var consultas: ArrayList<String>
    var hora = 0
    var minuto = 0
    var planSeleccionado = 0
    var posAnterior = 0
    lateinit var nombreEvento: String
    var plan = Planificacion()
    var pictograma = Pictograma()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_nuevo_evento, container, false)
        cancelarEvento = vista.findViewById(R.id.img_cancelarEvento)
        btn_hora = vista.findViewById(R.id.btn_horaEvento)
        btn_guardar = vista.findViewById(R.id.btn_guardarEvento)
        btn_planificar = vista.findViewById(R.id.btn_planificar)
        btn_desplegarPlanes = vista.findViewById(R.id.button)
        horaEvento = vista.findViewById(R.id.lbl_horaEvento)
        fechaEvento = vista.findViewById(R.id.lbl_fechaEvento)
        mensajePlanes = vista.findViewById(R.id.lbl_mensajePlanes)
        listaPlanificaciones = vista.findViewById(R.id.recycler_planificaciones)
        spinner_consultas = vista.findViewById(R.id.spinner_consultas)
        layout_planificaciones = vista.findViewById(R.id.layout)

        //Componentes deshabilitados al principio
        spinner_consultas.setEnabled(false)
        btn_desplegarPlanes.setEnabled(false)
        layout_planificaciones.setVisibility(View.GONE)
        fechaEvento.setText(formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada!!))
        consultas = pictograma.obtenerConsultas(actividad, 1) as ArrayList<String>
        spinner_consultas.setAdapter(ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, consultas))
        btn_desplegarPlanes.setOnClickListener(View.OnClickListener {
            layout_planificaciones.setVisibility(View.VISIBLE)
            iniciarListaPlanificaciones()
        })
        btn_hora.setOnClickListener(View.OnClickListener { mostrarReloj(horaEvento) })
        btn_guardar.setOnClickListener(View.OnClickListener {
            val rutaImagen = obtenerImagenEvento()
            val evento = Evento(0, nombreEvento, CalendarioUtilidades.fechaSeleccionada, horaEvento.getText().toString(), planSeleccionado, rutaImagen)
            eventoInterface!!.nuevoEvento(evento)
            Toast.makeText(context, "Evento creado", Toast.LENGTH_SHORT).show()
        })
        btn_planificar.setOnClickListener(View.OnClickListener { eventoInterface!!.planificar() })
        cancelarEvento.setOnClickListener(View.OnClickListener { eventoInterface!!.cancelarEvento() })
        return vista
    }

    fun obtenerImagenEvento(): String? {
        return pictograma.obtenerImagenEvento(actividad, spinner_consultas!!.selectedItem.toString(), 1)
    }

    override fun onResume() {
        super.onResume()
        iniciarListaPlanificaciones()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
            eventoInterface = (actividad as EventoInterface?)!!
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun iniciarListaPlanificaciones() {
        listaPlanificaciones!!.layoutManager = LinearLayoutManager(context)
        planes = plan.mostrarPlanificacionesDisponibles(actividad) as ArrayList<Planificacion>
        adaptador = AdaptadorListaPlanes(planes, this)
        listaPlanificaciones!!.adapter = adaptador
        //Mostramos un mensaje informando si la lista está vacía
        if (planes!!.isEmpty()) {
            listaPlanificaciones!!.visibility = View.GONE
            mensajePlanes!!.visibility = View.VISIBLE
        } else {
            listaPlanificaciones!!.visibility = View.VISIBLE
            mensajePlanes!!.visibility = View.GONE
        }
    }

    private fun mostrarReloj(tiempo: TextView?) {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime[Calendar.HOUR_OF_DAY]
        val currentMinute = currentTime[Calendar.MINUTE]
        val onTimeSetListener = OnTimeSetListener { timePicker, horaSeleccionada, minutoSeleccionado ->
            hora = horaSeleccionada
            minuto = minutoSeleccionado
            tiempo!!.text = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
            //Habilitamos el resto de componentes
            spinner_consultas!!.isEnabled = true
            btn_desplegarPlanes!!.isEnabled = true
        }
        val style = android.R.style.Theme_Holo_Light_Dialog
        val timePickerDialog = TimePickerDialog(context, style, onTimeSetListener, currentHour, currentMinute, true)
        timePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.setTitle("Selecciona una hora")
        timePickerDialog.show()
    }

    override fun deleteClick(posicion: Int) {
        val dialogoEliminar = AlertDialog.Builder(context)
        dialogoEliminar.setTitle("Eliminar Planificación")
        dialogoEliminar.setMessage("¿Seguro que deseas eliminar la planificación seleccionada?")
        dialogoEliminar.setCancelable(false)
        dialogoEliminar.setPositiveButton("Confirmar") { dialogoEliminar, id ->
            Toast.makeText(context, "Planificación eliminada", Toast.LENGTH_SHORT).show()
            plan.eliminarPlanificacion(actividad, planes!![posicion].id)
            planes!!.removeAt(posicion)
            adaptador!!.notifyDataSetChanged()
            //Mostramos un mensaje informando si la lista está vacía
            if (planes!!.isEmpty()) {
                listaPlanificaciones!!.visibility = View.GONE
                mensajePlanes!!.visibility = View.VISIBLE
            } else {
                listaPlanificaciones!!.visibility = View.VISIBLE
                mensajePlanes!!.visibility = View.GONE
            }
        }
        dialogoEliminar.setNegativeButton("Cancelar") { dialogoEliminar, id -> dialogoEliminar.dismiss() }
        dialogoEliminar.show()
    }

    override fun editClick(posicion: Int) {
        pictogramas = ArrayList()
        pictogramas = plan.obtenerPictogramasPlanificacion(actividad, planes!![posicion].id) as ArrayList<Pictograma>
        val intent = Intent(actividad, CrearPlanActivity::class.java)
        intent.putExtra("identificador", planes!![posicion].id)
        intent.putExtra("titulo", planes!![posicion].titulo)
        intent.putExtra("pictogramas", pictogramas)
        startActivity(intent)
    }

    override fun duplicateClick(posicion: Int) {
        pictogramas = ArrayList()
        pictogramas = plan.obtenerPictogramasPlanificacion(actividad, planes!![posicion].id) as ArrayList<Pictograma>
        val creada = plan.crearPlanificacion(actividad, pictogramas, planes!![posicion].titulo)
        if (creada) {
            Toast.makeText(context, "Planificación duplicada", Toast.LENGTH_LONG).show()
            iniciarListaPlanificaciones()
        } else {
            Toast.makeText(context, "Error al duplicar planificación", Toast.LENGTH_LONG).show()
        }
    }

    override fun planSeleccionado(posicion: Int) {
        val viewHolder = listaPlanificaciones!!.findViewHolderForAdapterPosition(posAnterior) as AdaptadorListaPlanes.ViewHolder?
        val card = viewHolder!!.itemView.findViewById<View>(R.id.card_plan) as CardView
        if (posicion != posAnterior) {
            card.setCardBackgroundColor(Color.WHITE)
        } else {
            card.setCardBackgroundColor(Color.rgb(224, 224, 224))
        }
        posAnterior = posicion
        planSeleccionado = 0
        nombreEvento = null.toString()
        planSeleccionado = planes!![posicion].id
        nombreEvento = planes!![posicion].titulo.toString()
        btn_guardar!!.isEnabled = true
    }
}
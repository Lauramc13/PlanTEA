package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class NuevoEventoFragment : Fragment(), AdaptadorListaPlanes.OnItemSelectedListener {
    lateinit var actividad: Activity
    lateinit var eventoInterface: EventoInterface
    lateinit var vista: View
    lateinit var btn_hora: Button
    lateinit var btn_guardar: Button
    lateinit var btn_planificar: Button
    lateinit var horaEvento: TextView
    lateinit var fechaEvento: TextView
    lateinit var mensajePlanes: TextView
    lateinit var cancelarEvento: ImageView
    lateinit var listaPlanificaciones: RecyclerView
    lateinit var adaptador: AdaptadorListaPlanes
    lateinit var planes: ArrayList<Planificacion>
    lateinit var layout_planificaciones: ConstraintLayout
    lateinit var btn_eliminar: Button
    lateinit var icono_cerrar_login: ImageView
    lateinit var btn_cancelar: Button
    var counter: Int = 1
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
        horaEvento = vista.findViewById(R.id.lbl_horaEvento)
        fechaEvento = vista.findViewById(R.id.lbl_fechaEvento)
        mensajePlanes = vista.findViewById(R.id.lbl_mensajePlanes)
        listaPlanificaciones = vista.findViewById(R.id.recycler_planificaciones)
        layout_planificaciones = vista.findViewById(R.id.layout)

        //Componentes deshabilitados al principio
        layout_planificaciones.visibility = View.GONE
        fechaEvento.text = formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada)
        consultas = pictograma.obtenerConsultas(actividad, 1) as ArrayList<String>
        btn_hora.setOnClickListener { mostrarReloj(horaEvento) }


        btn_guardar.setOnClickListener {
            val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
            val userId = prefs.getString("idUsuario", "")
            val evento = userId?.let { it1 ->
                Evento(
                    0,
                    it1,
                    nombreEvento,
                    CalendarioUtilidades.fechaSeleccionada,
                    horaEvento.text.toString(),
                    planSeleccionado,
                )
            }
            if (evento != null) {
                eventoInterface.nuevoEvento(evento)
            }
            Toast.makeText(context, "Evento creado", Toast.LENGTH_SHORT).show()
        }
        btn_planificar.setOnClickListener { eventoInterface.planificar() }
        cancelarEvento.setOnClickListener { eventoInterface.cancelarEvento() }
        return vista
    }

    fun obtenerImagenEvento(): String? {
        return pictograma.obtenerImagenEvento(actividad, 1)
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

    private fun iniciarListaPlanificaciones() {
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val userId = prefs.getString("idUsuario", "")
        listaPlanificaciones.layoutManager = LinearLayoutManager(context)
        planes = userId?.let { plan.mostrarPlanificacionesDisponibles(it, actividad) } as ArrayList<Planificacion>
        adaptador = AdaptadorListaPlanes(planes, this)
        listaPlanificaciones.adapter = adaptador
        //Mostramos un mensaje informando si la lista está vacía
        if (planes.isEmpty()) {
            listaPlanificaciones.visibility = View.GONE
            mensajePlanes.visibility = View.VISIBLE
        } else {
            listaPlanificaciones.visibility = View.VISIBLE
            mensajePlanes.visibility = View.GONE
        }
    }

    // private fun mostrarReloj(tiempo: TextView?) {
    //     val currentTime = Calendar.getInstance()
    //     val currentHour = currentTime[Calendar.HOUR_OF_DAY]
    //     val currentMinute = currentTime[Calendar.MINUTE]
    //     val onTimeSetListener = OnTimeSetListener { _, horaSeleccionada, minutoSeleccionado ->
    //         hora = horaSeleccionada
    //         minuto = minutoSeleccionado
    //         tiempo!!.text = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
    //         //Habilitamos el resto de componentes
    //         spinner_consultas.isEnabled = true
    //         horaEvento.setTextColor(Color.BLACK)
    //         layout_planificaciones.visibility = View.VISIBLE
    //         iniciarListaPlanificaciones()
    //     }
    //     val timePickerDialog = TimePickerDialog(context, onTimeSetListener, currentHour, currentMinute, true)
    //     timePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    //     timePickerDialog.setTitle("Selecciona una hora")
    //     timePickerDialog.show()
    // }

    private fun mostrarReloj(tiempo: TextView?) {

        val currentTime = Calendar.getInstance()
        val currentHour = currentTime[Calendar.HOUR_OF_DAY]
        val currentMinute = currentTime[Calendar.MINUTE]

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTheme(R.style.TimePicker)
            .setTitleText("Selecciona una hora")
            .build()

        picker.addOnPositiveButtonClickListener {
            val horaSeleccionada = picker.hour
            val minutoSeleccionado = picker.minute

            hora = horaSeleccionada
            minuto = minutoSeleccionado

            tiempo?.text = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)

            // Habilitamos el resto de componentes
            val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            if (isDarkMode) {
                horaEvento.setTextColor(Color.rgb(228, 231, 235))

            } else {
                horaEvento.setTextColor(Color.rgb(24, 31, 37))
            }

            layout_planificaciones.visibility = View.VISIBLE
            iniciarListaPlanificaciones()
            eventoInterface.clickReloj(tiempo?.text)
        }

        picker.show(requireFragmentManager(), "TimePicker")
    }


    override fun deleteClick(posicion: Int) {
        val dialogPlan = Dialog(actividad)
        dialogPlan.setContentView(R.layout.dialogo_eliminar_planificacion)
        dialogPlan.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_eliminar = dialogPlan.findViewById(R.id.btn_eliminarPlan)
        icono_cerrar_login = dialogPlan.findViewById(R.id.icono_CerrarDialogoEvento)
        btn_cancelar = dialogPlan.findViewById(R.id.btn_cancelarPlan)
        btn_eliminar.setOnClickListener {
            Toast.makeText(context, "Planificación eliminada", Toast.LENGTH_SHORT).show()
            plan.eliminarPlanificacion(actividad, planes[posicion].id)
            planes.removeAt(posicion)
            adaptador.notifyDataSetChanged()
            //Mostramos un mensaje informando si la lista está vacía
            if (planes.isEmpty()) {
                listaPlanificaciones.visibility = View.GONE
                mensajePlanes.visibility = View.VISIBLE
            } else {
                listaPlanificaciones.visibility = View.VISIBLE
                mensajePlanes.visibility = View.GONE
            }
            dialogPlan.dismiss()
        }
        btn_cancelar.setOnClickListener {
            dialogPlan.dismiss()
        }
        icono_cerrar_login.setOnClickListener { dialogPlan.dismiss() }
        dialogPlan.show()
    }


    override fun editClick(posicion: Int) {
        pictogramas = ArrayList()
        pictogramas = plan.obtenerPictogramasPlanificacion(actividad, planes[posicion].id) as ArrayList<Pictograma>
        val intent = Intent(actividad, CrearPlanActivity::class.java)
        intent.putExtra("identificador", planes[posicion].id)
        intent.putExtra("titulo", planes[posicion].titulo)
        intent.putExtra("pictogramas", pictogramas)
        startActivity(intent)
    }

    override fun duplicateClick(posicion: Int) {
        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val userId = prefs.getString("idUsuario", "")
        pictogramas = ArrayList()
        pictogramas = plan.obtenerPictogramasPlanificacion(actividad, planes[posicion].id) as ArrayList<Pictograma>
        val creada = userId?.let { plan.crearPlanificacion(actividad, it, planes[posicion].titulo + " " + counter.toString()) }
        //añadir pictogramas TODO
        counter++ //Incrementamos el contador para que el título de la planificación duplicada sea diferente
        if (creada != 0) {
            Toast.makeText(context, "Planificación duplicada", Toast.LENGTH_LONG).show()
            iniciarListaPlanificaciones()
        } else {
            Toast.makeText(context, "Error al duplicar planificación", Toast.LENGTH_LONG).show()
        }
    }

    override fun planSeleccionado(posicion: Int) {
        val viewHolder = listaPlanificaciones.findViewHolderForAdapterPosition(posAnterior) as AdaptadorListaPlanes.ViewHolder? // TODO: NULL POINTER EXCEPTION
        val card = viewHolder!!.itemView.findViewById<View>(R.id.card_plan) as CardView
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode) {
            if (posicion != posAnterior) {
                card.setCardBackgroundColor(Color.rgb(37, 47, 56))
            }
        } else {
            if (posicion != posAnterior) {
                card.setCardBackgroundColor(Color.WHITE)
            }
        }
        posAnterior = posicion
        planSeleccionado = 0
        nombreEvento = null.toString()
        planSeleccionado = planes[posicion].id
        nombreEvento = planes[posicion].titulo
        btn_guardar.isEnabled = true
    }
}
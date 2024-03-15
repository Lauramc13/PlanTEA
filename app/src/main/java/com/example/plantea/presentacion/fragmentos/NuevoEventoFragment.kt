package com.example.plantea.presentacion.fragmentos

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoDiaMes
import com.example.plantea.dominio.CalendarioUtilidades.formatoFechaEvento
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import android.app.NotificationManager
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.setIcon
import com.example.plantea.presentacion.adaptadores.AdaptadorListaPlanes
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import java.util.*


class NuevoEventoFragment : BottomSheetDialogFragment(), AdaptadorListaPlanes.OnItemSelectedListener {
    lateinit var actividad: Activity
    lateinit var vista: View
    private lateinit var btnHora: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnPlanificar: Button
   // private lateinit var tituloEvento : EditText
    private lateinit var fechaEvento: TextView
    private lateinit var mensajePlanes: TextView
    private lateinit var cancelarEvento: ImageView
    private lateinit var listaPlanificaciones: RecyclerView
    private lateinit var adaptador: AdaptadorListaPlanes
    private lateinit var layout_planificaciones: ConstraintLayout
    private lateinit var switchReminder : SwitchCompat
   // private lateinit var reminderview: FragmentContainerView

    private val viewModel by activityViewModels<CalendarioViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_nuevo_evento, container, false)
        // view background transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

        cancelarEvento = vista.findViewById(R.id.img_cancelarEvento)
        btnHora = vista.findViewById(R.id.btn_horaEvento)
        btnGuardar = vista.findViewById(R.id.btn_guardarEvento)
        btnPlanificar = vista.findViewById(R.id.btn_planificar)
        fechaEvento = vista.findViewById(R.id.lbl_fechaEvento)
        mensajePlanes = vista.findViewById(R.id.lbl_mensajePlanes)
        listaPlanificaciones = vista.findViewById(R.id.recycler_planificaciones)
        layout_planificaciones = vista.findViewById(R.id.layout)
        //tituloEvento = vista.findViewById(R.id.txt_tituloEvento)
        switchReminder = vista.findViewById(R.id.switch_recordatorio)
        //reminderview = vista.findViewById(R.id.fragment_container_view)

       // iniciarListaPlanificaciones()

        //if(viewModel.isClickedReloj){

       /* }else{
            layout_planificaciones.visibility = View.GONE
        }*/

        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        viewModel.setIdUsario(prefs)

        fechaEvento.text = formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada)
        //consultas = pictograma.obtenerConsultas(actividad, 1) as ArrayList<String>
        btnHora.setOnClickListener { mostrarReloj() }

        btnGuardar.setOnClickListener {

          /*  if(tituloEvento.text.toString().isEmpty()){
                tituloEvento.error = "Introduce un título"
                return@setOnClickListener
            }*/

            if(btnHora.text.toString().isEmpty()){
                //color de texto
                btnHora.setHintTextColor(Color.RED)
                return@setOnClickListener
            }

            val tituloEvento = viewModel.planes[viewModel.posicionPlan].titulo + " - " + formatoDiaMes(CalendarioUtilidades.fechaSeleccionada)

            val evento = Evento(0, viewModel.idUsuario, tituloEvento, CalendarioUtilidades.fechaSeleccionada, btnHora.text.toString(),viewModel.planSeleccionado)
            context?.let { it1 -> viewModel.nuevoEvento(it1, evento) }
            dismiss()
        }

        btnPlanificar.setOnClickListener { context?.let { it1 -> viewModel.planificar(it1) } }
        cancelarEvento.setOnClickListener {
            if(CommonUtils.isMobile(requireContext())) {
                switchReminder.isChecked = false
                dismiss()
            } else{
                context?.let { it1 -> viewModel.cancelarEvento(it1) }
            }
        }

        switchReminder.setOnCheckedChangeListener { _, isChecked ->

            if(CommonUtils.isMobile(requireContext())) {
                showReminderFragment(isChecked)
            } else{
                if(isChecked){
                    val requestPermissionLauncher =
                        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                            if (isGranted) {
                                // Permission granted, create the reminder dialog
                                createDialogReminder()
                            } else {
                                // Permission not granted, handle accordingly
                                switchReminder.isChecked = false
                                CommonUtils.showSnackbar(
                                    vista,
                                    requireContext(),
                                    "No se puede activar el recordatorio sin permisos de notificación"
                                )
                            }
                        }
                    requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY)

                }

            }


        }


        dialog?.setOnCancelListener { switchReminder.isChecked = false }

        return vista
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
    }

    private fun showReminderFragment(isChecked: Boolean) {
        val transaction = childFragmentManager.beginTransaction()

        if (isChecked) {
            // createDialogReminder()
            val fragment = ReminderFragment()
            transaction.replace(R.id.fragment_container_view, fragment)
            transaction.addToBackStack(null)

        }else{
            val existingFragment = childFragmentManager.findFragmentById(R.id.fragment_container_view)
            if (existingFragment is ReminderFragment) {
                transaction.remove(existingFragment)
                transaction.addToBackStack(null)  // Optional: Add to back stack if you want to navigate back
            }
        }
        transaction.commit()
    }

    private fun createDialogReminder(){
        val dialogReminder = Dialog(actividad)
        dialogReminder.setContentView(R.layout.dialog_reminder)
        dialogReminder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnAceptar = dialogReminder.findViewById<Button>(R.id.btn_aceptarRecordatorio)
        val iconCerrar = dialogReminder.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val checkboxMin = dialogReminder.findViewById<SwitchCompat>(R.id.checkBox_min)
        val checkboxHora = dialogReminder.findViewById<SwitchCompat>(R.id.checkBox_hora)
        val checkboxDia = dialogReminder.findViewById<SwitchCompat>(R.id.checkBox_dia)
        val checkBoxPersonalizar = dialogReminder.findViewById<SwitchCompat>(R.id.checkBox_personalizar)

        btnAceptar.setOnClickListener {
            //si no se ha seleccionado ningun checkbox poner el switch a false
            if(!checkBoxPersonalizar.isChecked && !checkboxMin.isChecked && !checkboxHora.isChecked && !checkboxDia.isChecked){
                switchReminder.isChecked = false
            }
            //delete dialog
            dialogReminder.dismiss()
        }

        checkBoxPersonalizar.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxPer = isChecked
            if (isChecked) {
                val picker = viewModel.createReloj()
                picker.addOnPositiveButtonClickListener {
                    viewModel.selectedHour = picker.hour
                    viewModel.selectedMin = picker.minute
                    checkBoxPersonalizar.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.selectedHour, viewModel.selectedMin)
                }
                picker.show(requireFragmentManager(), "TimePicker")
            }
        }

        checkboxMin.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxMin = isChecked
        }

        checkboxHora.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxHora = isChecked
        }

        checkboxDia.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxDia = isChecked
        }

        iconCerrar.setOnClickListener {
            if(!checkBoxPersonalizar.isChecked && !checkboxMin.isChecked && !checkboxHora.isChecked && !checkboxDia.isChecked){
                switchReminder.isChecked = false
            }
            dialogReminder.dismiss()
        }
        dialogReminder.show()
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

            //mostrarPlanificaciones()
            btnHora.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.hora, viewModel.minuto)

        }

        picker.show(requireFragmentManager(), "TimePicker")
    }

   /* private fun mostrarPlanificaciones(){
        btnHora.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.hora, viewModel.minuto)

        //layout_planificaciones.visibility = View.VISIBLE
        iniciarListaPlanificaciones()
    }
*/

    override fun deleteClick(posicion: Int) {
        val dialogPlan = Dialog(actividad)
        dialogPlan.setContentView(R.layout.dialogo_eliminar_planificacion)
        dialogPlan.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnEliminar = dialogPlan.findViewById<Button>(R.id.btn_eliminarPlan)
        val iconCerrar = dialogPlan.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val btnCancelar = dialogPlan.findViewById<Button>(R.id.btn_cancelarPlan)

        btnEliminar.setOnClickListener {
            CommonUtils.showSnackbar(vista, requireContext(), "Planificación eliminada")
            viewModel.plan.eliminarPlanificacion(actividad, viewModel.planes[posicion].id)
            adaptador.notifyItemRemoved(posicion)
            viewModel.planes.removeAt(posicion)
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
        if (creada != 0) {
            CommonUtils.showSnackbar(vista, requireContext(), "Planificación duplicada")
            val planificacion = viewModel.planes[posicion]
            planificacion.titulo = planificacion.titulo + " " + viewModel.counter.toString()
            viewModel.planes.add(planificacion)
            adaptador.notifyItemInserted(viewModel.planes.size)
            listaPlanificaciones.scrollToPosition(adaptador.itemCount - 1)
        } else {
            CommonUtils.showSnackbar(vista, requireContext(), "Error al duplicar planificación")

        }
        viewModel.counter++ //Incrementamos el contador para que el título de la planificación duplicada sea diferente

    }

    override fun planSeleccionado(posicion: Int) {
        viewModel.configureDataPlanSeleccionado(posicion)
        btnGuardar.isEnabled = true
    }

    private fun visibilityPlan(){
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
        iniciarListaPlanificaciones()
        if(viewModel.isPlanSeleccionado){
            adaptador.setItemSelected(viewModel.posicionPlan)
            btnGuardar.isEnabled = true
        }
        viewModel.planes.clear()
        viewModel.planes.addAll(viewModel.idUsuario.let { viewModel.plan.mostrarPlanificacionesDisponibles(it, actividad) } as ArrayList<Planificacion>)
        adaptador.notifyDataSetChanged()
    }
}
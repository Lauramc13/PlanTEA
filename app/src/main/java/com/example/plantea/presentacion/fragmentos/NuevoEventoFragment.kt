package com.example.plantea.presentacion.fragmentos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoFechaEvento
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CrearPlanActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorListaPlanes
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import java.util.Locale

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
    private lateinit var layoutPlanificaciones: ConstraintLayout
    private lateinit var switchReminder : SwitchCompat
    private lateinit var radioButtonVista : CheckBox
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    private val viewModel by activityViewModels<CalendarioViewModel>()
    private var permisosGranted = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permisosGranted = isGranted
    }


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
        layoutPlanificaciones = vista.findViewById(R.id.layout)
        //tituloEvento = vista.findViewById(R.id.txt_tituloEvento)
        switchReminder = vista.findViewById(R.id.switch_recordatorio)
        radioButtonVista = vista.findViewById(R.id.switch_hacer_visible)

        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        viewModel.setIdUsario(prefs)

        fechaEvento.text = formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada)
        //consultas = pictograma.obtenerConsultas(actividad, 1) as ArrayList<String>
        btnHora.setOnClickListener { mostrarReloj() }

        btnGuardar.setOnClickListener {
            if(btnHora.text.toString().isEmpty()){
                btnHora.setHintTextColor(Color.RED)
                return@setOnClickListener
            }

            val evento = Evento(0, viewModel.idUsuario, viewModel.planes[viewModel.posicionPlan].titulo, CalendarioUtilidades.fechaSeleccionada, btnHora.text.toString(),viewModel.planSeleccionado, radioButtonVista.isChecked)
            context?.let { it1 -> viewModel.nuevoEvento(it1, evento)}
            dismiss()
        }

        configurarEvento()

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val idPlan = intent?.getIntExtra("idPlan", 0)
                if (idPlan != null) {
                    if(!intent.getBooleanExtra("isNuevo", false)){
                        viewModel.posicionPlan = viewModel.planes.size
                    }else{
                        for (i in viewModel.planes.indices) {
                            if (viewModel.planes[i].id == idPlan) {
                                viewModel.posicionPlan = i
                                break
                            }
                        }
                    }
                    btnGuardar.isEnabled = true
                    viewModel.isPlanSeleccionado = true
                    viewModel.planSeleccionado = idPlan
                    adaptador.setItemSelected(viewModel.posicionPlan)
                }
            }
        }

        btnPlanificar.setOnClickListener {
            val intent = Intent(context, CrearPlanActivity::class.java)
            startForResult.launch(intent)
        }

        cancelarEvento.setOnClickListener {
            if(CommonUtils.isMobile(requireContext())) {
                switchReminder.isChecked = false
                dismiss()
            } else{
                context?.let { it1 -> viewModel.cancelarEvento(it1) }
            }
        }

        askNotificationPermission()

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                if(permisosGranted){
                  /*  if(CommonUtils.isMobile(requireContext())) {
                        showReminderFragment(isChecked)
                    } else{*/
                        createDialogReminder()
                //}
                }else{
                    switchReminder.isChecked = false
                    Toast.makeText(requireContext(), R.string.toast_permisos, Toast.LENGTH_SHORT).show()
                }
            }/*else{
                showReminderFragment(isChecked)*/
            //}
        }

        dialog?.setOnCancelListener { switchReminder.isChecked = false }

        return vista
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                permisosGranted = true
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                val rootView = activity?.window?.decorView?.findViewById<View>(android.R.id.content)

                val snackbar = rootView?.let { Snackbar.make(it, R.string.toast_permisos, Snackbar.LENGTH_INDEFINITE) }
                snackbar?.setAction("OK") {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                snackbar?.show()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actividad = context
        }
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
                val picker = viewModel.createReloj(requireContext())
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
        val picker = viewModel.createReloj(requireContext())

        picker.addOnPositiveButtonClickListener {
            viewModel.isClickedReloj = true
            viewModel.hora = picker.hour
            viewModel.minuto = picker.minute

            //mostrarPlanificaciones()
            btnHora.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.hora, viewModel.minuto)

        }

        picker.show(requireFragmentManager(), "TimePicker")
    }

    override fun deleteClick(posicion: Int) {
        val dialogPlan = Dialog(actividad)
        dialogPlan.setContentView(R.layout.dialogo_eliminar_planificacion)
        dialogPlan.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnEliminar = dialogPlan.findViewById<Button>(R.id.btn_eliminarPlan)
        val iconCerrar = dialogPlan.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val btnCancelar = dialogPlan.findViewById<Button>(R.id.btn_cancelarPlan)

        btnEliminar.setOnClickListener {
            Toast.makeText(actividad, R.string.toast_planificacion_eliminada, Toast.LENGTH_SHORT).show()
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
        viewModel.editarClick(actividad, posicion, startForResult)
    }

    override fun duplicateClick(posicion: Int) {
        val pictogramas = viewModel.plan.obtenerPictogramasPlanificacion(actividad, viewModel.planes[posicion].id, Locale.getDefault().language) as ArrayList<Pictograma>
        val creada = viewModel.idUsuario.let { viewModel.plan.crearPlanificacion(actividad, it, viewModel.planes[posicion].titulo + " " + viewModel.counter.toString()) }
        viewModel.plan.addPictogramasPlan(creada, actividad, pictogramas)

        if (creada != 0) {
            Log.d("idPlan", creada.toString())
            Toast.makeText(actividad, R.string.toast_planificacion_duplicada, Toast.LENGTH_SHORT).show()
            val planificacion = viewModel.planes[posicion]
            planificacion.titulo = planificacion.titulo + " " + viewModel.counter.toString()
            planificacion.id = creada
            viewModel.planes.add(planificacion)
            adaptador.notifyItemInserted(viewModel.planes.size)
            listaPlanificaciones.scrollToPosition(adaptador.itemCount - 1)
        } else {
            Toast.makeText(actividad, R.string.toast_error_planificacion_duplicada, Toast.LENGTH_SHORT).show()

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

    private fun configurarEvento() {
        iniciarListaPlanificaciones()
        if(viewModel.isPlanSeleccionado){
            adaptador.setItemSelected(viewModel.posicionPlan)
            btnGuardar.isEnabled = true
        }
        viewModel.planes.clear()
        viewModel.planes.addAll(viewModel.idUsuario.let { viewModel.plan.mostrarPlanificacionesDisponibles(it, actividad) } as ArrayList<Planificacion>)
        adaptador.notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        configurarEvento()
     }
}
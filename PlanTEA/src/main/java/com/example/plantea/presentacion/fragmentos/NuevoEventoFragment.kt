package com.example.plantea.presentacion.fragmentos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.CalendarioUtilidades.formatoFechaEvento
import com.example.plantea.dominio.objetos.Evento
import com.example.plantea.dominio.objetos.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.EventosPlanificadorActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanesEventos
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.example.plantea.presentacion.viewModels.EventosPlanificadorViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialSharedAxis
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import kotlin.time.ExperimentalTime


class NuevoEventoFragment : BottomSheetDialogFragment(), AdaptadorPlanesEventos.OnItemSelectedListener {
    lateinit var actividad: Activity
    lateinit var vista: View
    private lateinit var btnHoraInicio: TextView
    private lateinit var btnHoraFin: TextView
    private lateinit var localizacion: TextInputLayout
    private lateinit var notas: TextInputLayout
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnBorrar: Button
//    private lateinit var btnPlanificar: Button
    private lateinit var fechaEvento: TextView
  //  private lateinit var mensajePlanes: TextView
    private lateinit var cancelarEvento: ImageView
    lateinit var listaPlanificaciones: RecyclerView
    private lateinit var adaptador: AdaptadorPlanesEventos
    private lateinit var layoutPlanificaciones: ConstraintLayout
    private lateinit var switchReminder : MaterialSwitch
    private lateinit var switchAllDay: MaterialSwitch
    private lateinit var layoutHora : LinearLayout
    private lateinit var radioButtonVista : MaterialSwitch

    private val viewModel by activityViewModels<CalendarioViewModel>()
    private val viewModelEventosPlanificador by activityViewModels<EventosPlanificadorViewModel>()

    private var permisosGranted = false

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted: Boolean -> permisosGranted = isGranted
    }

    companion object {
        fun newInstance(idEvento: Int?, date: LocalDate?, horaInicio: String?, horaFin:String?, localizacion: String?, notas:String?, idPlan: Int?, changeVisibility: Boolean?): NuevoEventoFragment {
            val fragment = NuevoEventoFragment()
            val args = Bundle()
            if (idEvento != null) {
                args.putInt("arg_idEvento", idEvento)
            }
            args.putString("arg_date", date.toString())
            args.putString("arg_hour_start", horaInicio.toString())
            args.putString("arg_hour_end", horaFin.toString())
            args.putString("arg_localizacion", localizacion)
            args.putString("arg_notas", notas)
            if (idPlan != null) {
                args.putInt("arg_idPlan" , idPlan)
            }
            if (changeVisibility != null) {
                args.putBoolean("arg_change_visibility", changeVisibility)
            }

            fragment.arguments = args
            return fragment
        }
    }

    @androidx.annotation.RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vista = inflater.inflate(R.layout.fragment_nuevo_evento, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)

        cancelarEvento = vista.findViewById(R.id.img_cancelarEvento)
        btnHoraInicio = vista.findViewById(R.id.btn_horaEvento)
        btnHoraFin = vista.findViewById(R.id.btn_horaFinEvento)
        localizacion = vista.findViewById(R.id.localizacionText)
        notas = vista.findViewById(R.id.notasText)
        btnGuardar = vista.findViewById(R.id.btn_guardarEvento)
        btnBorrar = vista.findViewById(R.id.btn_borrarEvento)
        fechaEvento = vista.findViewById(R.id.lbl_fechaEvento)
        listaPlanificaciones = vista.findViewById(R.id.recycler_planificaciones)
        layoutPlanificaciones = vista.findViewById(R.id.layout)
        switchReminder = vista.findViewById(R.id.switch_recordatorio)
        layoutHora = vista.findViewById(R.id.layout_hora)
        switchAllDay = vista.findViewById(R.id.switch_allDay)
        radioButtonVista = vista.findViewById(R.id.switch_hacer_visible)

        val prefs = this.requireActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        viewModel.configureUser(prefs)

        fechaEvento.text = formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada)
        fechaEvento.setOnClickListener { mostrarCalendario() }

        btnHoraInicio.setOnClickListener { mostrarReloj(true) }
        btnHoraFin.setOnClickListener { mostrarReloj(false) }

        btnGuardar.setOnClickListener {
            var horaInicio : String? = null
            var horaFin : String? = null

            if(!switchAllDay.isChecked){
                if(btnHoraInicio.text.toString().isEmpty()){
                    btnHoraInicio.setHintTextColor(Color.RED)
                    btnHoraFin.setHintTextColor(Color.RED)
                    Toast.makeText(requireContext(), R.string.toast_no_se_ha_seleccionado_hora, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (btnHoraFin.text.toString().isEmpty()){
                    btnHoraInicio.setHintTextColor(Color.RED)
                    btnHoraFin.setHintTextColor(Color.RED)
                    Toast.makeText(requireContext(), R.string.toast_no_se_ha_seleccionado_hora, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                horaInicio = btnHoraInicio.text.toString()
                horaFin = btnHoraFin.text.toString()
            }

            var idUserTEA = prefs.getString("idUsuarioTEA", null)
            if (idUserTEA == null || idUserTEA == "") {
                idUserTEA = viewModel.idUsuario
            }

            val evento = Evento(viewModel.eventoIdEdited, idUserTEA, viewModel.planes[viewModel.posicionPlan].titulo, CalendarioUtilidades.fechaSeleccionada, horaInicio, horaFin, localizacion.editText?.text.toString(), notas.editText?.text.toString(), viewModel.planSeleccionado, radioButtonVista.isChecked, null)
            val parentActivity: EventosPlanificadorActivity? = activity as EventosPlanificadorActivity?

            val pictosEvento = if(viewModelEventosPlanificador.checkInitializedVariable()){
                viewModelEventosPlanificador.pictosEvento
            }else{
                null
            }

            viewModel.nuevoEventoEdit(requireContext(), evento, pictosEvento, parentActivity)

            dismiss()
        }

        btnBorrar.setOnClickListener {
            borrarEvento()
        }

        configurarEvento()

        cancelarEvento.setOnClickListener {
            if(viewModel.isEditing){
                viewModel.cancelarEventoEdit()
            }
            if(CommonUtils.isMobile(requireContext())) {
                switchReminder.isChecked = false
                dismiss()
            } else{
                val parentActivity: Activity? = activity
                if(parentActivity is EventosPlanificadorActivity){
                    parentActivity.expand(false, CommonUtils.isPortrait(parentActivity), false)
                }else{
                    context?.let { it1 -> viewModel.cancelarEvento(it1) }
                }
            }
        }

        askExactAlarmPermission()
        askNotificationPermission()
        configurarEventoEdit()

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                if(permisosGranted){
                    createDialogReminder()
                }else{
                    switchReminder.isChecked = false
                    Toast.makeText(requireContext(), R.string.toast_permisos, Toast.LENGTH_SHORT).show()
                }
            }/*else{
                showReminderFragment(isChecked)*/
            //}
        }

        switchAllDay.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                btnHoraInicio.isEnabled = false
                btnHoraFin.isEnabled = false
                layoutHora.visibility = View.GONE
            }else{
                btnHoraInicio.isEnabled = true
                btnHoraFin.isEnabled = true
                layoutHora.visibility = View.VISIBLE
            }
        }

        dialog?.setOnCancelListener { switchReminder.isChecked = false }

        viewModel.sePlanSeleccionado.observe(viewLifecycleOwner) {
            viewModel.isPlanSeleccionado = true
            viewModel.posicionPlan = it
            btnGuardar.isEnabled = true
        }

        return vista
    }

    private fun configurarEventoEdit(){
        // get data from arguments if the fragment is called to be edited
        val idEventoEdit = arguments?.getInt("arg_idEvento")
        val idPlanEdit = arguments?.getInt("arg_idPlan")
        val dateEdit = arguments?.getString("arg_date")
        val hourStartEdit = arguments?.getString("arg_hour_start")
        val hourFinEdit = arguments?.getString("arg_hour_end")
        val localizacionEdit = arguments?.getString("arg_localizacion")
        val notasEdit = arguments?.getString("arg_notas")

        val reminder = arguments?.getString("arg_reminder")
        val change_visibility = arguments?.getBoolean("arg_change_visibility")
        val label = vista.findViewById<TextView>(R.id.lbl_nuevoEvento)


        //set the date and hour if the fragment is called to be edited
        if (idPlanEdit != null && dateEdit != null) {
            viewModel.isEditing = true
            val localDate = LocalDate.parse(dateEdit)
            fechaEvento.text = formatoFechaEvento(localDate)
            if(hourStartEdit != "null"){
                btnHoraInicio.text = hourStartEdit
                btnHoraFin.text = hourFinEdit
            }else{
                switchAllDay.isChecked = true
                layoutHora.visibility = View.GONE
            }

            localizacionEdit?.let { localizacion.editText?.setText(it) }
            notasEdit?.let { notas.editText?.setText(it) }
            for (i in viewModel.planes.indices) {
                if (viewModel.planes[i].id!!.toInt() == idPlanEdit) {
                    viewModel.posicionPlan = i
                    break
                }
            }
            viewModel.eventoIdEdited = idEventoEdit!!
            btnGuardar.isEnabled = true
            btnBorrar.visibility = View.VISIBLE
            viewModel.isPlanSeleccionado = true
            viewModel.planSeleccionado = idPlanEdit
            adaptador.setItemSelected(viewModel.posicionPlan)
            label.text = getString(R.string.editar_evento)

        }else{
            viewModel.eventoIdEdited = 0
            viewModel.isEditing = false
            viewModel.isPlanSeleccionado = false
            adaptador.setItemSelected(-1)
            btnGuardar.isEnabled = false
        }

        radioButtonVista.isChecked = change_visibility ?: false
        switchReminder.isChecked = reminder != null
    }


    private fun askExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {

                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)

            }
        }
    }

    private fun askNotificationPermission() {
        try {
            // This is only necessary for API level >= 33 (TIRAMISU)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    permisosGranted = true
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    val rootView =if(CommonUtils.isMobile(requireContext())) {
                        view
                    }else{
                        activity?.window?.decorView?.findViewById(android.R.id.content)
                    }

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
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al solicitar permisos", Toast.LENGTH_SHORT).show()
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
        val checkbox5Min = dialogReminder.findViewById<MaterialSwitch>(R.id.checkBox_5min)
        val checkbox15Min = dialogReminder.findViewById<MaterialSwitch>(R.id.checkBox_15min)
        val checkbox30Min = dialogReminder.findViewById<MaterialSwitch>(R.id.checkBox_30min)
        val checkbox1Hora = dialogReminder.findViewById<MaterialSwitch>(R.id.checkBox_1hora)
        val checkboxDia = dialogReminder.findViewById<MaterialSwitch>(R.id.checkBox_dia)
        val checkBoxPersonalizar = dialogReminder.findViewById<MaterialSwitch>(R.id.checkBox_personalizar)

        btnAceptar.setOnClickListener {
            //si no se ha seleccionado ningun checkbox poner el switch a false
            checkReminderChecked(checkBoxPersonalizar, checkbox5Min, checkbox15Min, checkbox30Min, checkbox1Hora, checkboxDia)
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

        checkbox5Min.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBox5Min = isChecked
        }

        checkbox15Min.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBox15Min = isChecked
        }

        checkbox30Min.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBox30Min = isChecked
        }

        checkbox1Hora.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBox1Hora = isChecked
        }

        checkboxDia.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkBoxDia = isChecked
        }

        iconCerrar.setOnClickListener {
            checkReminderChecked(checkBoxPersonalizar, checkbox5Min, checkbox15Min, checkbox30Min, checkbox1Hora, checkboxDia)
            dialogReminder.dismiss()
        }

        dialogReminder.setOnCancelListener {
            checkReminderChecked(checkBoxPersonalizar, checkbox5Min, checkbox15Min, checkbox30Min, checkbox1Hora, checkboxDia)
        }
        dialogReminder.show()
    }

    private fun iniciarListaPlanificaciones(vista: View) {
        listaPlanificaciones.layoutManager = LinearLayoutManager(context)
        viewModel.planes = viewModel.gPlan.mostrarPlanificacionesDisponibles(viewModel.idUsuarioPlanner, actividad) as ArrayList<Planificacion>
        if(viewModel.planes.isEmpty()){
            vista.findViewById<TextView>(R.id.lbl_mensajePlanes).visibility = View.VISIBLE
        }else{
            vista.findViewById<TextView>(R.id.lbl_mensajePlanes).visibility = View.GONE
        }
        adaptador = AdaptadorPlanesEventos(viewModel.planes, this)
        listaPlanificaciones.adapter = adaptador
    }

    private fun mostrarReloj(isStart: Boolean) {
        val picker = viewModel.createReloj(requireContext())

        picker.addOnPositiveButtonClickListener {
            if(btnHoraInicio.text.toString().isNotEmpty() && !isStart){
                //si la hora que se ha elegido es menor que btnHoraInicio
                if(checkHoraFin(picker.hour, picker.minute)){
                    Toast.makeText(requireContext(), "La hora de fin no puede ser menor a la de inicio", Toast.LENGTH_SHORT).show()
                    return@addOnPositiveButtonClickListener
                }
            }

            if(btnHoraFin.text.toString().isNotEmpty() && isStart){
                if(checkHoraInicio(picker.hour, picker.minute)){
                    Toast.makeText(requireContext(), "La hora de inicio no puede ser mayor a la de fin", Toast.LENGTH_SHORT).show()
                    return@addOnPositiveButtonClickListener
                }
            }

            viewModel.isClickedReloj = true
            viewModel.hora = picker.hour
            viewModel.minuto = picker.minute

            if(isStart) {
                btnHoraInicio.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.hora, viewModel.minuto)
            } else {
                btnHoraFin.text = String.format(Locale.getDefault(), "%02d:%02d", viewModel.hora, viewModel.minuto)
            }
        }

        picker.show(requireFragmentManager(), "TimePicker")
    }

    @OptIn(ExperimentalTime::class)
    private fun mostrarCalendario(){

        val picker = viewModel.mostrarCalendario()

        picker.addOnPositiveButtonClickListener {
            CalendarioUtilidades.fechaSeleccionada = Instant.ofEpochMilli(picker.selection!!).atZone(ZoneId.systemDefault()).toLocalDate()
            fechaEvento.text = formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada)
        }

        // Forzar locale antes de mostrar
        val config = resources.configuration
        config.setLocale(Locale.getDefault())
        context?.createConfigurationContext(config)

        picker.show(requireFragmentManager(), "CalendarPicker")

    }

    override fun planSeleccionado(posicion: Int, context: Context) {
        val parent = activity as EventosPlanificadorActivity
        viewModel.configureDataPlanSeleccionado(posicion, parent)
        btnGuardar.isEnabled = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun configurarEvento() {
        iniciarListaPlanificaciones(vista)
        if(viewModel.isPlanSeleccionado){
            adaptador.setItemSelected(viewModel.posicionPlan)
            btnGuardar.isEnabled = true
        }
    }

    private fun borrarEvento(){
        val parentActivity: EventosPlanificadorActivity? = activity as EventosPlanificadorActivity?

        val dialog = Dialog(actividad)
        dialog.setContentView(R.layout.dialogo_eliminar_evento)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnEliminar = dialog.findViewById<Button>(R.id.btn_eliminar)
        val atras = dialog.findViewById<Button>(R.id.btn_cancelar)
        val btnCancelar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)

        atras.setOnClickListener {
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnEliminar.setOnClickListener{
            //find position of the event to be deleted
            val posicion = parentActivity?.viewModel?.eventos?.indexOfFirst { it.id == viewModel.eventoIdEdited }
            parentActivity?.viewModel?.deleteEvento(actividad, requireContext(), posicion!!)
            parentActivity?.adaptador?.notifyItemRemoved(posicion!!)

            viewModel.closeFragment(parentActivity)
            viewModel.isEditing = false
            viewModel.cancelarEventoEdit()


            dialog.dismiss()
        }

        dialog.show()
    }

    private fun checkReminderChecked(cb1: MaterialSwitch, cb2: MaterialSwitch, cb3: MaterialSwitch, cb4: MaterialSwitch, cb5: MaterialSwitch, cb6: MaterialSwitch){
        if(!cb1.isChecked && !cb2.isChecked && !cb3.isChecked && !cb4.isChecked && !cb5.isChecked && !cb6.isChecked ){
            switchReminder.isChecked = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        configurarEvento()
    }

    private fun checkHoraFin(horaFin: Int, minutoFin: Int): Boolean{
        val inicio = btnHoraInicio.text.toString().split(":")
        val horaInicio = inicio[0].toInt()
        val minutoInicio = inicio[1].toInt()

        return horaFin < horaInicio || (horaFin == horaInicio && minutoFin < minutoInicio)
    }

    private fun checkHoraInicio(horaInicio: Int, minutoInicio: Int): Boolean{
        val fin = btnHoraFin.text.toString().split(":")
        val horaFin = fin[0].toInt()
        val minutoFin = fin[1].toInt()

        return horaInicio > horaFin || (horaInicio == horaFin && minutoInicio > minutoFin)
    }
}
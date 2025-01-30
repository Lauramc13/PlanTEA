package com.example.plantea.presentacion.actividades

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.adaptadores.AdaptadorListaEventos
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramaEntretenimiento
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasEventos
import com.example.plantea.presentacion.fragmentos.CalendarioFragment
import com.example.plantea.presentacion.fragmentos.NuevoEventoFragment
import com.example.plantea.presentacion.viewModels.CalendarioViewModel
import com.example.plantea.presentacion.viewModels.EventosPlanificadorViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale

class EventosPlanificadorActivity : AppCompatActivity(), AdaptadorListaEventos.OnItemSelectedListener, AdaptadorPictogramasEventos.OnItemSelectedListener {

    val viewModel: EventosPlanificadorViewModel by viewModels()
    val viewModelCalendario : CalendarioViewModel by viewModels()

    lateinit var listaEventos: RecyclerView
    lateinit var adaptador: AdaptadorListaEventos
    private lateinit var fragmentEdit: FragmentContainerView
    private lateinit var fragmentLayout: LinearLayout
    private lateinit var  dialogEntretenimiento: Dialog
    private var entretenimientoPosition = -1
    private lateinit var btnNuevaPlanificacion: MaterialButton
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_planificador)
        val atras = findViewById<Button>(R.id.atras)
        val nuevoEvento = findViewById<Button>(R.id.btn_eventos)
        val calendarButton = findViewById<MaterialButton>(R.id.btn_calendario)
        fragmentEdit = findViewById(R.id.linearLayout16)
        fragmentLayout = findViewById(R.id.linearLayoutFragment)
        btnNuevaPlanificacion = findViewById(R.id.btn_nuevo_plan)

        // Si se va hacia atras y no hay nada en la cola, se redirige a MainActivity
        val callback = viewModel.backCallBack(this)
        onBackPressedDispatcher.addCallback(this, callback)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs)

        listaEventos = findViewById(R.id.recycler_eventos)

        atras?.setOnClickListener {
            finish()
        }

        nuevoEvento.setOnClickListener {
            btnNuevaPlanificacion.visibility = View.VISIBLE
            CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
            if(CommonUtils.isMobile(this) && CommonUtils.isPortrait(this)) {
                bottomSheetDialog(this, null)
            }else{
                viewModel.fragment = NuevoEventoFragment()
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.linearLayout16, viewModel.fragment)
                ft.addToBackStack(null)
                ft.commit()

                expand(true, CommonUtils.isPortrait(this), false)
            }
        }

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val idPlan = intent?.getIntExtra("idPlan", 0)
                var posicionPlan = 0
                if (idPlan != null) {
                    if(!intent.getBooleanExtra("isNuevo", false)){
                        viewModel.posicionEvento = viewModelCalendario.planes.size
                        posicionPlan = viewModelCalendario.planes.size
                    }

                    viewModelCalendario.planSeleccionado = idPlan
                    viewModelCalendario._planSeleccionado.value = posicionPlan
                }
            }
        }

        btnNuevaPlanificacion.setOnClickListener {
            val intent = Intent(this, CrearPlanActivity::class.java)
            startForResult.launch(intent)
        }

        calendarButton.setOnClickListener{
            if(CommonUtils.isMobile(this)){
                calendarDialog()
            }else{
                btnNuevaPlanificacion.visibility = View.GONE
                calendarSection()
                expand(true, CommonUtils.isPortrait(this), true)
            }

        }

        iniciarListaPlanificaciones()
        calendarSection()

        fragmentLayout.post{
            val parent = fragmentLayout.parent as? View
            if (parent != null) {
                if (CommonUtils.isPortrait(this)) {
                    val targetHeight = (parent.height * 0.55).toInt()
                    fragmentLayout.layoutParams.height = targetHeight
                } else {
                    val targetWidth = (parent.width * 0.4).toInt()
                    fragmentLayout.layoutParams.width = targetWidth
                }
                fragmentLayout.requestLayout()
            }
        }

        observer()
    }

    private fun observer() {
        viewModel._idPictoEntretenimiento.observe(this){
            viewModel.pictosEvento[entretenimientoPosition].pictoEntretenimiento = it
            val picto = Pictograma()
            picto.guardarPictoEntretenimiento(this, viewModel.eventos[viewModel.posicionEvento].id.toString(), viewModel.pictosEvento[entretenimientoPosition].id!!, it.toString())
            Thread.sleep(150)
            dialogEntretenimiento.dismiss()
        }

        viewModelCalendario._fechaSeleccionada.observe(this){
            Toast.makeText(this, "FILTRAR LA LISTA DE EVENTOS POR EL DIA SELECCIONADO", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calendarSection(){
        val fragment = CalendarioFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.linearLayout16, fragment)
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun calendarDialog(){
        val fragment = CalendarioFragment()
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun iniciarListaPlanificaciones() {
        listaEventos.layoutManager = LinearLayoutManager(this)
        viewModel.eventos = viewModel.evento.obtenerTodosEventos(viewModel.idUsuario, this)
        if(viewModel.eventos.isEmpty()){
            findViewById<LinearLayout>(R.id.layout_no_eventos).visibility = View.VISIBLE
        }
        adaptador = AdaptadorListaEventos(viewModel.eventos, this)
        listaEventos.adapter = adaptador
    }

//    private fun crearDialogo(){
//        viewModel.dialog = Dialog(this)
//        viewModel.dialog!!.setContentView(R.layout.dialogo_calendario)
//        viewModel.dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        val fechaActual = viewModel.dialog!!.findViewById<TextView>(R.id.lbl_mes2)
//        val btnSiguienteMes = viewModel.dialog!!.findViewById<ImageView>(R.id.image_calendar_siguiente2)
//        val btnAnteriorMes = viewModel.dialog!!.findViewById<ImageView>(R.id.image_calendar_anterior2)
//        val calendario = viewModel.dialog!!.findViewById<RecyclerView>(R.id.recycler_calendario)
//        val cerrarDialog = viewModel.dialog!!.findViewById<Button>(R.id.icono_CerrarDialogoEvento)
//        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
//
//        viewModel.obtenerVistaMes()
//        viewModel._fechaActual.observe(this) { fechaActual.text = it }
//        viewModel._diasMes.observe(this) {
//            calendario.layoutManager = GridLayoutManager(this, 7)
//            val listaDays = if(CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
//                arrayOf("L", "M", "X", "J", "V", "S", "D")
//            }else if (CommonUtils.isMobile(this) && Locale.getDefault().language == "en"){
//                arrayOf("M", "T", "W", "T", "F", "S", "S")
//            }else if (!CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
//                arrayOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
//            }else{
//                arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
//            }
//
//            val adaptadorCalendario = AdaptadorCalendario(it,listaDays, viewModel.eventos, null)
//            calendario.adapter = adaptadorCalendario
//        }
//
//        btnAnteriorMes.setOnClickListener {
//            val firstDayOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1)
//            if (CalendarioUtilidades.fechaSeleccionada.minusMonths(1).isBefore(firstDayOfPreviousMonth)){
//                return@setOnClickListener
//            }else{
//                CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
//                viewModel.obtenerVistaMes()
//            }
//        }
//        btnSiguienteMes.setOnClickListener {
//            CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
//            viewModel.obtenerVistaMes()
//        }
//
//        cerrarDialog.setOnClickListener { viewModel.dialog!!.dismiss() }
//        viewModel.dialog!!.show()
//    }

    override fun eventoSeleccionado(posicion: Int, recyclerPictogramas: RecyclerView, context: Context) {
        viewModel.posicionEvento = posicion
        val plan = Planificacion()
        viewModel.pictosEvento = plan.obtenerPictogramasPlanificacionEvento(this, viewModel.eventos[posicion].idPlan, viewModel.eventos[posicion].id,Locale.getDefault().language, viewModel.idUsuario)

        for (pictogram in viewModel.pictosEvento) {
            if (pictogram.idAPI != 0)
                pictogram.imagen = BitmapFactory.decodeResource(resources, R.drawable.loading_placeholder)
        }

        val adaptadorPictogramas = AdaptadorPictogramasEventos(viewModel.pictosEvento, this)
        recyclerPictogramas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerPictogramas.adapter = adaptadorPictogramas

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.pictosEvento.forEach { pictogram ->
                if (pictogram.idAPI != 0) {
                    pictogram.imagen = BitmapFactory.decodeResource(resources, R.drawable.loading_placeholder)
                    pictogram.imagen = withContext(Dispatchers.IO) {
                        CommonUtils.getImagenAPI(pictogram.idAPI)
                    }
                }
                adaptadorPictogramas.notifyItemChanged(viewModel.pictosEvento.indexOf(pictogram))
            }
        }
    }

    override fun verEvento(posicion: Int, context: Context) {
        val plan = Planificacion()
        val pictogramas = plan.obtenerPictogramasPlanificacionEvento(this, viewModel.eventos[posicion].idPlan, viewModel.eventos[posicion].id, Locale.getDefault().language, viewModel.idUsuario)

        val intent = Intent(applicationContext, EventosActivity::class.java)
        intent.putExtra("titulo", viewModel.eventos[posicion].nombre)
        intent.putExtra("pictogramas", pictogramas)
        pictogramas.forEachIndexed { index, pictogram ->
            intent.putExtra("imagen_$index", CommonUtils.bitmapToByteArray(pictogram.imagen))
        }
        intent.putExtra("fecha", viewModel.eventos[posicion].fecha)


        startActivity(intent)

    }

    override fun eventoEditado(posicion: Int, context: Context) {
        btnNuevaPlanificacion.visibility = View.VISIBLE
        CalendarioUtilidades.fechaSeleccionada = viewModel.eventos[posicion].fecha?: LocalDate.now()
        if(CommonUtils.isMobile(this) && CommonUtils.isPortrait(this)) {
            bottomSheetDialog(context, viewModel.eventos[posicion])
        }else{
            val evento = viewModel.eventos[posicion]
            viewModel.fragment = NuevoEventoFragment.newInstance(evento.id, evento.fecha, evento.hora, evento.idPlan, evento.reminder, evento.cambiarVisibilidad)
            CalendarioUtilidades.fechaSeleccionada = evento.fecha!!
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            ft.replace(R.id.linearLayout16, viewModel.fragment)
            ft.addToBackStack(null)
            ft.commit()

            expand(true, CommonUtils.isPortrait(this), false)
        }
    }

    override fun cambiarVisibilidadEvento(posicion: Int, context: Context) {
        val eventoVisible = viewModel.idUsuario.let { viewModel.evento.comprobarEventoVisible(it, viewModel.eventos[posicion].fecha.toString(), this) }

        if (viewModel.eventos[posicion].visible == 1) {
            viewModel.evento.cambiarVisibilidad(this, 0, viewModel.eventos[posicion].id)
        } else {
            viewModel.evento.cambiarVisibilidad(this, 0, eventoVisible)
            viewModel.evento.cambiarVisibilidad(this, 1, viewModel.eventos[posicion].id)
        }

        iniciarListaPlanificaciones()
    }

    override fun exportarEvento(posicion: Int, context: Context) {
        val intent = viewModel.exportEventCalendar(posicion)
        //filter my own package
        val resolvedActivities = this.packageManager.queryIntentActivities(intent, 0)

        if (resolvedActivities.isNotEmpty()) {
            val chooserIntent = Intent.createChooser(intent, R.string.toast_exportar_evento.toString())
            val excludeComponentNames = resolvedActivities.map { ComponentName(it.activityInfo.packageName, it.activityInfo.name) }.toTypedArray() //Exclude self
            chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludeComponentNames)
            startActivity(chooserIntent)
        } else {
            Toast.makeText(this, R.string.toast_error_exportar_evento, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bottomSheetDialog(context: Context, evento: Evento?){
        val fragment = if (evento == null){
            NuevoEventoFragment()
        }else{
            NuevoEventoFragment.newInstance(evento.id, evento.fecha, evento.hora, evento.idPlan, evento.reminder, evento.cambiarVisibilidad)
        }
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        fragment.show((context as AppCompatActivity).supportFragmentManager, fragment.tag)
    }

    fun expand(isExpand: Boolean, isVertical: Boolean, isCalendar: Boolean) {
        val valueAnimator: ValueAnimator
        if(isExpand) {
            if(fragmentLayout.visibility == View.VISIBLE) return

            fragmentLayout.visibility = View.VISIBLE
            if(isVertical){
                val targetHeigt = ((fragmentLayout.parent as View).height * 0.55).toInt()
                fragmentLayout.layoutParams.height = 0
                valueAnimator = ValueAnimator.ofInt(0, targetHeigt)
                valueAnimator.addUpdateListener { animation ->
                    fragmentLayout.layoutParams.height = animation.animatedValue as Int
                    fragmentLayout.requestLayout()
                }
            }else {
                val targetWidth = ((fragmentLayout.parent as View).width * 0.37).toInt()
                fragmentLayout.layoutParams.width = 0
                valueAnimator = ValueAnimator.ofInt(0, targetWidth)
                valueAnimator.addUpdateListener { animation ->
                    fragmentLayout.layoutParams.width = animation.animatedValue as Int
                    fragmentLayout.requestLayout()
                }
            }

            valueAnimator.addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    if(!isCalendar)
                        viewModel.fragment.listaPlanificaciones.adapter?.notifyDataSetChanged()
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            valueAnimator.interpolator = AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator)
            valueAnimator.duration = 500
            valueAnimator.start()
        }else{
            btnNuevaPlanificacion.visibility = View.GONE
            fragmentEdit.removeAllViews()
            if(isVertical){
                valueAnimator = ValueAnimator.ofInt(fragmentLayout.height, 0)
                valueAnimator.addUpdateListener { animation ->
                    fragmentLayout.layoutParams.height = animation.animatedValue as Int
                    fragmentLayout.requestLayout()
                }
            }else{
                valueAnimator = ValueAnimator.ofInt(fragmentLayout.width, 0)
                valueAnimator.addUpdateListener { animation ->
                    fragmentLayout.layoutParams.width = animation.animatedValue as Int
                    fragmentLayout.requestLayout()
                }
            }

            valueAnimator.addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    fragmentLayout.visibility = View.GONE
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            valueAnimator.interpolator = AnimationUtils.loadInterpolator(this, android.R.anim.decelerate_interpolator)
            valueAnimator.duration = 300
            valueAnimator.start()
        }
    }

    private fun configurarEventos() {
        iniciarListaPlanificaciones()
        viewModel.eventos.clear()
        viewModel.eventos.addAll(viewModel.evento.obtenerTodosEventos(viewModel.idUsuario, this))
        adaptador.notifyDataSetChanged()
    }

    override fun historiaSeleccionado(posicion: Int, context: Context) {
        val tituloCard = viewModel.pictosEvento[posicion].titulo
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_historiasocial)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnGuardar = dialog.findViewById<Button>(R.id.btn_guardar)
        val btnBorrar = dialog.findViewById<Button>(R.id.btn_eliminar)
        val cardtitulo = dialog.findViewById<TextView>(R.id.cardName)
        cardtitulo.text = tituloCard
        val iconoCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val historiaText = dialog.findViewById<TextInputLayout>(R.id.historiaText)

        if (viewModel.pictosEvento[posicion].historia.toString() == "null") {
            historiaText.editText?.setText("")
            btnBorrar.visibility = View.GONE
        } else {
            historiaText.editText?.setText(viewModel.pictosEvento[posicion].historia)
            btnBorrar.visibility = View.VISIBLE
        }

        iconoCerrar.setOnClickListener { dialog.dismiss() }

        btnGuardar.setOnClickListener {_ ->
            if (historiaText.editText?.text.toString() == "") {
                Toast.makeText(this, R.string.toast_campo_vacio, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.pictosEvento[posicion].historia = historiaText.editText?.text.toString()
                val picto = Pictograma()
                picto.guardarHistoria(this, viewModel.eventos[viewModel.posicionEvento].id.toString(), viewModel.pictosEvento[posicion].id!!, historiaText.editText?.text.toString())
                dialog.dismiss()
            }
        }

        btnBorrar.setOnClickListener {
            historiaText.editText?.text = null
            viewModel.pictosEvento[posicion].historia = null
            val picto = Pictograma()
            picto.guardarHistoria(this, viewModel.eventos[viewModel.posicionEvento].id.toString(), viewModel.pictosEvento[posicion].id!!, null)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun entretenimientoSeleccionado(posicion: Int, context: Context) {
        entretenimientoPosition = posicion
        dialogEntretenimiento = Dialog(this)
        dialogEntretenimiento.setContentView(R.layout.dialogo_aniadir_actividad)
        dialogEntretenimiento.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val recyclerActividad = dialogEntretenimiento.findViewById<RecyclerView>(R.id.recycler_actividad)
        val recyclerEntretenimiento = dialogEntretenimiento.findViewById<RecyclerView>(R.id.recycler_entretenimiento)
        val btnClose = dialogEntretenimiento.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val btnBorrar = dialogEntretenimiento.findViewById<Button>(R.id.btn_eliminar)
        val pictograma = Pictograma()
        val listaPictogramas = ArrayList<Pictograma>()

        val idPictoEntretenimiento = viewModel.pictosEvento[posicion].pictoEntretenimiento

        if(idPictoEntretenimiento == 0){
            btnBorrar.visibility = View.GONE
        }

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        if(prefs.getBoolean("info_objeto", false)) {
            pictograma.id = "-1"
            pictograma.titulo = prefs.getString("nombreObjeto", "default")!!.uppercase()
            listaPictogramas.add(pictograma)
            recyclerActividad(recyclerActividad, dialogEntretenimiento, listaPictogramas, idPictoEntretenimiento)
        }else{
            val title = dialogEntretenimiento.findViewById<TextView>(R.id.txt_actividad)
            title.visibility = View.GONE
            recyclerActividad.visibility = View.GONE
        }

        val language = Locale.getDefault().language
        val pictoEntretenimiento =  pictograma.obtenerPictogramas(this, 4, viewModel.idUsuario, language) as ArrayList<Pictograma>
        recyclerActividad(recyclerEntretenimiento, dialogEntretenimiento, pictoEntretenimiento, idPictoEntretenimiento)

        btnClose.setOnClickListener {
            dialogEntretenimiento.dismiss()
        }

        btnBorrar.setOnClickListener {
            viewModel.pictosEvento[posicion].pictoEntretenimiento = 0
            val picto = Pictograma()
            picto.guardarPictoEntretenimiento(this, viewModel.eventos[viewModel.posicionEvento].id.toString(), viewModel.pictosEvento[posicion].id!!, null)
            dialogEntretenimiento.dismiss()
        }

        dialogEntretenimiento.show()
    }

    private fun recyclerActividad(recyclerActividad : RecyclerView, dialog: Dialog, listaPictogramas: ArrayList<Pictograma>, idPicto: Int){
        val constraintLayout = dialog.findViewById<ConstraintLayout>(R.id.frameLayout)
        CommonUtils.getGridValueCuaderno(findViewById(android.R.id.content), this, recyclerActividad, constraintLayout, 150, 200)
        val adaptador = AdaptadorPictogramaEntretenimiento(listaPictogramas, idPicto, viewModel)
        recyclerActividad.adapter = adaptador
    }

    override fun duracionSeleecionado(posicion: Int, context: Context) {
        var duracion = viewModel.pictosEvento[posicion].duracion
        if(duracion == null || duracion == "null"){
            duracion = "00:00"
        }
        val duracionArray = duracion.split(":")

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_duracion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val pickerMin = dialog.findViewById<NumberPicker>(R.id.numberPickerMin)
        val pickerSec = dialog.findViewById<NumberPicker>(R.id.numberPickerSec)
        val btnAceptar = dialog.findViewById<MaterialButton>(R.id.btn_aceptar)
        val btnBorrar = dialog.findViewById<MaterialButton>(R.id.btn_eliminar)
        val cerrar = dialog.findViewById<MaterialButton>(R.id.icono_CerrarDialogo)

        pickerMin.minValue = 0
        pickerMin.maxValue = 59
        pickerSec.minValue = 0
        pickerSec.maxValue = 59

        pickerMin.value = duracionArray[0].toInt()
        pickerSec.value = duracionArray[1].toInt()

        if(duracionArray[0].toInt() == 0 && duracionArray[1].toInt() == 0){
            btnBorrar.visibility = View.GONE
        }

        btnAceptar.setOnClickListener{
            if( pickerMin.value == 0 && pickerSec.value == 0){
                dialog.dismiss()
            }

            viewModel.pictosEvento[posicion].duracion = "${pickerMin.value}:${pickerSec.value}"
            val picto = Pictograma()
            picto.guardarDuracion(this, viewModel.eventos[viewModel.posicionEvento].id.toString(), viewModel.pictosEvento[posicion].id!!, "${pickerMin.value}:${pickerSec.value}")
            dialog.dismiss()
        }

        btnBorrar.setOnClickListener {
            viewModel.pictosEvento[posicion].duracion = null
            val picto = Pictograma()
            picto.guardarDuracion(this, viewModel.eventos[viewModel.posicionEvento].id.toString(), viewModel.pictosEvento[posicion].id!!, null)
            dialog.dismiss()
        }

        cerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        configurarEventos()

    }

}
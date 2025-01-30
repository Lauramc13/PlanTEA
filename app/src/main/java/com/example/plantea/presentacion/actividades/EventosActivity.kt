package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.example.plantea.presentacion.viewModels.EventosViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


class EventosActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    lateinit var titulo: TextView
    private lateinit var lblMensaje: LinearLayout
    //private lateinit var iconoEscuchar: Button
    private lateinit var iconoReproducir: MaterialButton
    private lateinit var iconoDeshacer: Button
    private lateinit var iconoDeshacerTodas: Button
    private lateinit var iconoMarcar: Button
    private lateinit var iconoMarcarTodas: Button
    private lateinit var dia: TextView
    private var atras : Button? = null
    private lateinit var lblImportante: TextView

    // private lateinit var planificacionesFuturas: RecyclerView
    private lateinit var calendarButton: Button
    lateinit var historia: ConstraintLayout

    val viewModel: EventosViewModel by viewModels()

    override fun onStop() {
        super.onStop()
        viewModel.isRunning = false
        CommonUtils.handler.removeCallbacksAndMessages(null)

        viewModel._fechaActual.removeObservers(this)
        viewModel._diasMes.removeObservers(this)
        //CommonUtils.textToSpeech.stop()

        var tachadosCopy = ArrayList<Int>()
        var imprevistosCopy = ArrayList<Int>()

        // if adaptador has not been initialized, we don't want to copy the lists
        if(viewModel.checkInitializedAdapter()){
            tachadosCopy = ArrayList(viewModel.adaptador.tachados)
            imprevistosCopy = ArrayList(viewModel.adaptador.imprevistos)
            viewModel.adaptador.countDownTimer?.cancel()
            viewModel.adaptador.countDownTimer = null
        }

        // Safely iterate over the copies
        tachadosCopy.forEach {
            viewModel.tachados.add(it)
        }

        imprevistosCopy.forEach {
            viewModel.imprevistos.add(it)
        }

        viewModel.dialog?.dismiss()

        if(viewModel.currentDialog != null){
            viewModel.currentDialog!!.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        val layoutManagerLinear: LinearLayoutManager

        if(prefs.getBoolean("isVerticalPictogramas", false)){
            layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            viewModel.recyclerView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            viewModel.recyclerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            if(viewModel.listaPictogramas.size > 4){
                val params = viewModel.recyclerView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 350, 0, 300)
                viewModel.recyclerView.layoutParams = params
            }
        }else{
            layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            viewModel.recyclerView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            viewModel.recyclerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        viewModel.recyclerView.layoutManager = layoutManagerLinear
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)

        // Si se va hacia atras y no hay nada en la cola, se redirige a MainActivity
//        val callback = viewModel.backCallBack(this)
//        onBackPressedDispatcher.addCallback(this, callback)

//        if(CommonUtils.isMobile(this)){
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        }

        iconoDeshacer = findViewById(R.id.icon_deshacer)
        iconoDeshacerTodas = findViewById(R.id.icon_deshacerTodas)
        iconoMarcar = findViewById(R.id.icon_marcar)
        iconoMarcarTodas = findViewById(R.id.icon_marcarTodas)
        iconoReproducir = findViewById(R.id.icon_reproducir)
        calendarButton = findViewById(R.id.CalendarDate)
        titulo = findViewById(R.id.lbl_titulo)
        lblMensaje = findViewById(R.id.layout_no_eventos)
        viewModel.recyclerView = findViewById(R.id.recycler_plan)
        dia = findViewById(R.id.lbl_dia)
        atras = findViewById(R.id.atras)
        lblImportante = findViewById(R.id.lbl_importante)

        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()

        val question = findViewById<MaterialButton>(R.id.question)
        question.setOnClickListener {
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show()
        }

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs, this)

        dia.text = getString(R.string.formatted_date, viewModel.dayOfWeek.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, viewModel.dayOfMonth, viewModel.month)

        viewModel.initializeAnimations(applicationContext)

        observe()
        viewModel.createPickMedia(viewModel, this)

        //Comprobar si hay parametros en caso de llamada desde el planificador
        if (this.intent.extras != null) {
            configureParameters(savedInstanceState)
        } else {
            if(savedInstanceState == null){
                viewModel.mostrarPlan(this)
            }else{
                viewModel.configureDataEvento(this)
            }
        }

        //--------------- FUNCIONALIDADES DE LOS BOTONES ---------------//

        atras?.setOnClickListener {
            if (isTaskRoot) {
                startActivity(Intent(this@EventosActivity, MainActivity::class.java))
            }
            finish()
        }

        calendarButton.setOnClickListener {
            crearDialogo()
        }

        //Este método se ejecutará al seleccionar el icono deshacer para volver un paso atrás en el seguimiento
        iconoDeshacer.setOnClickListener {
            if (!viewModel._pasosCompletados.value?.isEmpty()!!) {
                //if the next pictogram has timer, cancel it
                if(viewModel.adaptador.listaPictogramas?.get(viewModel._pasosCompletados.value?.last() as Int)?.duracion != "null"){
                    viewModel.adaptador.countDownTimer?.cancel()
                    viewModel.adaptador.timeLeft = 0
                    viewModel.adaptador.notifyItemChanged(viewModel._pasosCompletados.value?.last()!!.toInt(), "null")
                }
                viewModel.adaptador.notifyItemChanged(viewModel._pasosCompletados.value?.removeLast() as Int)
                viewModel._pasosCompletados.postValue(viewModel._pasosCompletados.value)
            }
        }

        //Este método se ejecutará al seleccionar el icono deshacer para marcar todos los pictogramas como no realizados
        iconoDeshacerTodas.setOnClickListener {
            if (!viewModel._pasosCompletados.value?.isEmpty()!!) {
                viewModel.adaptador.countDownTimer?.cancel() //Cancelar si existe un temporizador activo
                viewModel.adaptador.timeLeft = 0
                val posicionPicto = viewModel._pasosCompletados.value?.last()!!.toInt()
                if(viewModel.adaptador.listaPictogramas?.get(posicionPicto)?.duracion != "null"){
                    viewModel.adaptador.notifyItemChanged(posicionPicto, "desmarcarDuracion")
                }

                for(i in 0 until viewModel._pasosCompletados.value?.size!!){
                    viewModel.adaptador.notifyItemChanged(i, "null")
                    viewModel._pasosCompletados.value?.removeLast()
                    viewModel._pasosCompletados.postValue(viewModel._pasosCompletados.value)
                }

            }
        }

        //Este método se ejecutará al seleccionar el icono marcar para marcar el pictograma actual como realizado
        iconoMarcar.setOnClickListener {
            if (!viewModel._pasosCompletados.value?.isEmpty()!!) {
                //val posicion = viewModel._pasosCompletados.value?.peek() as Int
                val posicion = viewModel._pasosCompletados.value?.last() as Int
                viewModel.adaptador.notifyItemChanged(posicion+1)
                viewModel._pasosCompletados.value?.add(posicion+1)
            }else{
                viewModel.adaptador.notifyItemChanged(0)
                viewModel._pasosCompletados.value?.add(0)
            }
            viewModel._pasosCompletados.postValue(viewModel._pasosCompletados.value)
        }

        // Este método se ejecutará al seleccionar el icono marcar para marcar todos los pictogramas como realizados
        iconoMarcarTodas.setOnClickListener {
            for (i in 0 until viewModel.listaPictogramas.size){
                viewModel.adaptador.notifyItemChanged(i)
                viewModel._pasosCompletados.value?.add(i)
                viewModel._pasosCompletados.postValue(viewModel._pasosCompletados.value)
            }
        }

       /* iconoEscuchar.setOnClickListener {
            if (!viewModel.speechInProgress) {
                iconoEscuchar.text = getString(R.string.str_parar)
                CommonUtils.textToSpeechOn(viewModel.listaPictogramas)
                viewModel.speechInProgress = true
            } else {
                iconoEscuchar.text = getString(R.string.str_escuchar)
                CommonUtils.textToSpeech.stop()
                viewModel.speechInProgress = false
            }
        }*/

        iconoReproducir.setOnClickListener {
            reproducirEvento()
        }

        showPlanificacionesFuturas()

        viewModel.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //get location of the item in position posicionSelectedCambio in recyclerview
                val view = recyclerView.layoutManager!!.findViewByPosition(viewModel.posicionSelectedCambio)
                if(viewModel.posicionSelectedCambio != -1){

                    if(view != null){
                        lblImportante.x = view.x + view.width - lblImportante.width/3
                    }else{
                        lblImportante.visibility = View.INVISIBLE
                    }

                }
            }
        })
    }

    /*private fun showDialogRotate(): Int {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_rotate_screen)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        return 0
    }*/

    private fun showPlanificacionesFuturas(){
        val isPlanificador = prefs.getBoolean("PlanificadorLogged", false)
        if(!isPlanificador) {
            iconoDeshacerTodas.visibility = View.INVISIBLE
            iconoMarcarTodas.visibility = View.INVISIBLE
        }
    }

    private fun crearDialogo(){
         viewModel.dialog = Dialog(this)
         viewModel.dialog!!.setContentView(R.layout.dialogo_calendario)
         viewModel.dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

         val fechaActual = viewModel.dialog!!.findViewById<TextView>(R.id.lbl_mes2)
         val btnSiguienteMes = viewModel.dialog!!.findViewById<ImageView>(R.id.image_calendar_siguiente2)
         val btnAnteriorMes = viewModel.dialog!!.findViewById<ImageView>(R.id.image_calendar_anterior2)
         val calendario = viewModel.dialog!!.findViewById<RecyclerView>(R.id.recycler_calendario)
         val cerrarDialog = viewModel.dialog!!.findViewById<Button>(R.id.icono_CerrarDialogoEvento)

         val idUsuario = if(prefs.getBoolean("PlanificadorLogged", false)) viewModel.idUsuario else viewModel.idUsuarioTEA
         viewModel.eventos = viewModel.evento.obtenerTodosEventos(idUsuario!!, this)
         viewModel.obtenerVistaMes()

         viewModel._fechaActual.observe(this) { fechaActual.text = it }

         viewModel._diasMes.observe(this) {
             val ratio = if(it.size < 42) "7:6" else "1:1"
             val params = calendario.layoutParams as ConstraintLayout.LayoutParams
             params.dimensionRatio = ratio
             calendario.layoutParams = params

             calendario.layoutManager = GridLayoutManager(this, 7)
             val listaDays = if(CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
                 arrayOf("L", "M", "X", "J", "V", "S", "D")
             }else if (CommonUtils.isMobile(this) && Locale.getDefault().language == "en"){
                 arrayOf("M", "T", "W", "T", "F", "S", "S")
             }else if (!CommonUtils.isMobile(this) && Locale.getDefault().language == "es"){
                 arrayOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
             }else{
                 arrayOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
             }

             val adaptadorCalendario = AdaptadorCalendario(it,listaDays, viewModel.eventos, viewModel)
             calendario.adapter = adaptadorCalendario
             calendario.requestLayout()
         }

         btnAnteriorMes.setOnClickListener {
             val firstDayOfPreviousMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1)
             if (CalendarioUtilidades.fechaSeleccionada.minusMonths(1).isBefore(firstDayOfPreviousMonth)){
                 return@setOnClickListener
             }else{
                 CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
                 viewModel.obtenerVistaMes()
             }
         }
         btnSiguienteMes.setOnClickListener {
             CalendarioUtilidades.fechaSeleccionada = CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
             viewModel.obtenerVistaMes()
         }

         cerrarDialog.setOnClickListener { viewModel.dialog!!.dismiss() }
         viewModel.dialog!!.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe(){
        viewModel._diaText.observe(this) { dia.text = it }
        //viewModel._tituloLiveData.observe(this) { titulo.text = it }

        viewModel._planLiveData.observe(this){
            if (it != null) {
                    titulo.text = viewModel.tituloPlan
                    lblMensaje.visibility = View.GONE
                    iconoDeshacer.visibility = View.VISIBLE
                    iconoDeshacerTodas.visibility = View.VISIBLE
                    iconoMarcar.visibility = View.VISIBLE
                    iconoMarcarTodas.visibility = View.VISIBLE
                    //iconoEscuchar.visibility = View.VISIBLE
                    iconoReproducir.visibility = View.VISIBLE
                    iconoMarcar.isEnabled = true
                    iconoMarcarTodas.isEnabled = true
                    viewModel.recyclerView.visibility = View.VISIBLE
            }else{
                Toast.makeText(this, "Error al cargar los pictogramas", Toast.LENGTH_SHORT).show()
            }

            viewModel.adaptador = AdaptadorPresentacion(it, viewModel)
            viewModel.adaptador.listMarcados = viewModel._pasosCompletados.value
            viewModel.adaptador.tachados = viewModel.tachados
            viewModel.adaptador.imprevistos = viewModel.imprevistos
            viewModel.recyclerView.adapter = viewModel.adaptador
            viewModel.adaptador.notifyDataSetChanged()
        }

        viewModel._noEvents.observe(this){
            val isPlanificador = prefs.getBoolean("PlanificadorLogged", false)
            if(it){
                titulo.text = ""
                lblMensaje.visibility = View.VISIBLE
                iconoDeshacer.visibility = View.INVISIBLE
                iconoDeshacerTodas.visibility = View.INVISIBLE
                iconoMarcar.visibility = View.INVISIBLE
                iconoMarcarTodas.visibility = View.INVISIBLE
               // iconoEscuchar.visibility = View.INVISIBLE
                iconoReproducir.visibility = View.INVISIBLE

                /*if(isPlanificador && (CommonUtils.isPortrait(this) && CommonUtils.isMobile(this) || !CommonUtils.isMobile(this))){
                    val layoutPlanificaciones = findViewById<LinearLayout>(R.id.layoutPlanificacionesFuturas)
                    layoutPlanificaciones.visibility = View.VISIBLE
                }else{
                    val layoutPlanificaciones = findViewById<LinearLayout>(R.id.layoutPlanificacionesFuturas)
                    layoutPlanificaciones.visibility = View.GONE
                }*/

                viewModel.recyclerView.visibility = View.INVISIBLE
            }
        }

        viewModel._pasosCompletados.observe(this){
            if (it != null) {
                if(it.isEmpty()){
                    iconoDeshacer.isEnabled = false
                    iconoDeshacerTodas.isEnabled = false
                }else{
                    iconoDeshacer.isEnabled = true
                    iconoDeshacerTodas.isEnabled = true
                }

                if (it.size == viewModel.listaPictogramas.size){
                    iconoMarcar.isEnabled = false
                    iconoMarcarTodas.isEnabled = false
                }else{
                    iconoMarcar.isEnabled = true
                    iconoMarcarTodas.isEnabled = true
                }
            }
        }

        viewModel._pictoChanged.observe(this){
            viewModel.adaptador.tachados.add(viewModel.posicionSelectedCambio)
            viewModel.adaptador.imprevistos.add(viewModel.posicionSelectedCambio+1)

            val pictoNuevo = viewModel._nuevoPicto.value!!
            pictoNuevo.historia = viewModel.listaPictogramas[viewModel.posicionSelectedCambio].historia
            pictoNuevo.duracion = viewModel.listaPictogramas[viewModel.posicionSelectedCambio].duracion
            pictoNuevo.pictoEntretenimiento = viewModel.listaPictogramas[viewModel.posicionSelectedCambio].pictoEntretenimiento

            viewModel.listaPictogramas[viewModel.posicionSelectedCambio].historia = null
            viewModel.listaPictogramas[viewModel.posicionSelectedCambio].duracion = null
            viewModel.listaPictogramas[viewModel.posicionSelectedCambio].pictoEntretenimiento = 0

            viewModel.adaptador.listaPictogramas?.add(viewModel.posicionSelectedCambio+1, pictoNuevo)
            viewModel.adaptador.notifyItemInserted(viewModel.posicionSelectedCambio+1)
            viewModel.adaptador.notifyItemChanged(viewModel.posicionSelectedCambio)

            val view = viewModel.recyclerView.layoutManager!!.findViewByPosition(viewModel.posicionSelectedCambio)

            if(viewModel.posicionSelectedCambio != -1){
                lblImportante.x = view?.x!! + view.width - lblImportante.width/3
            }
            lblImportante.visibility = View.VISIBLE

        }
    }

    private fun configureParameters(savedInstanceState: Bundle?){
        if(intent.getBooleanExtra("isFromSemana", false)){
            viewModel.diaSeleccionado(this, intent.getSerializableExtra("dia") as LocalDate)
        }else{
            if(savedInstanceState == null) viewModel._pasosCompletados.value = ArrayList()
            viewModel.tituloPlan = intent.getStringExtra("titulo")!! //PETA AQUI CUANDO SE INTENTA COMPARTIR UN EVENTO
            viewModel.listaPictogramas = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!

            /*for (i in viewModel.listaPictogramas.indices){
                viewModel.listaPictogramas[i].imagen = CommonUtils.byteArrayToBitmap(intent.getByteArrayExtra("imagen_$i"))
            }*/

            if(savedInstanceState == null){
                viewModel.listaPictogramas.forEachIndexed{ index, pictogram ->
                    viewModel.listaPictogramas[index].imagen = CommonUtils.byteArrayToBitmap(intent.getByteArrayExtra("imagen_$index"))
                    pictogram.imagen = CommonUtils.byteArrayToBitmap(intent.getByteArrayExtra("imagen_$index"))
                    if (pictogram.idAPI != 0) {
                        pictogram.imagen = BitmapFactory.decodeResource(resources, R.drawable.loading_placeholder)
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.listaPictogramas.forEach { pictogram ->
                        if (pictogram.idAPI != 0) {
                            pictogram.imagen = withContext(Dispatchers.IO) {
                                CommonUtils.getImagenAPI(pictogram.idAPI)
                            }
                        }
                        viewModel.adaptador.notifyItemChanged(viewModel.listaPictogramas.indexOf(pictogram))
                    }
                }
            }

            val fecha = intent.getSerializableExtra("fecha") as LocalDate
            CalendarioUtilidades.fechaSeleccionada = fecha
            val diaSemana = fecha.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }
            val mes = fecha.month.getDisplayName(TextStyle.FULL, Locale("es"))
            val diaText = "$diaSemana, ${fecha.dayOfMonth} de $mes"
            dia.text = diaText
            viewModel._planLiveData.value = viewModel.listaPictogramas
        }
    }

    private fun reproducirEvento() {
        val tiempo = 1500L

        if(viewModel.isRunning){
            configureIsNotRunning()
        }else{
            configureIsRunning()

            viewModel.currentRunnable = object : Runnable {
                override fun run() {
                    if (viewModel.isRunning) {
                        //onItemSeleccionado(currentPosition)
                        if (viewModel.currentPosition < viewModel.listaPictogramas.size) {
                            viewModel.handler.postDelayed(this, tiempo)
                            viewModel.adaptador.animatedPositions.add(viewModel.currentPosition)
                            viewModel.recyclerView.scrollToPosition(viewModel.currentPosition)
                            viewModel.adaptador.notifyItemChanged(viewModel.currentPosition)
                        } else {
                            configureIsNotRunning()
                        }
                        viewModel.currentPosition++
                    }
                }
            }

            viewModel.handler.post(viewModel.currentRunnable!!)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun configureIsNotRunning(){
        viewModel.isRunning = false
        viewModel.adaptador.animatedPositions.clear()
        iconoReproducir.setIconResource(R.drawable.svg_play)
        viewModel.adaptador.listMarcados?.clear()
        viewModel._pasosCompletados.value?.clear()
        viewModel.adaptador.notifyDataSetChanged()
        viewModel.stopReproductor()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun configureIsRunning(){
        iconoReproducir.setIconResource(R.drawable.svg_stop)
       // viewModel.adaptador.optionMarcar = false
        viewModel.adaptador.listMarcados?.clear()
        viewModel._pasosCompletados.value?.clear()
        viewModel.adaptador.notifyDataSetChanged()

        iconoMarcar.isEnabled = false
        iconoMarcarTodas.isEnabled = false
        iconoDeshacer.isEnabled = false
        iconoDeshacerTodas.isEnabled = false

        viewModel.currentPosition = 0
        viewModel.isRunning = true
    }

   /* override fun onSpeechDone() {
        iconoEscuchar.text = getString(R.string.str_escuchar)
    }*/
}

package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.example.plantea.presentacion.viewModels.EventosViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var iconoReproducir: MaterialButton
    private lateinit var iconoDeshacer: Button
    private lateinit var iconoDeshacerTodas: Button
    private lateinit var iconoMarcar: Button
    private lateinit var iconoMarcarTodas: Button
    private lateinit var btnInfo: Button
    private lateinit var dia: TextView
    private var hora: TextView? = null
    private var atras : Button? = null

    // private lateinit var planificacionesFuturas: RecyclerView
    private lateinit var calendarButton: Button
    lateinit var historia: ConstraintLayout

    val viewModel: EventosViewModel by viewModels()

    override fun onStop() {
        super.onStop()
        viewModel.isRunning = false
        CommonUtils.handler.removeCallbacksAndMessages(null)

        viewModel.mdFechaActual.removeObservers(this)
        viewModel.mdDiasMes.removeObservers(this)
        //CommonUtils.textToSpeech.stop()

        var tachadosCopy = ArrayList<Int>()
        var imprevistosCopy = ArrayList<Int>()

        // if adaptador has not been initialized, we don't want to copy the lists
        if(viewModel.checkInitializedAdapter()){
            tachadosCopy = ArrayList(viewModel.adaptador.tachados ?: emptyList())
            imprevistosCopy = ArrayList(viewModel.adaptador.imprevistos ?: emptyList())
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

    /**
     * Método que se ejecuta al reanudar la actividad. Se comprueba la configuracion del orden
     * de los pictogramas y se ajusta el layout en consecuencia.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        val layoutManagerLinear: LinearLayoutManager

        if (prefs.getBoolean("isVerticalPictogramas", false) && CommonUtils.isMobile(this) && resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            viewModel.recyclerView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            viewModel.recyclerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            if (viewModel.listaPictogramas.size > 4) {
                val params = viewModel.recyclerView.layoutParams as ViewGroup.MarginLayoutParams
                val marginTop = CommonUtils.dpToPx(90, resources)
                val marginBottom = CommonUtils.dpToPx(75, resources)
                params.setMargins(0, marginTop, 0, marginBottom)
                viewModel.recyclerView.layoutParams = params
            }
        } else {
            layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            viewModel.recyclerView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            viewModel.recyclerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            val params = viewModel.recyclerView.layoutParams as ViewGroup.MarginLayoutParams
            val margins = if (CommonUtils.isMobile(this)) CommonUtils.dpToPx(10, resources) else CommonUtils.dpToPx(25, resources)
            params.setMargins(margins, margins, margins, margins)
            viewModel.recyclerView.layoutParams = params
        }
        viewModel.recyclerView.layoutManager = layoutManagerLinear
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)

        // Si se va hacia atras y no hay nada en la cola, se redirige a MainActivity
        val callback = viewModel.backCallBack(this)
        onBackPressedDispatcher.addCallback(this, callback)

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
        hora = findViewById(R.id.lbl_hora)
        atras = findViewById(R.id.atras)
        btnInfo = findViewById(R.id.btnInfo)

        CalendarioUtilidades.fechaSeleccionada = LocalDate.now()

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !prefs.getBoolean("darkMode", false)
        viewModel.configureUser(prefs, this)

        viewModel.initializeAnimations(applicationContext)
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

        //get string based on the language of the device

        dia.text = getString(
            R.string.formatted_date,
            viewModel.dayOfWeek.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            },
            viewModel.dayOfMonth,
            viewModel.month
        )

        if (viewModel.horaEventoInicio.isNullOrEmpty() || viewModel.horaEventoInicio == "null") {
            hora?.text = getString(R.string.str_todo_dia)
        } else {
            hora?.text = """${viewModel.horaEventoInicio} a ${viewModel.horaEventoFin}"""
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
            if (!viewModel.mdPasosCompletados.value?.isEmpty()!!) {
                //if the next pictogram has timer, cancel it
                if(viewModel.adaptador.listaPictogramas?.get(viewModel.mdPasosCompletados.value?.last() as Int)?.duracion != "null"){
                    viewModel.adaptador.countDownTimer?.cancel()
                    viewModel.adaptador.timeLeft = 0
                    viewModel.adaptador.notifyItemChanged(viewModel.mdPasosCompletados.value?.last()!!.toInt(), "null")
                }
                viewModel.adaptador.notifyItemChanged(viewModel.mdPasosCompletados.value?.removeLast() as Int)
                viewModel.mdPasosCompletados.postValue(viewModel.mdPasosCompletados.value)
            }
        }

        //Este método se ejecutará al seleccionar el icono deshacer para marcar todos los pictogramas como no realizados
        iconoDeshacerTodas.setOnClickListener {
            if (!viewModel.mdPasosCompletados.value?.isEmpty()!!) {
                viewModel.adaptador.countDownTimer?.cancel() //Cancelar si existe un temporizador activo
                viewModel.adaptador.timeLeft = 0
                val posicionPicto = viewModel.mdPasosCompletados.value?.last()!!.toInt()
                if(viewModel.adaptador.listaPictogramas?.get(posicionPicto)?.duracion != "null"){
                    viewModel.adaptador.notifyItemChanged(posicionPicto, "desmarcarDuracion")
                }

                for(i in 0 until viewModel.mdPasosCompletados.value?.size!!){
                    viewModel.adaptador.notifyItemChanged(i, "null")
                    viewModel.mdPasosCompletados.value?.removeLast()
                    viewModel.mdPasosCompletados.postValue(viewModel.mdPasosCompletados.value)
                }

            }
        }

        //Este método se ejecutará al seleccionar el icono marcar para marcar el pictograma actual como realizado
        iconoMarcar.setOnClickListener {
            if (!viewModel.mdPasosCompletados.value?.isEmpty()!!) {
                //val posicion = viewModel._pasosCompletados.value?.peek() as Int
                val posicion = viewModel.mdPasosCompletados.value?.last() as Int
                viewModel.adaptador.notifyItemChanged(posicion+1)
                viewModel.mdPasosCompletados.value?.add(posicion+1)
            }else{
                viewModel.adaptador.notifyItemChanged(0)
                viewModel.mdPasosCompletados.value?.add(0)
            }
            viewModel.mdPasosCompletados.postValue(viewModel.mdPasosCompletados.value)
        }

        // Este método se ejecutará al seleccionar el icono marcar para marcar todos los pictogramas como realizados
        iconoMarcarTodas.setOnClickListener {
            for (i in 0 until viewModel.listaPictogramas.size){
                viewModel.adaptador.notifyItemChanged(i)
                viewModel.mdPasosCompletados.value?.add(i)
                viewModel.mdPasosCompletados.postValue(viewModel.mdPasosCompletados.value)
            }
        }

        iconoReproducir.setOnClickListener {
            reproducirEvento()
        }


        btnInfo.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialogo_info_eventos)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val tituloEvento = dialog.findViewById<TextView>(R.id.lbl_tituloEvento)
            val fechaEvento = dialog.findViewById<TextView>(R.id.lbl_fechaEvento)
            val horaEvento = dialog.findViewById<TextView>(R.id.lbl_horaEvento)
            val localizacion = dialog.findViewById<TextView>(R.id.lbl_localizacionEvento)
            val layoutLocalizacion = dialog.findViewById<LinearLayout>(R.id.layout_localizacion)
            val notas = dialog.findViewById<TextInputLayout>(R.id.notasText)
            val layoutNotas = dialog.findViewById<LinearLayout>(R.id.layout_notas)
            val cerrarDialog = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)

            tituloEvento.text = viewModel.evento.nombre
            fechaEvento.text =  CalendarioUtilidades.formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada)
            localizacion.text = viewModel.evento.localizacion
            notas.editText?.setText(viewModel.evento.notas)
            if(viewModel.horaEventoInicio != "null"){
                horaEvento.text = """${viewModel.horaEventoInicio} a ${viewModel.horaEventoFin}"""
            }else{
                horaEvento.text = getString(R.string.str_todo_dia)
            }

            if (viewModel.evento.localizacion == "") {
                layoutLocalizacion.visibility = View.GONE
            }

            if(viewModel.evento.notas == "") {
                layoutNotas.visibility = View.GONE
                notas.visibility = View.GONE
            }
            cerrarDialog.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }

        showPlanificacionesFuturas()
        observe()

        viewModel.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                for (i in 0 until viewModel.lblImportantes.size) {
                    val view = recyclerView.layoutManager!!.findViewByPosition(viewModel.lblImportantes[i].first.toInt())
                    if(view != null){
                        viewModel.lblImportantes[i].second.visibility = View.VISIBLE
                        viewModel.lblImportantes[i].second.x = view.x + view.width - viewModel.lblImportantes[i].second.width/3
                    }else{
                        viewModel.lblImportantes[i].second.visibility = View.GONE
                    }
                }
            }
        })

    }

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

        val idUsuario = if (viewModel.idUsuarioTEA != "") {
            viewModel.idUsuarioTEA
        } else {
            viewModel.idUsuario
        }
         viewModel.eventos = viewModel.gEvento.obtenerTodosEventos(idUsuario, this).filter { it.visible == 1 } as ArrayList
         viewModel.obtenerVistaMes()

         viewModel.mdFechaActual.observe(this) { fechaActual.text = it }

         viewModel.mdDiasMes.observe(this) {
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

         cerrarDialog.setOnClickListener { viewModel.dialog?.dismiss() }
         viewModel.dialog!!.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe(){
        viewModel.mdDiaText.observe(this) { dia.text = it }
        //viewModel._tituloLiveData.observe(this) { titulo.text = it }

        viewModel.mdPlanLiveData.observe(this){
            if (it != null) {
                    titulo.text = viewModel.evento.nombre
                    lblMensaje.visibility = View.GONE
                    btnInfo.visibility = View.VISIBLE
                    iconoDeshacer.visibility = View.VISIBLE
                    iconoDeshacerTodas.visibility = View.VISIBLE
                    iconoMarcar.visibility = View.VISIBLE
                    iconoMarcarTodas.visibility = View.VISIBLE
                    iconoReproducir.visibility = View.VISIBLE
                    iconoMarcar.isEnabled = true
                    iconoMarcarTodas.isEnabled = true
                    viewModel.recyclerView.visibility = View.VISIBLE
            }else{
                Toast.makeText(this, "Error al cargar los pictogramas", Toast.LENGTH_SHORT).show()
            }

            it?.forEachIndexed { index, pictograma ->
                if (pictograma.isImprevisto && !viewModel.imprevistos.contains(index)) {
                    viewModel.imprevistos.add(index)
                    viewModel.tachados.add(index - 1)
                    if (!prefs.getBoolean("isVerticalPictogramas", false)) {
                        createImporantText(index -1, pictograma.id)
                    }
                }
            }


            viewModel.adaptador = AdaptadorPresentacion(it, viewModel, viewModel.imprevistos, viewModel.tachados)
            viewModel.adaptador.listMarcados = viewModel.mdPasosCompletados.value
            viewModel.recyclerView.adapter = viewModel.adaptador
            viewModel.adaptador.notifyDataSetChanged()

//            for (i in viewModel.tachados){
//                createImporantText(i)
//            }
        }

        viewModel.seNoEvents.observe(this){
            if(it){
                titulo.text = ""
                lblMensaje.visibility = View.VISIBLE
                btnInfo.visibility = View.GONE
                iconoDeshacer.visibility = View.INVISIBLE
                iconoDeshacerTodas.visibility = View.INVISIBLE
                iconoMarcar.visibility = View.INVISIBLE
                iconoMarcarTodas.visibility = View.INVISIBLE
                iconoReproducir.visibility = View.INVISIBLE

                viewModel.recyclerView.visibility = View.INVISIBLE
            }
        }

        viewModel.mdPasosCompletados.observe(this){
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

        viewModel.seNewImprevisto.observe(this){
            if (!prefs.getBoolean("isVerticalPictogramas", false)) {
                createImporantText(it.first, it.second)
            }
        }
    }

    private fun createImporantText(i: Int, id:String?) {
        val lblTachado = TextView(this)
        lblTachado.layoutParams= ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lblTachado.text = getString(R.string.importante)
        lblTachado.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        lblTachado.setTextColor(getColor(R.color.red))
        lblTachado.typeface = resources.getFont(R.font.poppins_semibold)
        lblTachado.visibility = View.VISIBLE

        viewModel.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewModel.recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                lblTachado.y = viewModel.recyclerView.y - 40
                val view = viewModel.recyclerView.layoutManager!!.findViewByPosition(i)
                if(view != null){
                    lblTachado.x = view.x + view.width - lblTachado.width/3
                }
            }
        })

        val layout: ConstraintLayout = findViewById(R.id.constraintLayout)
        layout.addView(lblTachado)

        //add id, textview
        viewModel.lblImportantes.add(Pair(id!!, lblTachado))
    }

    private fun configureParameters(savedInstanceState: Bundle?){
        if(intent.getBooleanExtra("isFromSemana", false)){
            viewModel.diaSeleccionado(this, intent.getSerializableExtra("dia") as LocalDate)
        }else{
            if(savedInstanceState == null) viewModel.mdPasosCompletados.value = ArrayList()
            viewModel.evento.nombre = intent.getStringExtra("titulo")!!
            viewModel.horaEventoInicio = intent.getStringExtra("horaInicio")
            viewModel.horaEventoFin = intent.getStringExtra("horaFin")
            val fechaString = intent.getStringExtra("fecha")
            viewModel.fechaEvento = fechaString?.let { LocalDate.parse(it) }
            viewModel.evento.localizacion = intent.getStringExtra("localizacion")!!
            viewModel.evento.notas = intent.getStringExtra("notas")!!
            viewModel.listaPictogramas = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!
            viewModel.evento.id = intent.getIntExtra("idEvento", 0)

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

            val fecha = viewModel.fechaEvento
            if (fecha != null) {
                CalendarioUtilidades.fechaSeleccionada = fecha

                dia.text = getString(
                    R.string.formatted_date,
                    fecha.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    fecha.dayOfMonth.toString(),
                    fecha.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                )
            }

            viewModel.mdPlanLiveData.value = viewModel.listaPictogramas
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
        viewModel.mdPasosCompletados.value?.clear()
        viewModel.adaptador.notifyDataSetChanged()
        viewModel.stopReproductor()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun configureIsRunning(){
        iconoReproducir.setIconResource(R.drawable.svg_stop)
       // viewModel.adaptador.optionMarcar = false
        viewModel.adaptador.listMarcados?.clear()
        viewModel.mdPasosCompletados.value?.clear()
        viewModel.adaptador.notifyDataSetChanged()

        iconoMarcar.isEnabled = false
        iconoMarcarTodas.isEnabled = false
        iconoDeshacer.isEnabled = false
        iconoDeshacerTodas.isEnabled = false

        viewModel.currentPosition = 0
        viewModel.isRunning = true
    }
}

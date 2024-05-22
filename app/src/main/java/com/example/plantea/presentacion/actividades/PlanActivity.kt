package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
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
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.PlanificacionItem
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacionesFuturas
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.example.plantea.presentacion.viewModels.PlanViewModel
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.Stack

class PlanActivity : AppCompatActivity(), CommonUtils.TextToSpeechListener {
    lateinit var prefs: SharedPreferences
    lateinit var titulo: TextView
    private lateinit var lblMensaje: TextView
    private lateinit var buttonPlanNuevo : Button
    private lateinit var iconoEscuchar: Button
    private lateinit var iconoReproducir: MaterialButton
    private lateinit var iconoDeshacer: Button
    private lateinit var iconoDeshacerTodas: Button
    private lateinit var iconoMarcar: Button
    private lateinit var iconoMarcarTodas: Button
    private lateinit var dia: TextView
    private var atras : Button? = null

    private lateinit var planificacionesFuturas: RecyclerView
    private lateinit var calendarButton: Button
    lateinit var historia: ConstraintLayout

    val viewModel: PlanViewModel by viewModels()

    override fun onStop() {
        super.onStop()
        viewModel.isRunning = false
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel._fechaActual.removeObservers(this)
        viewModel._diasMes.removeObservers(this)
        CommonUtils.textToSpeech.stop()
        viewModel.dialog?.dismiss()

        if(viewModel.currentDialog != null){
            viewModel.currentDialog!!.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)

        // Si se va hacia atras y no hay nada en la cola, se redirige a MainActivity

        val callback = viewModel.backCallBack(this)
        onBackPressedDispatcher.addCallback(this, callback)

        iconoDeshacer = findViewById(R.id.icon_deshacer)
        iconoDeshacerTodas = findViewById(R.id.icon_deshacerTodas)
        iconoMarcar = findViewById(R.id.icon_marcar)
        iconoMarcarTodas = findViewById(R.id.icon_marcarTodas)
        iconoEscuchar = findViewById(R.id.icon_escuchar)
        iconoReproducir = findViewById(R.id.icon_reproducir)
        planificacionesFuturas = findViewById(R.id.planificacionRecyclerView)
        calendarButton = findViewById(R.id.CalendarDate)
        buttonPlanNuevo = findViewById(R.id.crearPlan)
        titulo = findViewById(R.id.lbl_titulo)
        lblMensaje = findViewById(R.id.lbl_mensajeNinio)
        viewModel.recyclerView = findViewById(R.id.recycler_plan)
        dia = findViewById(R.id.lbl_dia)
        atras = findViewById(R.id.atras)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs, this)

        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewModel.recyclerView.layoutManager = layoutManagerLinear

        initTextSpeech()
        val isPlanificador = prefs.getBoolean("PlanificadorLogged", false)
        if(isPlanificador && (CommonUtils.isPortrait(this) && CommonUtils.isMobile(this) || !CommonUtils.isMobile(this))){
            initNotificationList()
        }

        dia.text = getString(R.string.formatted_date, viewModel.dayOfWeek.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, viewModel.dayOfMonth, viewModel.month)

        viewModel.initializeAnimations(applicationContext)

        observe()

        //Comprobar si hay parametros en caso de llamada desde el planificador
        if (this.intent.extras != null) {
            configureParameters()
        } else {
            if(savedInstanceState == null){
                viewModel.mostrarPlan(this)
            }else{
                viewModel.configureDataEvento(this)
            }
        }

        //--------------- FUNCIONALIDADES DE LOS BOTONES ---------------

        atras?.setOnClickListener {
            if (isTaskRoot) {
                startActivity(Intent(this@PlanActivity, MainActivity::class.java))
            }
            finish()
        }

        calendarButton.setOnClickListener {
            crearDialogo()
        }

        buttonPlanNuevo.setOnClickListener {
            val intent = Intent(this, CalendarioActivity::class.java)
            intent.putExtra("date", viewModel.fechaSeleccionada.toString())
            startActivity(intent)
        }

        //Este método se ejecutará al seleccionar el icono deshacer para volver un paso atrás en el seguimiento
        iconoDeshacer.setOnClickListener {
            if (!viewModel._pasosCompletados.value?.empty()!!) {
                viewModel.adaptador.optionMarcar = false
                viewModel.adaptador.notifyItemChanged(viewModel._pasosCompletados.value?.pop() as Int)
                viewModel._pasosCompletados.postValue(viewModel._pasosCompletados.value)
            }
        }

        //Este método se ejecutará al seleccionar el icono deshacer para marcar todos los pictogramas como no realizados
        iconoDeshacerTodas.setOnClickListener {
            if (!viewModel._pasosCompletados.value?.empty()!!) {
                viewModel.adaptador.optionMarcar = false
                for(i in 0 until viewModel._pasosCompletados.value?.size!!){
                    viewModel.adaptador.notifyItemChanged(i)
                    viewModel._pasosCompletados.value?.pop()
                    viewModel._pasosCompletados.postValue(viewModel._pasosCompletados.value)
                }
            }
        }

        //Este método se ejecutará al seleccionar el icono marcar para marcar el pictograma actual como realizado
        iconoMarcar.setOnClickListener {
            viewModel.adaptador.optionMarcar = true
            if (!viewModel._pasosCompletados.value?.empty()!!) {
                val posicion = viewModel._pasosCompletados.value?.peek() as Int
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
            viewModel.adaptador.optionMarcar = true
            for (i in 0 until viewModel.listaPictogramas.size){
                viewModel.adaptador.notifyItemChanged(i)
                viewModel._pasosCompletados.value?.add(i)
                viewModel._pasosCompletados.postValue(viewModel._pasosCompletados.value)
            }
        }

        iconoEscuchar.setOnClickListener {
            if (!viewModel.speechInProgress) {
                iconoEscuchar.text = getString(R.string.str_parar)
                CommonUtils.textToSpeechOn(viewModel.listaPictogramas)
                viewModel.speechInProgress = true
            } else {
                iconoEscuchar.text = getString(R.string.str_escuchar)
                CommonUtils.textToSpeech.stop()
                viewModel.speechInProgress = false
            }
        }

        iconoReproducir.setOnClickListener {
            reproducirEvento()
        }

        //--------------- OBLIGAR A GIRAR PANTALLA ---------------
     }

    private fun showDialogRotate(): Int {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogo_rotate_screen)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        return 0
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
         CalendarioUtilidades.fechaSeleccionada = LocalDate.now()

         viewModel.eventos = viewModel.evento.obtenerTodosEventos(viewModel.idUsuario, this) as ArrayList<Evento>
         viewModel.obtenerVistaMes()

         viewModel._fechaActual.observe(this) { fechaActual.text = it }

         viewModel._diasMes.observe(this) {
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
         }

         btnAnteriorMes.setOnClickListener {
             CalendarioUtilidades.fechaSeleccionada =
                 CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
             viewModel.obtenerVistaMes()
         }
         btnSiguienteMes.setOnClickListener {
             CalendarioUtilidades.fechaSeleccionada =
                 CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
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
            val isPlanificador = prefs.getBoolean("PlanificadorLogged", false)

            if (it != null) {
                    titulo.text = viewModel.tituloPlan
                    lblMensaje.visibility = View.INVISIBLE
                    buttonPlanNuevo.visibility = View.INVISIBLE
                    iconoDeshacer.visibility = View.VISIBLE
                    iconoDeshacerTodas.visibility = View.VISIBLE
                    iconoMarcar.visibility = View.VISIBLE
                    iconoMarcarTodas.visibility = View.VISIBLE
                    iconoEscuchar.visibility = View.VISIBLE
                    iconoReproducir.visibility = View.VISIBLE
                    iconoMarcar.isEnabled = true
                    iconoMarcarTodas.isEnabled = true
                    viewModel.recyclerView.visibility = View.VISIBLE

                if(CommonUtils.isMobile(this) && CommonUtils.isPortrait(this)){
                    showDialogRotate()
                }


            }else{
                Toast.makeText(this, "Error al cargar los pictogramas", Toast.LENGTH_SHORT).show()
            }

            viewModel.adaptador = AdaptadorPresentacion(it, viewModel)
            viewModel.adaptador.listMarcados = viewModel._pasosCompletados.value!!
            viewModel.recyclerView.adapter = viewModel.adaptador
            viewModel.adaptador.notifyDataSetChanged()

            if(isPlanificador && (CommonUtils.isPortrait(this) && CommonUtils.isMobile(this) || !CommonUtils.isMobile(this))){
                val layoutPlanificaciones = findViewById<LinearLayout>(R.id.layoutPlanificacionesFuturas)
                layoutPlanificaciones.visibility = View.VISIBLE
                //val imageDecoration = findViewById<ImageView>(R.id.imageDecoration)
              //  imageDecoration.visibility = View.GONE
            }else{
                iconoDeshacerTodas.visibility = View.INVISIBLE
                iconoMarcarTodas.visibility = View.INVISIBLE
            }
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
                iconoEscuchar.visibility = View.INVISIBLE
                iconoReproducir.visibility = View.INVISIBLE

                if(isPlanificador && (CommonUtils.isPortrait(this) && CommonUtils.isMobile(this) || !CommonUtils.isMobile(this))){
                    buttonPlanNuevo.visibility = View.VISIBLE
                    val layoutPlanificaciones = findViewById<LinearLayout>(R.id.layoutPlanificacionesFuturas)
                    layoutPlanificaciones.visibility = View.VISIBLE
                }else{
                    buttonPlanNuevo.visibility = View.INVISIBLE
                    val layoutPlanificaciones = findViewById<LinearLayout>(R.id.layoutPlanificacionesFuturas)
                    layoutPlanificaciones.visibility = View.GONE
                }

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

    }

    private fun initTextSpeech(){
        CommonUtils.initializeTextToSpeech(this)
        CommonUtils.listener = this
    }

    private fun initNotificationList(){
        val notificationList : ArrayList<PlanificacionItem> = viewModel.mostrarPlanificaciones()
        planificacionesFuturas.layoutManager = LinearLayoutManager(this)
        val adaptadorNot = AdaptadorPlanificacionesFuturas(notificationList, viewModel)
        planificacionesFuturas.adapter = adaptadorNot
    }

    private fun configureParameters(){
        viewModel._pasosCompletados.value = Stack()
        viewModel.tituloPlan = intent.getStringExtra("titulo")!! //PETA AQUI CUANDO SE INTENTA COMPARTIR UN EVENTO
        viewModel.listaPictogramas = intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>
        val fecha = intent.getSerializableExtra("fecha") as LocalDate
        val diaSemana = fecha.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }
        val mes = fecha.month.getDisplayName(TextStyle.FULL, Locale("es"))
        dia.text = "$diaSemana, ${fecha.dayOfMonth} de $mes"
        viewModel._planLiveData.value = viewModel.listaPictogramas
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
        viewModel.adaptador.listMarcados.clear()
        viewModel._pasosCompletados.value?.clear()
        viewModel.adaptador.notifyDataSetChanged()
        viewModel.stopReproductor()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun configureIsRunning(){
        iconoReproducir.setIconResource(R.drawable.svg_stop)
        viewModel.adaptador.optionMarcar = false
        viewModel.adaptador.listMarcados.clear()
        viewModel._pasosCompletados.value?.clear()
        viewModel.adaptador.notifyDataSetChanged()

        iconoMarcar.isEnabled = false
        iconoMarcarTodas.isEnabled = false
        iconoDeshacer.isEnabled = false
        iconoDeshacerTodas.isEnabled = false

        viewModel.currentPosition = 0
        viewModel.isRunning = true
    }

    override fun onSpeechDone() {
        iconoEscuchar.text = getString(R.string.str_escuchar)
    }
}

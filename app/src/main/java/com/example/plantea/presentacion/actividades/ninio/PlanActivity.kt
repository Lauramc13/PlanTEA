package com.example.plantea.presentacion.actividades.ninio

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.dominio.PlanificacionItem
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.MainActivity
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacionesFuturas
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.Stack


class PlanActivity : AppCompatActivity(), AdaptadorPresentacion.OnItemSelectedListener, AdaptadorCalendario.OnItemSelectedListener, AdaptadorPlanificacionesFuturas.OnItemSelectedListener, CommonUtils.TextToSpeechListener {
    lateinit var prefs: SharedPreferences
    lateinit var listaPictogramas: ArrayList<Pictograma>
    var plan = Planificacion()
    lateinit var titulo: TextView
    private lateinit var mensajePremio: TextView
    private lateinit var lblMensaje: TextView
    private lateinit var tituloObtenido: String
    private lateinit var buttonPlanNuevo : Button
    private lateinit var iconoEscuchar: Button
    private lateinit var iconoReproducir: MaterialButton
    private lateinit var iconoDeshacer: Button
    private lateinit var iconoDeshacerTodas: Button
    private lateinit var iconoMarcar: Button
    private lateinit var iconoMarcarTodas: Button
   // private lateinit var iconoReproducirLento: Button
   // private lateinit var iconoReproducirRapido: Button
    private lateinit var imagenConfeti: ImageView
    private lateinit var pasosCompletados: Stack<Int>
    private lateinit var adaptador: AdaptadorPresentacion
    private lateinit var dia: TextView
    private lateinit var dialogoPresentacion: ConstraintLayout
    private lateinit var backButton: Button

    private lateinit var btnSiguienteMes: ImageView
    private lateinit var btnAnteriorMes: ImageView
    private lateinit var calendario: RecyclerView
    private lateinit var planificacionesFuturas: RecyclerView
    private lateinit var cerrarDialog: Button
    private lateinit var fechaActual: TextView
    private lateinit var dias: ArrayList<LocalDate?>
    private lateinit var adaptadorCalendario: AdaptadorCalendario
    lateinit var eventos: ArrayList<Evento>
    var evento = Evento()
    //private lateinit var textToSpeech: TextToSpeech
    private var fechaSeleccionada : LocalDate = LocalDate.now()

    private var reproduccionLenta = false
    private var reproduccionRapida = false

    private lateinit var selectedDate: String
    private lateinit var calendarButton: Button

    private lateinit var btnCerrar: ImageView

    private lateinit var recyclerView: RecyclerView
    private var recyclerViewState: Parcelable? = null

    private var dialog: Dialog? = null

    val handler = Handler()
    private var currentDialog: Dialog? = null
    private var isRunning = false
    private var currentRunnable: Runnable? = null
    private var currentPosition: Int = 0
    private lateinit var animFondo: Animation
    private lateinit var animCard: Animation
    lateinit var historia: ConstraintLayout

    companion object {
        const val FECHA_SELECCIONADA = "FECHA_SELECCIONADA" // const key to save/read value from bundle
    }

    override fun diaSeleccionado(fecha: LocalDate) {
        if (fecha != null) {
            fechaSeleccionada = fecha
            CalendarioUtilidades.fechaSeleccionada = fecha
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormatter.format(fecha)

            val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
            val dayOfWeek = dayOfWeekFormatter.format(fecha)
            val dayOfMonth = fecha.dayOfMonth.toString()
            val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
            val month = monthFormatter.format(fecha)

            dia.text =  getString(R.string.formatted_date, dayOfWeek, dayOfMonth, month)
            mostrarPlan()
            dialog?.dismiss()
        }
    }
/*
    override fun onItemSeleccionadoFuturo(fecha: LocalDate){

        fechaSeleccionada = fecha
        CalendarioUtilidades.fechaSeleccionada = fecha
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormatter.format(fecha)

        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        val dayOfWeek = dayOfWeekFormatter.format(fecha)
        val dayOfMonth = fecha.dayOfMonth.toString()
        val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
        val month = monthFormatter.format(fecha)

        dia.text =  getString(R.string.formatted_date, dayOfWeek, dayOfMonth, month)
        mostrarPlan()
        dialog?.dismiss()
    }*/

    /*override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val layoutParams = historia.layoutParams as? ConstraintLayout.LayoutParams
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
            layoutParams?.width = 350.dpToPx(this)
            historia.layoutParams = layoutParams
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
            layoutParams?.width = 250.dpToPx(this)
            historia.layoutParams = layoutParams
        }
    }*/

    private fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

    override fun onStop() {
        super.onStop()
        isRunning = false
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the RecyclerView state before the activity is destroyed
        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
       // outState.putParcelable("recycler_view_state", recyclerViewState)
        outState.putString(FECHA_SELECCIONADA, fechaSeleccionada.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val fechaSel = savedInstanceState.getString(FECHA_SELECCIONADA)
        //val recyclerView2 = savedInstanceState.getString("recycler_view_state")

        if (!fechaSel.isNullOrEmpty()) {
            diaSeleccionado(LocalDate.parse(fechaSel))
        }
       // mostrarPlan()
       // cambiarPicto(1) TODO: QUE SE GUARDE EL ESTADO DE LOS PICTOGRAMAS QUE SE HAN HECHO
    }

    private fun obtenerVistaMes() {
        fechaActual.text = CalendarioUtilidades.formatoMesAnio(CalendarioUtilidades.fechaSeleccionada).uppercase(Locale.getDefault())
        //Calcular días del mes y mostrar
        dias = CalendarioUtilidades.obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
        calendario.layoutManager = GridLayoutManager(this, 7)
        adaptadorCalendario = AdaptadorCalendario(dias, this)
        calendario.adapter = adaptadorCalendario
    }


    private fun backCallBack(): OnBackPressedCallback{
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount == 0) {
                    startActivity(Intent(this@PlanActivity, MainActivity::class.java))
                } else {
                    supportFragmentManager.popBackStack()
                }
                finish()
            }
        }
        return callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)

        // Si se va hacia atras y no hay nada en la cola, se redirige a MainActivity
        val callback = backCallBack()
        onBackPressedDispatcher.addCallback(this, callback)

        //Pila de los pasos completados en el seguimiento de un plan
        pasosCompletados = Stack<Int>()
        iconoDeshacer = findViewById(R.id.icon_deshacer)
        iconoDeshacerTodas = findViewById(R.id.icon_deshacerTodas)
        iconoMarcar = findViewById(R.id.icon_marcar)
        iconoMarcarTodas = findViewById(R.id.icon_marcarTodas)
        iconoEscuchar = findViewById(R.id.icon_escuchar)
        iconoReproducir = findViewById(R.id.icon_reproducir)
        //iconoReproducirLento = findViewById(R.id.icon_reproducir_lento)
        //iconoReproducirRapido = findViewById(R.id.icon_reproducir_rapido)
        calendarButton = findViewById(R.id.CalendarDate)
        buttonPlanNuevo = findViewById(R.id.crearPlan)
        titulo = findViewById(R.id.lbl_titulo)
        lblMensaje = findViewById(R.id.lbl_mensajeNinio)
        recyclerView = findViewById(R.id.recycler_plan)
        planificacionesFuturas = findViewById(R.id.planificacionRecyclerView)
        dia = findViewById(R.id.lbl_dia)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val userId = prefs.getString("idUsuario", "")
        val planificaciones = userId?.let { evento.obtenerTodosEventos(it, this) } as ArrayList<Evento>

        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManagerLinear

        var speechInProgress = false
        CommonUtils.initializeTextToSpeech(this)
        CommonUtils.listener = this

        val notificationList : ArrayList<PlanificacionItem> = mostrarPlanificaciones(planificaciones)
        planificacionesFuturas.layoutManager = LinearLayoutManager(this)
        val adaptadorNot = AdaptadorPlanificacionesFuturas(notificationList, this)
        planificacionesFuturas.adapter = adaptadorNot

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val dayOfWeek = dateFormat.format(calendar.time)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = monthFormat.format(calendar.time)
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        dia.text = getString(R.string.formatted_date, dayOfWeek.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, dayOfMonth, month)

        initializeAnimations()

        //Comprobar si hay parametros en caso de llamada desde el planificador
        val parametros = this.intent.extras
        if (parametros != null) {
            titulo.text = intent.getStringExtra("titulo")
            listaPictogramas = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!
            adaptador = AdaptadorPresentacion(listaPictogramas, this)
            recyclerView.adapter = adaptador
            lblMensaje.visibility = View.INVISIBLE
            buttonPlanNuevo.visibility = View.INVISIBLE
            iconoDeshacer.visibility = View.VISIBLE
            iconoDeshacerTodas.visibility = View.VISIBLE
            iconoMarcar.visibility = View.VISIBLE
            iconoMarcarTodas.visibility = View.VISIBLE
            iconoEscuchar.visibility = View.VISIBLE
            iconoReproducir.visibility = View.VISIBLE
            //iconoReproducirLento.visibility = View.VISIBLE
            //iconoReproducirRapido.visibility = View.VISIBLE
        } else {
            mostrarPlan()
        }

        //--------------- FUNCIONALIDADES DE LOS BOTONES ---------------

        /*backButton.setOnClickListener{
            if (supportFragmentManager.backStackEntryCount == 0) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // otherwise, pop back stack
                supportFragmentManager.popBackStack()
            }
            finish()
        }*/

        calendarButton.setOnClickListener {
            dialog = Dialog(this)
            dialog!!.setContentView(R.layout.dialogo_calendario)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            fechaActual = dialog!!.findViewById(R.id.lbl_mes2)
            btnSiguienteMes = dialog!!.findViewById(R.id.image_calendar_siguiente2)
            btnAnteriorMes = dialog!!.findViewById(R.id.image_calendar_anterior2)
            calendario = dialog!!.findViewById(R.id.recycler_calendario)
            cerrarDialog = dialog!!.findViewById(R.id.icono_CerrarDialogoEvento)
            CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
            obtenerVistaMes()
            val userId = prefs.getString("idUsuario", "")
            eventos = userId?.let {
                evento.obtenerEventos(it, this, CalendarioUtilidades.fechaSeleccionada)
            } as ArrayList<Evento>


            btnAnteriorMes.setOnClickListener {
                CalendarioUtilidades.fechaSeleccionada =
                    CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
                obtenerVistaMes()
            }
            btnSiguienteMes.setOnClickListener {
                CalendarioUtilidades.fechaSeleccionada =
                    CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
                obtenerVistaMes()
            }

            cerrarDialog.setOnClickListener { dialog!!.dismiss() }
            dialog!!.show()
        }

        buttonPlanNuevo.setOnClickListener {
            Toast.makeText(this, "Crear plan", Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, CalendarioActivity::class.java))
        }

        //Este método se ejecutará al seleccionar el icono deshacer para volver un paso atrás en el seguimiento
        iconoDeshacer.setOnClickListener {
            if (!pasosCompletados.empty()) {
                val posicionUndo = pasosCompletados.pop() as Int
                cambiarPicto(posicionUndo)

                if (pasosCompletados.isEmpty()) {
                    iconoDeshacer.isEnabled = false
                    iconoDeshacerTodas.isEnabled = false
                }

                iconoMarcarTodas.isEnabled = true
                iconoMarcar.isEnabled = true
            }
        }

        //Este método se ejecutará al seleccionar el icono deshacer para marcar todos los pictogramas como no realizados
        iconoDeshacerTodas.setOnClickListener {
            if (!pasosCompletados.empty()) {
                for(i in 0 until pasosCompletados.size){
                    val posicionUndo = pasosCompletados.pop() as Int
                    cambiarPicto(posicionUndo)
                }

                iconoDeshacerTodas.isEnabled = false
                iconoDeshacer.isEnabled = false
                iconoMarcarTodas.isEnabled = true
                iconoMarcar.isEnabled = true
            }
        }

        //Este método se ejecutará al seleccionar el icono marcar para marcar el pictograma actual como realizado
        iconoMarcar.setOnClickListener {

            if (!pasosCompletados.empty()) {
                val posicion = pasosCompletados.peek() as Int
                cambiarPictoClickedNormal(posicion+1)
                pasosCompletados.add(posicion+1)
            }else{
                cambiarPictoClickedNormal(0)
                pasosCompletados.add(0)
            }

            iconoDeshacerTodas.isEnabled = true
            iconoDeshacer.isEnabled = true

            if(pasosCompletados.size == listaPictogramas.size){
                iconoMarcarTodas.isEnabled = false
                iconoMarcar.isEnabled = false
            }
        }

        // Este método se ejecutará al seleccionar el icono marcar para marcar todos los pictogramas como realizados
        iconoMarcarTodas.setOnClickListener {
            for (i in 0 until listaPictogramas.size){
                cambiarPictoClickedNormal(i)
                pasosCompletados.add(i)
            }

            iconoMarcarTodas.isEnabled = false
            iconoMarcar.isEnabled = false
            iconoDeshacerTodas.isEnabled = true
            iconoDeshacer.isEnabled = true
        }

        iconoEscuchar.setOnClickListener {
            if (!speechInProgress) {
                iconoEscuchar.text = getString(R.string.str_parar)
                CommonUtils.textToSpeechOn(listaPictogramas)
                speechInProgress = true
            } else {
                iconoEscuchar.text = getString(R.string.str_escuchar)
                CommonUtils.textToSpeech.stop()
                speechInProgress = false
            }
        }

        iconoReproducir.setOnClickListener {
            /*reproduccionLenta = false
            reproduccionRapida = false*/
            reproducirEvento(1500L)
        }

       /* iconoReproducirLento.setOnClickListener {
            reproduccionLenta = true
            reproduccionRapida = false
            reproducirEvento(7000L)
        }

        iconoReproducirRapido.setOnClickListener {
            reproduccionRapida = true
            reproduccionLenta = false
            reproducirEvento(2500L)
        }*/
    }

    private fun reproducirEvento(tiempo: Long) {

        //val drawableIconStop = ContextCompat.getDrawable(this, R.drawable.svg_stop)
        //val drawableIconPlay = ContextCompat.getDrawable(this, R.drawable.svg_play)
        val screenWidthInDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val targetHeight: Float
        val targetWidth: Float
        if (screenWidthInDp < 800) {
            //Es (129, 164) y no (115,150) ya que le sumamos el margen
            targetHeight = 129.dpToPx(this).toFloat()
            targetWidth = 164.dpToPx(this).toFloat()
        }else{
            targetHeight = 170.dpToPx(this).toFloat()
            targetWidth = 200.dpToPx(this).toFloat()
        }

        if(isRunning){
            animationReproduccion(targetHeight, targetWidth, currentPosition, 1f)
            iconoReproducir.setIconResource(R.drawable.svg_play)
            iconoDeshacer.isEnabled = true
            iconoMarcar.isEnabled = false
            iconoMarcarTodas.isEnabled = false
            iconoDeshacerTodas.isEnabled = true
            iconoDeshacerTodas.performClick()
            stopReproductor()
        }else{
            iconoReproducir.setIconResource(R.drawable.svg_stop)
            iconoDeshacerTodas.performClick()
            iconoMarcar.isEnabled = false
            iconoDeshacer.isEnabled = false
            iconoMarcarTodas.isEnabled = false
            iconoDeshacerTodas.isEnabled = false

            currentPosition = 0
            isRunning = true

            currentRunnable = object : Runnable {
                override fun run() {
                    if (isRunning) {
                        //onItemSeleccionado(currentPosition)
                        if (currentPosition < listaPictogramas.size) {
                            handler.postDelayed(this, tiempo)
                            cambiarPictoClicked(currentPosition)
                            pasosCompletados.add(currentPosition)
                        } else {
                            lastPictoClicked(currentPosition)
                            stopReproductor()
                            iconoDeshacer.isEnabled = true
                            iconoMarcar.isEnabled = false
                            iconoMarcarTodas.isEnabled = false
                            iconoDeshacerTodas.isEnabled = true
                            iconoReproducir.setIconResource(R.drawable.svg_stop)
                        }
                        currentPosition++
                    }
                }
            }

            handler.post(currentRunnable!!)
        }
    }

    fun stopReproductor() {
        isRunning = false
        currentRunnable?.let {
            handler.removeCallbacksAndMessages(null)
            currentRunnable = null
        }
        currentRunnable = null
        currentPosition = 0
       /* currentDialog?.dismiss()
        currentDialog = null*/
    }

    private fun mostrarPlan() {
        val idUsuario = prefs.getString("idUsuario", "")
        //Mostrar la planificación a seguir para el niño
        listaPictogramas = ArrayList()
        listaPictogramas = idUsuario?.let {
            plan.mostrarPlanificacion(
                it,
                selectedDate,
                this
            )
        } as ArrayList<Pictograma>
        //Mostrar título de la planificación
        tituloObtenido = plan.obtenerTituloPlan(idUsuario, selectedDate, this)
        titulo.text = tituloObtenido

        adaptador = AdaptadorPresentacion(listaPictogramas, this)
        recyclerView.adapter = adaptador
        val isPlanificador = prefs.getBoolean("PlanificadorLogged", false)

        //Mostrar mensaje si no hay plan
        if (listaPictogramas.isEmpty()) {
            lblMensaje.visibility = View.VISIBLE
            iconoDeshacer.visibility = View.INVISIBLE
            iconoDeshacerTodas.visibility = View.INVISIBLE
            iconoMarcar.visibility = View.INVISIBLE
            iconoMarcarTodas.visibility = View.INVISIBLE
            iconoEscuchar.visibility = View.INVISIBLE
            iconoReproducir.visibility = View.INVISIBLE
            //iconoReproducirLento.visibility = View.INVISIBLE
            //iconoReproducirRapido.visibility = View.INVISIBLE
            if(isPlanificador) {
                buttonPlanNuevo.visibility = View.VISIBLE
            }
        } else {
            lblMensaje.visibility = View.INVISIBLE
            buttonPlanNuevo.visibility = View.INVISIBLE
            iconoDeshacer.visibility = View.VISIBLE
            iconoDeshacerTodas.visibility = View.VISIBLE
            iconoMarcar.visibility = View.VISIBLE
            iconoMarcarTodas.visibility = View.VISIBLE
            iconoEscuchar.visibility = View.VISIBLE
            iconoReproducir.visibility = View.VISIBLE

           // iconoReproducirLento.visibility = View.VISIBLE
            //iconoReproducirRapido.visibility = View.VISIBLE

        }
        if(isPlanificador) {
            val layoutPlanificaciones = findViewById<LinearLayout>(R.id.layoutPlanificacionesFuturas)
            layoutPlanificaciones.visibility = View.VISIBLE
            val imageDecoration = findViewById<ImageView>(R.id.imageDecoration)
            imageDecoration.visibility = View.GONE
        }else{
            iconoDeshacerTodas.visibility = View.INVISIBLE
            iconoMarcarTodas.visibility = View.INVISIBLE
        }
    }

    override fun onItemSeleccionado(posicion: Int) {
        if (currentDialog != null && currentDialog!!.isShowing) {
            currentDialog!!.dismiss()
        }

        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dialogo_presentacion)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnCerrar = dialog!!.findViewById(R.id.icono_CerrarDialogoEvento)
        val pictograma = dialog!!.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog!!.findViewById<TextView>(R.id.lbl_pictograma)
        historia = dialog!!.findViewById(R.id.Bubble)
        imagenConfeti = dialog!!.findViewById(R.id.img_confeti)
        mensajePremio = dialog!!.findViewById(R.id.txt_premio)
        dialogoPresentacion = dialog!!.findViewById(R.id.dialogo_presentacion_2)
        pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
        tituloPictograma.text = listaPictogramas[posicion].titulo

        dialogoPresentacion.clearAnimation()
        imagenConfeti.clearAnimation()
        mensajePremio.clearAnimation()
        imagenConfeti.visibility = View.INVISIBLE
        mensajePremio.visibility = View.INVISIBLE

        val textoHistoria = dialog!!.findViewById<TextView>(R.id.lblBubble)
        val avatarHistoria = dialog!!.findViewById<ShapeableImageView>(R.id.avatarBubble)

        // Si tenemos historias
        if (listaPictogramas[posicion].historia != "null") {
            textoHistoria.text = listaPictogramas[posicion].historia
            historia.visibility = View.VISIBLE
            if (prefs.getString("imagenPlanificador", "") === "") {
                avatarHistoria.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            } else {
                avatarHistoria.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
            }
        } else {
            historia.visibility = View.GONE
        }

            if (listaPictogramas[posicion].categoria == 9 || listaPictogramas[posicion].categoria == 8) {
                animacionesConfeti(listaPictogramas[posicion].categoria)
            }

            pasosCompletados.push(posicion)
            if (posicion == 0) {
                iconoDeshacer.isEnabled = true
                iconoDeshacerTodas.isEnabled = true
            }
            currentDialog = dialog

            //Botón cerrar
            btnCerrar.setOnClickListener { dialog!!.dismiss() }
            dialog!!.show()

        /*} else {
            val showAnimation: Animation
            val delayBeforeDismiss: Long
            val dismissAnimation = AnimationUtils.makeOutAnimation(this, false)

            if (reproduccionLenta) {
                showAnimation =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_side_slow)
                delayBeforeDismiss = 3000L
                dismissAnimation.duration = 1000L
            } else if (reproduccionRapida) {
                showAnimation =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_side_fast)
                delayBeforeDismiss = 1000L
                dismissAnimation.duration = 500L
            } else {
                showAnimation =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_side)
                delayBeforeDismiss = 2000L
                dismissAnimation.duration = 500L
            }

            dialogoPresentacion.visibility = View.VISIBLE
            dialogoPresentacion.startAnimation(showAnimation)
            val totalDuration =
                showAnimation.duration + delayBeforeDismiss + dismissAnimation.duration

            //Comienza la animacion de los premios
            if (listaPictogramas[posicion].categoria == 9 || listaPictogramas[posicion].categoria == 8) {
                handler.postDelayed({
                    animacionesConfeti(listaPictogramas[posicion].categoria)
                }, showAnimation.duration)
            }

            //Comienza la animación de esconder el pictograma
            handler.postDelayed({
                dialogoPresentacion.clearAnimation()
                dialogoPresentacion.startAnimation(dismissAnimation)
            }, showAnimation.duration + delayBeforeDismiss)

            //Cuando se ha terminado la animación pone el pictograma invisible
            if (posicion != listaPictogramas.size - 1) {
                handler.postDelayed({
                    dialogoPresentacion.visibility = View.GONE
                }, totalDuration)
            }

            //Botón cerrar
            btnCerrar.setOnClickListener {
                dialog!!.dismiss()
                stopReproductor()
            }

            dialog!!.setOnCancelListener {
                stopReproductor()
            }
        }*/
        dialog!!.show()
    }

    override fun checkPosition(posicion: Int): Boolean {
        return if(pasosCompletados.isEmpty() ){
            posicion == 0
        }else{
            pasosCompletados.peek() + 1 == posicion
        }
    }

    private fun initializeAnimations() {
        animFondo = AnimationUtils.loadAnimation(applicationContext, R.anim.confeti)
        animCard = AnimationUtils.loadAnimation(applicationContext, R.anim.card)
    }

    private fun animacionesConfeti(categoria: Int) {

        imagenConfeti.visibility = View.VISIBLE
        mensajePremio.visibility = View.VISIBLE

        imagenConfeti.clearAnimation()
        mensajePremio.clearAnimation()

        if (categoria == 9) {
            imagenConfeti.animation = animFondo
            animFondo.start()
            mensajePremio.animation = animFondo
            animFondo.start()
        } else if (categoria == 8) {
            imagenConfeti.setImageResource(R.drawable.svg_espera)
            mensajePremio.text = getString(R.string.str_esperar)
            imagenConfeti.animation = animCard
            animCard.start()
            mensajePremio.animation = animCard
            animCard.start()
        }
    }


    private fun mostrarPlanificaciones(planificaciones: ArrayList<Evento>): ArrayList<PlanificacionItem> {
        val lista: ArrayList<PlanificacionItem> = ArrayList()

        for (planificacion in planificaciones) {
            planificacion.fecha?.let { fechaPlanificacion ->
                val datePlanificacion = LocalDate.parse(fechaPlanificacion.toString())

                if (!datePlanificacion.isBefore(LocalDate.now()) && planificacion.visible == 1) {
                    planificacion.nombre?.let {
                        lista.add(PlanificacionItem(it, fechaPlanificacion.toString()))
                    }
                }
            }
        }

        lista.sortBy { it.date }
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
        for (notification in lista) {
            val dateFormatted = LocalDate.parse(notification.date, DateTimeFormatter.ISO_LOCAL_DATE)
            notification.date = dateFormatted.format(formatter)
        }

        return lista
    }

    private fun cambiarPicto(posicion: Int){
        val viewHolderPictogramas = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas
        viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card_picto).animate().alpha(1f).setDuration(100).start()

        when (listaPictogramas[posicion].categoria) {
            9 -> {
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card)
                    .setBackgroundResource(R.drawable.card_premio)
            }
            8 -> {
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card)
                    .setBackgroundResource(R.drawable.card_espera)
            }
            else -> {
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card)
                    .setBackgroundResource(R.drawable.card_personalizado)
            }
        }


    }

    /*private fun cambiarPictoClicked(posicion: Int){
        val viewHolderPictogramas = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas?
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card)?.setBackgroundResource(R.drawable.card_pronunced)

       // convert dp into pixels
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card_picto)?.layoutParams?.height = 180.dpToPx(this)
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card_picto)?.layoutParams?.width = 220.dpToPx(this)
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card_picto)?.requestLayout()

        if(posicion !=0){
            lastPictoClicked(posicion)
        }

    }

    private fun lastPictoClicked(posicion: Int){
        val viewHolderPictogramas= recyclerView.findViewHolderForAdapterPosition(posicion-1) as AdaptadorPresentacion.ViewHolderPictogramas?
        viewHolderPictogramas!!.itemView.findViewById<View>(R.id.id_Imagen).alpha = 0.7f
        viewHolderPictogramas.itemView.findViewById<View>(R.id.id_Texto).alpha = 0.7f
        viewHolderPictogramas.itemView.findViewById<View>(R.id.btn_historiaPictoOn).alpha = 0.7f
        viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card).setBackgroundResource(R.drawable.card_disabled)
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card_picto)?.layoutParams?.height = 170.dpToPx(this)
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card_picto)?.layoutParams?.width = 200.dpToPx(this)
        viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card_picto)?.requestLayout()

    }
*/

    private fun cambiarPictoClicked(posicion: Int) {
        val viewHolderPictogramas = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas?
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card)?.setBackgroundResource(R.drawable.card_pronunced)

        val targetHeight : Float
        val targetWidth : Float

        if (CommonUtils.isMobile(this)) {
            targetHeight = 145.dpToPx(this).toFloat()
            targetWidth = 180.dpToPx(this).toFloat()
        }else{
            targetHeight = 185.dpToPx(this).toFloat()
            targetWidth = 220.dpToPx(this).toFloat()
        }

        animationReproduccion(targetHeight, targetWidth, posicion, 1f)

        if(posicion !=0){
            lastPictoClicked(posicion)
        }
    }

    private fun cambiarPictoClickedNormal(posicion: Int){
        val viewHolderPictogramas = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas?
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card)?.setBackgroundResource(R.drawable.card_disabled)
        viewHolderPictogramas!!.itemView.findViewById<View>(R.id.id_card_picto).alpha = 0.7f
    }

    private fun lastPictoClicked(posicion: Int){
        val viewHolderPictogramas = recyclerView.findViewHolderForAdapterPosition(posicion-1) as AdaptadorPresentacion.ViewHolderPictogramas?
        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card)?.setBackgroundResource(R.drawable.card_disabled)

        val targetHeight : Float
        val targetWidth : Float

        if (CommonUtils.isMobile(this)) {
            targetHeight = 129.dpToPx(this).toFloat()
            targetWidth = 164.dpToPx(this).toFloat()
        }else{
            targetHeight = 170.dpToPx(this).toFloat()
            targetWidth = 200.dpToPx(this).toFloat()
        }


        animationReproduccion(targetHeight, targetWidth, posicion-1, 0.7f)
    }


    private fun animationReproduccion(targetHeight: Float, targetWidth: Float, posicion: Int, alpha: Float){
        val viewHolderPictogramas = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas?

        viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card_picto)
            ?.animate()
            ?.setDuration(250)
            ?.alpha(alpha)
            ?.scaleX(targetWidth / viewHolderPictogramas.itemView.width)
            ?.scaleY(targetHeight / viewHolderPictogramas.itemView.height)
            ?.withEndAction {
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card_picto)
                    ?.layoutParams?.height = targetHeight.toInt()
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card_picto)
                    ?.layoutParams?.width = targetWidth.toInt()
            }

        if(posicion !=0){
            lastPictoClicked(posicion)
        }

    }



    override fun onSpeechDone() {
        iconoEscuchar.text = getString(R.string.str_escuchar)
    }


}

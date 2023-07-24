package com.example.plantea.presentacion.actividades.ninio

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.ConfiguracionActivity
import com.example.plantea.presentacion.actividades.ManualActivity
import com.example.plantea.presentacion.actividades.PreLoginActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class PlanActivity : AppCompatActivity(), AdaptadorPresentacion.OnItemSelectedListener, AdaptadorCalendario.OnItemSelectedListener {
    lateinit var listaPictogramas: ArrayList<Pictograma>
    var plan = Planificacion()
    lateinit var titulo: TextView
    lateinit var mensajePremio: TextView
    lateinit var lblMensaje: TextView
    lateinit var tituloObtenido: String
    lateinit var iconoCuaderno: LinearLayout
    lateinit var iconoActividad: LinearLayout
    lateinit var iconoDeshacer: Button
    lateinit var iconoEscuchar: Button
    lateinit var iconoReproducir: Button
    lateinit var iconoReproducirLento: Button
    lateinit var iconoReproducirRapido: Button
    lateinit var imagenConfeti: ImageView
    lateinit var card: CardView
    lateinit var card_cuaderno : CardView
    lateinit var card_actividades: CardView
    lateinit var pasosCompletados: Stack<Int>
    lateinit var adaptador: AdaptadorPresentacion
    lateinit var dia: TextView
    lateinit var dialogo_presentacion: ConstraintLayout
    var textToSpeech: TextToSpeech? = null


    private lateinit var btn_siguienteMes: ImageView
    private lateinit var btn_anteriorMes: ImageView
    private lateinit var calendario: RecyclerView
    private lateinit var cerrarDialog : ImageView
    private lateinit var fechaActual: TextView
    private lateinit var dias: ArrayList<LocalDate?>
    lateinit var adaptadorCalendario: AdaptadorCalendario
    lateinit var eventos: ArrayList<Evento>
    var evento = Evento()

    var reproduccionLenta = false
    var reproduccionRapida = false

    lateinit var selectedDate: String
    lateinit var calendarButton: Button

    lateinit var btn_cerrar : ImageView

    private lateinit var recyclerView: RecyclerView
    private var recyclerViewState: Parcelable? = null

    private var dialog: Dialog? = null

    lateinit var btn_logout: Button
    private lateinit var icono_cerrar_login: ImageView

    val handler = Handler()
    private var currentDialog: Dialog? = null
    var reproductor : Boolean = false
    private var isRunning = false
    private var currentRunnable: Runnable? = null
    private var currentPosition: Int = 0

    override fun diaSeleccionado(fecha: LocalDate?) {
        if (fecha != null) {
            CalendarioUtilidades.fechaSeleccionada = fecha
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormatter.format(fecha)

            val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
            val dayOfWeek = dayOfWeekFormatter.format(fecha)
            val dayOfMonth = fecha.dayOfMonth.toString()
            val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
            val month = monthFormatter.format(fecha)

            dia.text = dayOfWeek.replaceFirstChar { it.titlecase() } + ", $dayOfMonth de $month"
            mostrarPlan()
            dialog?.dismiss()
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Horizontal", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Vertical", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the RecyclerView state before the activity is destroyed
        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        outState.putParcelable("recycler_view_state", recyclerViewState)
    }

    private fun obtenerVistaMes() {
        fechaActual.text = CalendarioUtilidades.formatoMesAnio(CalendarioUtilidades.fechaSeleccionada)
            .uppercase(Locale.getDefault())
        //Calcular días del mes y mostrar
        dias = CalendarioUtilidades.obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
        calendario.layoutManager = GridLayoutManager(this, 7)
        adaptadorCalendario = AdaptadorCalendario(dias, this)
        calendario.adapter = adaptadorCalendario
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Pila de los pasos completados en el seguimiento de un plan
        pasosCompletados = Stack<Int>()
        iconoCuaderno = findViewById(R.id.img_cuaderno)
        iconoActividad = findViewById(R.id.img_actividad)
        card_cuaderno = findViewById(R.id.card_cuaderno)
        iconoDeshacer = findViewById(R.id.icon_deshacer)
        iconoEscuchar = findViewById(R.id.icon_escuchar)
        iconoReproducir = findViewById(R.id.icon_reproducir)
        iconoReproducirLento = findViewById(R.id.icon_reproducir_lento)
        iconoReproducirRapido = findViewById(R.id.icon_reproducir_rapido)
        calendarButton = findViewById(R.id.CalendarDate)

        titulo = findViewById(R.id.lbl_titulo)
        lblMensaje = findViewById(R.id.lbl_mensajeNinio)
        recyclerView = findViewById(R.id.recycler_plan)

        dia = findViewById(R.id.lbl_dia)
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val dayOfWeek = dateFormat.format(calendar.time)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val month = monthFormat.format(calendar.time)
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        dia.text = dayOfWeek.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } + ", " + dayOfMonth + " de " + month

       // CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
       // obtenerVistaMes()

        calendarButton.setOnClickListener {
            dialog = Dialog(this)
            dialog!!.setContentView(R.layout.dialogo_calendario)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            fechaActual = dialog!!.findViewById(R.id.lbl_mes2)
            btn_siguienteMes = dialog!!.findViewById(R.id.image_calendar_siguiente2)
            btn_anteriorMes = dialog!!.findViewById(R.id.image_calendar_anterior2)
            calendario = dialog!!.findViewById(R.id.recycler_calendario)
            cerrarDialog = dialog!!.findViewById(R.id.icono_CerrarDialogoEvento)
            CalendarioUtilidades.fechaSeleccionada = LocalDate.now()
            obtenerVistaMes()
            val prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
            val userId = prefs.getString("idUsuario", "")
            eventos = userId?.let { evento.obtenerEventos(it, this, CalendarioUtilidades.fechaSeleccionada) } as ArrayList<Evento>

            btn_anteriorMes.setOnClickListener {
                CalendarioUtilidades.fechaSeleccionada =
                    CalendarioUtilidades.fechaSeleccionada.minusMonths(1)
                obtenerVistaMes()
            }
            btn_siguienteMes.setOnClickListener {
                CalendarioUtilidades.fechaSeleccionada =
                    CalendarioUtilidades.fechaSeleccionada.plusMonths(1)
                obtenerVistaMes()
            }

            cerrarDialog.setOnClickListener { dialog!!.dismiss() }
            dialog!!.show()
        }

        card_actividades = findViewById(R.id.card_actividad)
        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.layoutManager = layoutManagerLinear

        // Restore the RecyclerView state if it was saved before
        if (savedInstanceState != null) {
            recyclerViewState =
                savedInstanceState.getParcelable("recycler_view_state") //TODO: QUITAR EL PARCEABLE POR GETPARCEABLEEXTRA
            recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }

        //Comprobar si hay parametros en caso de llamada desde el planificador
        val parametros = this.intent.extras
        if (parametros != null) {
            Log.d("asf", "paso por aqui")
            titulo.text = intent.getStringExtra("titulo")
            listaPictogramas = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!
            adaptador = AdaptadorPresentacion(listaPictogramas, this)
            recyclerView.adapter = adaptador
            lblMensaje.visibility = View.INVISIBLE
            iconoDeshacer.visibility = View.VISIBLE
            iconoEscuchar.visibility = View.VISIBLE
            iconoReproducir.visibility = View.VISIBLE
            iconoReproducirLento.visibility = View.VISIBLE
            iconoReproducirRapido.visibility = View.VISIBLE
        } else {
            mostrarPlan()
        }

        //Este método se ejecutará al seleccionar el icono cuaderno para acceder
        card_cuaderno.setOnClickListener {
            val intent = Intent(applicationContext, CuadernoActivity::class.java)
            startActivity(intent)
        }

        card_actividades.setOnClickListener{
            val intent = Intent(applicationContext, ActividadActivity::class.java)
            startActivity(intent)
        }

        //Este método se ejecutará al seleccionar el icono deshacer para volver un paso atrás en el seguimiento
        iconoDeshacer.setOnClickListener {
            if (!pasosCompletados.empty()) {
                val posicionUndo = pasosCompletados.pop() as Int
                val viewHolderPictogramas =
                    recyclerView.findViewHolderForAdapterPosition(posicionUndo) as AdaptadorPresentacion.ViewHolderPictogramas?
                viewHolderPictogramas!!.itemView.findViewById<View>(R.id.id_Imagen).alpha = 1f
                viewHolderPictogramas.itemView.findViewById<View>(R.id.id_Texto).alpha = 1f
                viewHolderPictogramas.itemView.findViewById<View>(R.id.btn_historiaPictoOn).alpha = 1f
                if(listaPictogramas[posicionUndo].categoria == 9){
                    viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card).setBackgroundResource(R.drawable.card_premio)
                }else if(listaPictogramas[posicionUndo].categoria == 8){
                    viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card).setBackgroundResource(R.drawable.card_espera)
                }else{
                    viewHolderPictogramas.itemView.findViewById<View>(R.id.id_card).setBackgroundResource(R.drawable.card_personalizado)
                }
                viewHolderPictogramas.popListClicked()
                if(pasosCompletados.isEmpty()){
                    iconoDeshacer.isEnabled = false
                }
            }
        }
        // create an object textToSpeech and adding features into it
        val textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TextToSpeech is ready to use
            } else {
                // Handle initialization failure if needed
            }
        }
        val delayedSpeechRunnables = mutableListOf<Runnable>()
        var speechInProgress = false

        iconoEscuchar.setOnClickListener{
            if (!speechInProgress) {
                iconoEscuchar.text = "Parar"
                val delayBetweenSpeech = 2000
                listaPictogramas.forEachIndexed { index, pictograma ->
                    val runnable = Runnable {
                        textToSpeech.speak(pictograma.titulo, TextToSpeech.QUEUE_FLUSH, null, null)
                    }

                    delayedSpeechRunnables.add(runnable)
                    handler.postDelayed(runnable, index * delayBetweenSpeech.toLong())
                }
                speechInProgress = true
            } else {
                iconoEscuchar.text = "Escuchar"
                handler.removeCallbacksAndMessages(null)
                delayedSpeechRunnables.clear()
                speechInProgress = false
            }
        }

        iconoReproducir.setOnClickListener{
            reproduccionLenta = false
            reproduccionRapida = false
            reproducirEvento(4000L)
        }

        iconoReproducirLento.setOnClickListener{
            reproduccionLenta = true
            reproduccionRapida = false
            reproducirEvento(7000L)
        }

        iconoReproducirRapido.setOnClickListener{
            reproduccionRapida = true
            reproduccionLenta = false
            reproducirEvento(2500L)
        }
    }

    fun reproducirEvento(tiempo: Long) {
        if (currentDialog != null) {
            currentDialog?.dismiss()
            currentDialog = null
        }

        currentPosition = 0
        isRunning = true

        currentRunnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    reproductor = true
                    onItemSeleccionado(currentPosition)
                    currentPosition++

                    if (currentPosition < listaPictogramas.size) {
                        handler.postDelayed(this, tiempo)
                    } else {
                        stopReproductor()
                    }
                }
            }
        }

        handler.post(currentRunnable!!)
    }

    fun stopReproductor() {
        Log.d("asf", "PASO POR AQUI")
        isRunning = false
        reproductor = false
        currentRunnable?.let {
            handler.removeCallbacksAndMessages(null)
            currentRunnable = null}
        currentRunnable = null
        currentPosition = 0
        currentDialog?.dismiss()
        currentDialog = null
    }

    fun mostrarPlan() {
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val idUsuario = prefs.getString("idUsuario", "")
        //Mostrar la planificación a seguir para el niño
        listaPictogramas = ArrayList()
        listaPictogramas = idUsuario?.let { plan.mostrarPlanificacion(it, selectedDate, this) } as ArrayList<Pictograma>
        //Mostrar título de la planificación
        tituloObtenido = plan.obtenerTituloPlan(idUsuario, selectedDate, this)
        titulo.text = tituloObtenido

        adaptador = AdaptadorPresentacion(listaPictogramas, this)
        recyclerView.adapter = adaptador
        //Mostrar mensaje si no hay plan
        if (listaPictogramas.isEmpty()) {
            lblMensaje.visibility = View.VISIBLE
            iconoDeshacer.visibility = View.INVISIBLE
            iconoEscuchar.visibility = View.INVISIBLE
            iconoReproducir.visibility = View.INVISIBLE
            iconoReproducirLento.visibility = View.INVISIBLE
            iconoReproducirRapido.visibility = View.INVISIBLE
        } else {
            lblMensaje.visibility = View.INVISIBLE
            iconoDeshacer.visibility = View.VISIBLE
            iconoEscuchar.visibility = View.VISIBLE
            iconoReproducir.visibility = View.VISIBLE
            iconoReproducirLento.visibility = View.VISIBLE
            iconoReproducirRapido.visibility = View.VISIBLE


        }
    }

    override fun onItemSeleccionado(posicion: Int) {
        if (currentDialog != null && currentDialog!!.isShowing) {
            currentDialog!!.dismiss()
        }
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dialogo_presentacion)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btn_cerrar = dialog!!.findViewById(R.id.icono_CerrarDialogoEvento)
        val pictograma = dialog!!.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog!!.findViewById<TextView>(R.id.lbl_pictograma)
        val historia = dialog!!.findViewById<ConstraintLayout>(R.id.Bubble)
        imagenConfeti = dialog!!.findViewById(R.id.img_confeti)
        mensajePremio = dialog!!.findViewById(R.id.txt_premio)
        val animFondo = AnimationUtils.loadAnimation(applicationContext, R.anim.confeti)
        val animCard = AnimationUtils.loadAnimation(applicationContext, R.anim.card)

        //Si es recompensa mostramos el dialogo diferente
        if (listaPictogramas[posicion].categoria == 9) {
            imagenConfeti.visibility = View.VISIBLE
            mensajePremio.visibility = View.VISIBLE
            imagenConfeti.animation = animFondo
            card.animation = animCard
            mensajePremio.animation = animFondo
        } else if (listaPictogramas[posicion].categoria == 8) {
            imagenConfeti.visibility = View.VISIBLE
            mensajePremio.visibility = View.VISIBLE
            imagenConfeti.setImageResource(R.drawable.svg_espera)
            mensajePremio.text = "¡Mientras esperamos!"
            imagenConfeti.animation = animCard
            card.animation = animCard
            mensajePremio.animation = animFondo
        } else {
            imagenConfeti.visibility = View.INVISIBLE
            mensajePremio.visibility = View.INVISIBLE
        }

        pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
        tituloPictograma.text = listaPictogramas[posicion].titulo
        val textoHistoria = dialog!!.findViewById<TextView>(R.id.lblBubble)
        val avatarHistoria = dialog!!.findViewById<ShapeableImageView>(R.id.avatarBubble)

        if (listaPictogramas[posicion].historia != null) {
            textoHistoria.text = listaPictogramas[posicion].historia
            historia.visibility = View.VISIBLE
        } else {
            historia.visibility = View.GONE
        }


        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        if (prefs.getString("imagenPlanificador", "") === "") {
            avatarHistoria.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
        } else {
            avatarHistoria.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
        }

        //Añade a la pila el paso completado
        if (!reproductor){
            pasosCompletados.push(posicion)
            if(posicion == 0){
                iconoDeshacer.isEnabled = true
            }
            card = dialog!!.findViewById(R.id.card_presentacion)
            currentDialog = dialog

            //Botón cerrar
            btn_cerrar.setOnClickListener { dialog!!.dismiss() }
            dialog!!.show()

            val orientation = resources.configuration.orientation
            val layoutParams = historia.layoutParams as ConstraintLayout.LayoutParams
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutParams.width = 350.dpToPx(this)
                historia.layoutParams = layoutParams
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutParams.width = 250.dpToPx(this)
                historia.layoutParams = layoutParams
            }
            
        }else{
            card = dialog!!.findViewById(R.id.card_presentacion)
            dialogo_presentacion = dialog!!.findViewById(R.id.dialogo_presentacion_reproductor)

            var showAnimation: Animation
            var delayBeforeDismiss : Long
            val dismissAnimation = AnimationUtils.makeOutAnimation(this, false)

            if(reproduccionLenta){
                showAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_side_slow)
                delayBeforeDismiss = 3000L
                dismissAnimation.duration = 1000L
            }else if(reproduccionRapida){
                showAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_side_fast)
                delayBeforeDismiss = 1000L
                dismissAnimation.duration = 500L
            }else{
                showAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_from_side)
                delayBeforeDismiss = 2000L
                dismissAnimation.duration = 500L
            }

            dialogo_presentacion.visibility = View.VISIBLE
            dialogo_presentacion.startAnimation(showAnimation)

            val totalDuration = showAnimation.duration + delayBeforeDismiss + dismissAnimation.duration

            //Comienza la animación de esconder el pictograma
            handler.postDelayed({
                dialogo_presentacion.clearAnimation()
                dialogo_presentacion.startAnimation(dismissAnimation)
            }, showAnimation.duration + delayBeforeDismiss)

            //Cuando se ha terminado la animación pone el pictograma invisible
            if(posicion != listaPictogramas.size-1){
                handler.postDelayed({
                    dialogo_presentacion.visibility = View.GONE
                }, totalDuration)
            }

            currentDialog = dialog

            //Botón cerrar
            btn_cerrar.setOnClickListener {
                dialog!!.dismiss()
                stopReproductor()
            }
        }
        dialog!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ayuda -> {
                val i = Intent(applicationContext, ManualActivity::class.java)
                startActivity(i)
            }
            R.id.item_perfil -> {
                val popupMenu = PopupMenu(this@PlanActivity, findViewById(R.id.item_ayuda) )
                popupMenu.inflate(R.menu.popup_menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.option_1 -> {
                            val perfil = Intent(applicationContext, ConfiguracionActivity::class.java)
                            startActivity(perfil)
                            true
                        }
                        R.id.option_3 -> {
                            val dialogLogout = Dialog(this)
                            dialogLogout.setContentView(R.layout.dialogo_logout)
                            dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                            icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                            btn_logout.setOnClickListener {
                                val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                                prefs.edit().clear().commit()
                                // val editor = prefs.edit()
                                // editor.putBoolean("userAccount", false)
                                // editor.apply()
                                val login = Intent(applicationContext, PreLoginActivity::class.java)
                                startActivity(login)
                            }
                            icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                            dialogLogout.show()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
            android.R.id.home -> finish()
        }
        return true
    }
}
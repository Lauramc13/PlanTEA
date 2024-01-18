package com.example.plantea.presentacion.actividades.ninio

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacionesFuturas
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.example.plantea.presentacion.fragmentos.DialogCalendarFragment
import com.example.plantea.presentacion.viewModels.PlanViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import java.util.Stack



class PlanActivity : AppCompatActivity(), CommonUtils.TextToSpeechListener, AdaptadorPresentacion.OnItemSelectedListener {
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
    private lateinit var adaptador: AdaptadorPresentacion
    private lateinit var dia: TextView

    private var viewHolderPicto: AdaptadorPresentacion.ViewHolderPictogramas? = null

    private lateinit var imagenConfeti: ImageView
    private lateinit var mensajePremio: TextView

    private lateinit var planificacionesFuturas: RecyclerView

    private lateinit var calendarButton: Button

    private lateinit var recyclerView: RecyclerView

    private var dialog: Dialog? = null
    private var dialogCalendar: DialogCalendarFragment? = null

    lateinit var historia: ConstraintLayout

    val viewModel: PlanViewModel by viewModels()

    override fun onStop() {
        super.onStop()
        viewModel.isRunning = false
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the RecyclerView state before the activity is destroyed
        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
       // outState.putParcelable("recycler_view_state", recyclerViewState)
        outState.putString("FECHA_SELECCIONADA", viewModel.fechaSeleccionada.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val fechaSel = savedInstanceState.getString("FECHA_SELECCIONADA")
        //val recyclerView2 = savedInstanceState.getString("recycler_view_state")

        if (!fechaSel.isNullOrEmpty()) {
            viewModel.diaSeleccionado(this, LocalDate.parse(fechaSel))
        }
       // mostrarPlan()
       // cambiarPicto(1)
    }

*/

    override fun onDestroy() {
        super.onDestroy()
        viewModel._dismissDialog.postValue(true)
        viewModel._fechaActual.removeObservers(this)
        viewModel._diasMes.removeObservers(this)
        viewModel._dismissDialog.removeObservers(this)
        viewModel.viewHolderPictogramas = viewHolderPicto
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)

        // Si se va hacia atras y no hay nada en la cola, se redirige a MainActivity
        val callback = viewModel.backCallBack(this)
        onBackPressedDispatcher.addCallback(this, callback)

        //Pila de los pasos completados en el seguimiento de un plan
        //viewModel.pasosCompletados = Stack<Int>()
        iconoDeshacer = findViewById(R.id.icon_deshacer)
        iconoDeshacerTodas = findViewById(R.id.icon_deshacerTodas)
        iconoMarcar = findViewById(R.id.icon_marcar)
        iconoMarcarTodas = findViewById(R.id.icon_marcarTodas)
        iconoEscuchar = findViewById(R.id.icon_escuchar)
        iconoReproducir = findViewById(R.id.icon_reproducir)
        calendarButton = findViewById(R.id.CalendarDate)
        buttonPlanNuevo = findViewById(R.id.crearPlan)
        titulo = findViewById(R.id.lbl_titulo)
        lblMensaje = findViewById(R.id.lbl_mensajeNinio)
        recyclerView = findViewById(R.id.recycler_plan)
        planificacionesFuturas = findViewById(R.id.planificacionRecyclerView)
        dia = findViewById(R.id.lbl_dia)

        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        viewModel.configureUser(prefs, this)

        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManagerLinear

        var speechInProgress = false
        CommonUtils.initializeTextToSpeech(this)
        CommonUtils.listener = this

        val notificationList : ArrayList<PlanificacionItem> = viewModel.mostrarPlanificaciones()
        planificacionesFuturas.layoutManager = LinearLayoutManager(this)
        val adaptadorNot = AdaptadorPlanificacionesFuturas(notificationList, viewModel)
        planificacionesFuturas.adapter = adaptadorNot

        viewModel.selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(viewModel.calendar.time)
        dia.text = getString(R.string.formatted_date, viewModel.dayOfWeek.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, viewModel.dayOfMonth, viewModel.month)

        viewModel.initializeAnimations(applicationContext)

        //Comprobar si hay parametros en caso de llamada desde el planificador
        if (this.intent.extras != null) {
            configureParameters()
        } else {
            if(savedInstanceState == null){
                viewModel.mostrarPlan(this)
            }else{
                viewModel.configureDataEvento() //Tiene que haber una mejor forma de hacer esto
                recyclerView.adapter = viewModel.adaptador
                for (i in 0 until viewModel.pasosCompletados.size){
                    viewModel.viewHolderPictogramas?.itemView?.findViewById<View>(R.id.id_card)?.setBackgroundResource(R.drawable.card_disabled)
                    viewModel.viewHolderPictogramas!!.itemView.findViewById<View>(R.id.id_card_picto).alpha = 0.7f
                }

                if(dialogCalendar?.isVisible == true){
                    dialogCalendar!!.notificarCambio()
                }
            }
        }

        //--------------- FUNCIONALIDADES DE LOS BOTONES ---------------

        calendarButton.setOnClickListener {
            dialogCalendar = DialogCalendarFragment()
            dialogCalendar!!.viewModel = viewModel
            dialogCalendar!!.show(supportFragmentManager, "CalendarDialogFragment")
        }

        buttonPlanNuevo.setOnClickListener {
            Toast.makeText(this, "Crear plan", Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, CalendarioActivity::class.java))
        }

        //Este método se ejecutará al seleccionar el icono deshacer para volver un paso atrás en el seguimiento
        iconoDeshacer.setOnClickListener {
            if (!viewModel.pasosCompletados.empty()) {
                cambiarPicto(viewModel.pasosCompletados.pop() as Int)

                if (viewModel.pasosCompletados.isEmpty()) {
                    iconoDeshacer.isEnabled = false
                    iconoDeshacerTodas.isEnabled = false
                }

                iconoMarcarTodas.isEnabled = true
                iconoMarcar.isEnabled = true
            }
        }

        //Este método se ejecutará al seleccionar el icono deshacer para marcar todos los pictogramas como no realizados
        iconoDeshacerTodas.setOnClickListener {
            if (!viewModel.pasosCompletados.empty()) {
                for(i in 0 until viewModel.pasosCompletados.size){
                    cambiarPicto(viewModel.pasosCompletados.pop() as Int)
                }

                canGoBack(false)
            }
        }

        //Este método se ejecutará al seleccionar el icono marcar para marcar el pictograma actual como realizado
        iconoMarcar.setOnClickListener {

            if (!viewModel.pasosCompletados.empty()) {
                val posicion = viewModel.pasosCompletados.peek() as Int
                cambiarPictoClickedNormal(posicion+1)
                viewModel.pasosCompletados.add(posicion+1)
            }else{
                cambiarPictoClickedNormal(0)
                viewModel.pasosCompletados.add(0)
            }

            iconoDeshacerTodas.isEnabled = true
            iconoDeshacer.isEnabled = true

            if(viewModel.pasosCompletados.size == viewModel.listaPictogramas.size){
                iconoMarcarTodas.isEnabled = false
                iconoMarcar.isEnabled = false
            }
        }

        // Este método se ejecutará al seleccionar el icono marcar para marcar todos los pictogramas como realizados
        iconoMarcarTodas.setOnClickListener {
            for (i in 0 until viewModel.listaPictogramas.size){
                cambiarPictoClickedNormal(i)
                viewModel.pasosCompletados.add(i)
            }
            canGoBack(true)
        }

        iconoEscuchar.setOnClickListener {
            if (!speechInProgress) {
                iconoEscuchar.text = getString(R.string.str_parar)
                CommonUtils.textToSpeechOn(viewModel.listaPictogramas)
                speechInProgress = true
            } else {
                iconoEscuchar.text = getString(R.string.str_escuchar)
                CommonUtils.textToSpeech.stop()
                speechInProgress = false
            }
        }

        iconoReproducir.setOnClickListener {
            reproducirEvento(1500L)
        }

        observe(savedInstanceState)

    }

    private fun observe(savedInstanceState : Bundle?){
        viewModel._diaText.observe(this) { dia.text = it }
        viewModel._tituloLiveData.observe(this) { titulo.text = it }

        viewModel._planLiveData.observe(this){
            val isPlanificador = prefs.getBoolean("PlanificadorLogged", false)

            if(it.isEmpty()){
                lblMensaje.visibility = View.VISIBLE
                iconoDeshacer.visibility = View.INVISIBLE
                iconoDeshacerTodas.visibility = View.INVISIBLE
                iconoMarcar.visibility = View.INVISIBLE
                iconoMarcarTodas.visibility = View.INVISIBLE
                iconoEscuchar.visibility = View.INVISIBLE
                iconoReproducir.visibility = View.INVISIBLE
                if(isPlanificador) {
                    buttonPlanNuevo.visibility = View.VISIBLE
                }
            }else{
                lblMensaje.visibility = View.INVISIBLE
                buttonPlanNuevo.visibility = View.INVISIBLE
                iconoDeshacer.visibility = View.VISIBLE
                iconoDeshacerTodas.visibility = View.VISIBLE
                iconoMarcar.visibility = View.VISIBLE
                iconoMarcarTodas.visibility = View.VISIBLE
                iconoEscuchar.visibility = View.VISIBLE
                iconoReproducir.visibility = View.VISIBLE
            }

            viewModel.adaptador = AdaptadorPresentacion(it, this)
            recyclerView.adapter = viewModel.adaptador

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

    }

    private fun configureParameters(){
        titulo.text = intent.getStringExtra("titulo")
        viewModel.listaPictogramas = (intent.getSerializableExtra("pictogramas") as ArrayList<Pictograma>?)!!
        adaptador = AdaptadorPresentacion(viewModel.listaPictogramas, this)
        recyclerView.adapter = adaptador
        lblMensaje.visibility = View.INVISIBLE
        buttonPlanNuevo.visibility = View.INVISIBLE
        iconoDeshacer.visibility = View.VISIBLE
        iconoDeshacerTodas.visibility = View.VISIBLE
        iconoMarcar.visibility = View.VISIBLE
        iconoMarcarTodas.visibility = View.VISIBLE
        iconoEscuchar.visibility = View.VISIBLE
        iconoReproducir.visibility = View.VISIBLE
    }


    override fun onItemSeleccionado(context: Context, posicion: Int) {
        if (viewModel.currentDialog != null && viewModel.currentDialog!!.isShowing) {
            viewModel.currentDialog!!.dismiss()
        }

        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.dialogo_presentacion)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val  btnCerrar = dialog!!.findViewById<ImageView>(R.id.icono_CerrarDialogoEvento)
        val pictograma = dialog!!.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog!!.findViewById<TextView>(R.id.lbl_pictograma)
        val historia = dialog!!.findViewById<ConstraintLayout>(R.id.Bubble)
         imagenConfeti = dialog!!.findViewById(R.id.img_confeti)
         mensajePremio = dialog!!.findViewById(R.id.txt_premio)
        val  dialogoPresentacion = dialog!!.findViewById<ConstraintLayout>(R.id.dialogo_presentacion_2)
        pictograma.setImageURI(Uri.parse(viewModel.listaPictogramas[posicion].imagen))
        tituloPictograma.text = viewModel.listaPictogramas[posicion].titulo

        dialogoPresentacion.clearAnimation()
           imagenConfeti.clearAnimation()
           mensajePremio.clearAnimation()
           imagenConfeti.visibility = View.INVISIBLE
           mensajePremio.visibility = View.INVISIBLE

        val textoHistoria = dialog!!.findViewById<TextView>(R.id.lblBubble)
        val avatarHistoria = dialog!!.findViewById<ShapeableImageView>(R.id.avatarBubble)

        // Si tenemos historias
        if (viewModel.listaPictogramas[posicion].historia != "null") {
            textoHistoria.text = viewModel.listaPictogramas[posicion].historia
            historia.visibility = View.VISIBLE
            if (prefs.getString("imagenPlanificador", "") === "") {
                avatarHistoria.setBackgroundResource(R.drawable.ic_baseline_add_photo_alternate_128)
            } else {
                avatarHistoria.setImageURI(Uri.parse(prefs.getString("imagenPlanificador", "")))
            }
        } else {
            historia.visibility = View.GONE
        }

        if (viewModel.listaPictogramas[posicion].categoria == 9 || viewModel.listaPictogramas[posicion].categoria == 8) {
            animacionesConfeti(this, viewModel.listaPictogramas[posicion].categoria)
        }

        viewModel.pasosCompletados.push(posicion)
        if (posicion == 0) {
            iconoDeshacer.isEnabled = true
            iconoDeshacerTodas.isEnabled = true
        }
        viewModel.currentDialog = dialog

        //Botón cerrar
        btnCerrar.setOnClickListener { dialog!!.dismiss() }
        dialog!!.show()
    }

    override fun checkPosition(posicion: Int): Boolean {
        return if(viewModel.pasosCompletados.isEmpty() ){
            posicion == 0
        }else{
            viewModel.pasosCompletados.peek() + 1 == posicion
        }
    }

    private fun animacionesConfeti(context: Context, categoria: Int) {
        imagenConfeti.visibility = View.VISIBLE
        mensajePremio.visibility = View.VISIBLE

        imagenConfeti.clearAnimation()
        mensajePremio.clearAnimation()

        if (categoria == 9) {
             imagenConfeti.animation = viewModel.animFondo
             viewModel.animFondo.start()
             mensajePremio.animation = viewModel.animFondo
            viewModel.animFondo.start()
        } else if (categoria == 8) {
             imagenConfeti.setImageResource(R.drawable.svg_espera)
             mensajePremio.text = context.getString(R.string.str_esperar)
             imagenConfeti.animation = viewModel.animCard
             viewModel.animCard.start()
             mensajePremio.animation = viewModel.animCard
             viewModel.animCard.start()
        }
    }

    private fun reproducirEvento(tiempo: Long) {
        val screenWidthInDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val targetHeight: Float
        val targetWidth: Float
        if (screenWidthInDp < 800) {
            //Es (129, 164) y no (115,150) ya que le sumamos el margen
            targetHeight = viewModel.dpToPx(129, this).toFloat()
            targetWidth = viewModel.dpToPx(164, this).toFloat()
        }else{
            targetHeight = viewModel.dpToPx(170, this).toFloat()
            targetWidth = viewModel.dpToPx(200, this).toFloat()
        }

        if(viewModel.isRunning){
            animationReproduccion(targetHeight, targetWidth, viewModel.currentPosition, 1f)
            iconoReproducir.setIconResource(R.drawable.svg_play)
            canGoBack(true)
            iconoDeshacerTodas.performClick()
            viewModel.stopReproductor()
        }else{
            iconoReproducir.setIconResource(R.drawable.svg_stop)
            iconoDeshacerTodas.performClick()
            iconoMarcar.isEnabled = false
            iconoDeshacer.isEnabled = false
            iconoMarcarTodas.isEnabled = false
            iconoDeshacerTodas.isEnabled = false

            viewModel.currentPosition = 0
            viewModel.isRunning = true

            viewModel.currentRunnable = object : Runnable {
                override fun run() {
                    if (viewModel.isRunning) {
                        //onItemSeleccionado(currentPosition)
                        if (viewModel.currentPosition < viewModel.listaPictogramas.size) {
                            viewModel.handler.postDelayed(this, tiempo)
                            cambiarPictoClicked(viewModel.currentPosition)
                            viewModel.pasosCompletados.add(viewModel.currentPosition)
                        } else {
                            lastPictoClicked(viewModel.currentPosition)
                            viewModel.stopReproductor()
                            canGoBack(true)
                            iconoReproducir.setIconResource(R.drawable.svg_play)
                        }
                        viewModel.currentPosition++
                    }
                }
            }

            viewModel.handler.post(viewModel.currentRunnable!!)
        }
    }

    private fun cambiarPicto(posicion: Int){
        viewHolderPicto = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas
        viewHolderPicto!!.itemView.findViewById<View>(R.id.id_card_picto).animate().alpha(1f).setDuration(100).start()

        when (viewModel.listaPictogramas[posicion].categoria) {
            9 -> {
                viewHolderPicto!!.itemView.findViewById<View>(R.id.id_card)
                    .setBackgroundResource(R.drawable.card_premio)
            }
            8 -> {
                viewHolderPicto!!.itemView.findViewById<View>(R.id.id_card)
                    .setBackgroundResource(R.drawable.card_espera)
            }
            else -> {
                viewHolderPicto!!.itemView.findViewById<View>(R.id.id_card)
                    .setBackgroundResource(R.drawable.card_personalizado)
            }
        }

    }

    private fun cambiarPictoClicked(posicion: Int) {
        viewHolderPicto = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas?
        viewHolderPicto?.itemView?.findViewById<View>(R.id.id_card)?.setBackgroundResource(R.drawable.card_pronunced)

        val targetHeight : Float
        val targetWidth : Float

        if (CommonUtils.isMobile(this)) {
            targetHeight = viewModel.dpToPx(145, this).toFloat()
            targetWidth = viewModel.dpToPx(180, this).toFloat()
        }else{
            targetHeight = viewModel.dpToPx(185, this).toFloat()
            targetWidth = viewModel.dpToPx(220, this).toFloat()
        }

        animationReproduccion(targetHeight, targetWidth, posicion, 1f)

        if(posicion !=0){
            lastPictoClicked(posicion)
        }
    }

    private fun cambiarPictoClickedNormal(posicion: Int){
        viewHolderPicto = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas?
        viewHolderPicto?.itemView?.findViewById<View>(R.id.id_card)?.setBackgroundResource(R.drawable.card_disabled)
        viewHolderPicto!!.itemView.findViewById<View>(R.id.id_card_picto).alpha = 0.7f
    }

    private fun lastPictoClicked(posicion: Int){
        viewHolderPicto = recyclerView.findViewHolderForAdapterPosition(posicion-1) as AdaptadorPresentacion.ViewHolderPictogramas?
        viewHolderPicto?.itemView?.findViewById<View>(R.id.id_card)?.setBackgroundResource(R.drawable.card_disabled)

        val targetHeight : Float
        val targetWidth : Float

        if (CommonUtils.isMobile(this)) {
            targetHeight = viewModel.dpToPx(129, this).toFloat()
            targetWidth = viewModel.dpToPx(164, this).toFloat()
        }else{
            targetHeight = viewModel.dpToPx(170, this).toFloat()
            targetWidth = viewModel.dpToPx(200, this).toFloat()
        }
        animationReproduccion(targetHeight, targetWidth, posicion-1, 0.7f)
    }


    private fun animationReproduccion(targetHeight: Float, targetWidth: Float, posicion: Int, alpha: Float){
        viewHolderPicto = recyclerView.findViewHolderForAdapterPosition(posicion) as AdaptadorPresentacion.ViewHolderPictogramas?

        viewHolderPicto?.itemView?.findViewById<View>(R.id.id_card_picto)
            ?.animate()
            ?.setDuration(250)
            ?.alpha(alpha)
            ?.scaleX(targetWidth / viewHolderPicto!!.itemView.width)
            ?.scaleY(targetHeight / viewHolderPicto!!.itemView.height)
            ?.withEndAction {
                viewHolderPicto!!.itemView.findViewById<View>(R.id.id_card_picto)
                    ?.layoutParams?.height = targetHeight.toInt()
                viewHolderPicto!!.itemView.findViewById<View>(R.id.id_card_picto)
                    ?.layoutParams?.width = targetWidth.toInt()
            }

        if(posicion !=0){
            lastPictoClicked(posicion)
        }

    }

    override fun onSpeechDone() {
        iconoEscuchar.text = getString(R.string.str_escuchar)
    }

    fun canGoBack(value: Boolean) {
        iconoDeshacer.isEnabled = value
        iconoDeshacerTodas.isEnabled = value
        iconoMarcar.isEnabled = !value
        iconoMarcarTodas.isEnabled = !value
    }


}

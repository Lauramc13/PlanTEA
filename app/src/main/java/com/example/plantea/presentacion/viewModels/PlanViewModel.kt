package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.dominio.PlanificacionItem
import com.example.plantea.presentacion.actividades.MainActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacionesFuturas
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.Stack


class PlanViewModel: ViewModel(), AdaptadorCalendario.OnItemSelectedListener, AdaptadorPlanificacionesFuturas.OnItemSelectedListener, AdaptadorPresentacion.OnItemSelectedListener {
    var fechaSeleccionada : LocalDate = LocalDate.now()
    var _diaText = MutableLiveData<String>()
    var tituloPlan = String()
    val _fechaActual = MutableLiveData<String>()
    val _diasMes = MutableLiveData<ArrayList<LocalDate?>>()
    val _planLiveData: MutableLiveData<ArrayList<Pictograma>?> = MutableLiveData()
    val _noEvents = SingleLiveEvent<Boolean>()
    var _pasosCompletados = SingleLiveEvent<Stack<Int>?>()

    var speechInProgress = false

    lateinit var recyclerView: RecyclerView

    var listaPictogramas = ArrayList<Pictograma>()
    var plan = Planificacion()
    lateinit var adaptador: AdaptadorPresentacion
    var currentDialog: Dialog? = null

    var dialog: Dialog? = null

    private lateinit var animFondo: Animation
    private lateinit var animCard: Animation

    var idUsuario = ""

    var isRunning = false
    var currentRunnable: Runnable? = null
    var currentPosition: Int = 0
    val handler = Handler()

    private val calendar: Calendar = Calendar.getInstance()
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    private val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    private val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    val dayOfWeek: String = dateFormat.format(calendar.time)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val month: String = monthFormat.format(calendar.time)

    var planificaciones = ArrayList<Evento>()
    var evento = Evento()
    lateinit var eventos: ArrayList<Evento>

    private lateinit var imagenConfeti: ImageView
    private lateinit var mensajePremio: TextView

    override fun diaSeleccionado(context: Context?, fecha: LocalDate, position: Int, selectedDay: Int) {
        selectedDate(context, fecha)
    }

    override fun diaSeleccionado(context: Context?, fecha: LocalDate) {
        selectedDate(context, fecha)
    }

    private fun selectedDate(context: Context?, fecha: LocalDate){
        fechaSeleccionada = fecha
        CalendarioUtilidades.fechaSeleccionada = fecha
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormatter.format(fecha)

        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        val dayOfWeek = dayOfWeekFormatter.format(fecha).replaceFirstChar{ if (it.isLowerCase()) it.titlecase() else it.toString() }
        val dayOfMonth = fecha.dayOfMonth.toString()
        val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
        val month = monthFormatter.format(fecha)

        if (context != null) {
            _diaText.value =  context.getString(R.string.formatted_date, dayOfWeek, dayOfMonth, month)
        }

        //_dismissDialog.value = true
        mostrarPlan(context)
        dialog?.dismiss()
    }

    fun mostrarPlan(context: Context?) {
        _pasosCompletados.value = Stack()
        //check if there is a plan for the selected date with visibility 1
        val check = evento.checkEventosDia(idUsuario, selectedDate, context)

        if(check > -1){
            //existe un evento visible para este dia
            Toast.makeText(context, "Evento encontrado id_plan: $check", Toast.LENGTH_SHORT).show()
            evento = evento.obtenerEventoPlan(idUsuario, selectedDate, context)
            listaPictogramas = idUsuario.let { plan.mostrarPlanificacion(it, evento.id.toString(), context) } as ArrayList<Pictograma>
            tituloPlan = evento.nombre.toString()
            _planLiveData.value = listaPictogramas
        }else{
            _noEvents.value = true
        }
    }

    fun configureDataEvento(context: Context?){
        val check = evento.checkEventosDia(idUsuario, selectedDate, context as Activity)

        if(check > -1){
            _planLiveData.value = _planLiveData.value
            _pasosCompletados.value = _pasosCompletados.value
        }else{
            _noEvents.value = true

        }
    }

    fun mostrarPlanificaciones(): ArrayList<PlanificacionItem> {
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

    fun stopReproductor() {
        currentRunnable?.let {
            handler.removeCallbacksAndMessages(null)
            currentRunnable = null
        }
        currentRunnable = null
        currentPosition = 0
    }

    fun backCallBack(context: Context): OnBackPressedCallback {
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                if ((context as AppCompatActivity).supportFragmentManager.backStackEntryCount == 0) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                } else {
                    (context).supportFragmentManager.popBackStack()
                }
                context.finish()
            }
        }
        return callback
    }

    fun obtenerVistaMes() {
        _fechaActual.value = CalendarioUtilidades.formatoMesAnio(CalendarioUtilidades.fechaSeleccionada).uppercase(Locale.getDefault())
        _diasMes.value = CalendarioUtilidades.obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
    }

    fun configureUser(prefs : SharedPreferences, context: Context){
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
        planificaciones = userId?.let { evento.obtenerTodosEventos(it, context as Activity) } as ArrayList<Evento>
    }

    fun initializeAnimations(applicationContext: Context) {
        animFondo = AnimationUtils.loadAnimation(applicationContext, R.anim.confeti)
        animCard = AnimationUtils.loadAnimation(applicationContext, R.anim.card)
    }

    /*fun obtenerEventosFecha(context: Context){
        eventos = idUsuario.let { evento.obtenerEventos(it, context as Activity, CalendarioUtilidades.fechaSeleccionada) } as ArrayList<Evento>
    }*/

    override fun onItemSeleccionado(context: Context, posicion: Int) {
        if (currentDialog != null && currentDialog!!.isShowing) {
            currentDialog!!.dismiss()
        }

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialogo_presentacion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val  btnCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogoEvento)
        val pictograma = dialog.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        val historia = dialog.findViewById<ConstraintLayout>(R.id.Bubble)
        imagenConfeti = dialog.findViewById(R.id.img_confeti)
        mensajePremio = dialog.findViewById(R.id.txt_premio)
        val  dialogoPresentacion = dialog.findViewById<ConstraintLayout>(R.id.dialogo_presentacion_2)
        val identifier = context.resources.getIdentifier(listaPictogramas[posicion].imagen, "drawable", context.packageName)
        if(identifier == 0) {
            pictograma.setImageURI(Uri.parse(listaPictogramas[posicion].imagen))
        }else{
            pictograma.setImageResource(identifier)
        }
        tituloPictograma.text = listaPictogramas[posicion].titulo

        dialogoPresentacion.clearAnimation()
        imagenConfeti.clearAnimation()
        mensajePremio.clearAnimation()
        imagenConfeti.visibility = View.INVISIBLE
        mensajePremio.visibility = View.INVISIBLE

        val textoHistoria = dialog.findViewById<TextView>(R.id.lblBubble)
        val avatarHistoria = dialog.findViewById<ShapeableImageView>(R.id.avatarBubble)

        val prefs: SharedPreferences = context.getSharedPreferences("Preferencias", MODE_PRIVATE)

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
            animacionesConfeti(context, listaPictogramas[posicion].categoria)
        }

        _pasosCompletados.value?.push(posicion)
        _pasosCompletados.postValue(_pasosCompletados.value)

        currentDialog = dialog

        //Botón cerrar
        btnCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun checkPosition(posicion: Int): Boolean {
        return if(_pasosCompletados.value?.isEmpty()!!){
            posicion == 0
        }else{
            _pasosCompletados.value?.peek()!! + 1 == posicion
        }
    }

    private fun animacionesConfeti(context: Context, categoria: Int) {
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
            mensajePremio.text = context.getString(R.string.str_esperar)
            imagenConfeti.animation = animCard
            animCard.start()
            mensajePremio.animation = animCard
            animCard.start()
        }
    }
}
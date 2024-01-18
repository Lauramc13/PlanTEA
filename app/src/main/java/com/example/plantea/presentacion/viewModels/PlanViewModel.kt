package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import org.checkerframework.checker.units.qual.A
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.Stack

class PlanViewModel: ViewModel(), AdaptadorCalendario.OnItemSelectedListener, AdaptadorPlanificacionesFuturas.OnItemSelectedListener {

    var fechaSeleccionada : LocalDate = LocalDate.now()
    lateinit var selectedDate: String
    var _diaText = MutableLiveData<String>()
    val _planLiveData: MutableLiveData<ArrayList<Pictograma>> = MutableLiveData()
    val _tituloLiveData: MutableLiveData<String> = MutableLiveData()
    val _fechaActual = MutableLiveData<String>()
    val _diasMes = MutableLiveData<ArrayList<LocalDate?>>()
    val _imageAvatar = MutableLiveData<Uri>()
    val _dismissDialog = MutableLiveData<Boolean>()

    var viewHolderPictogramas : AdaptadorPresentacion.ViewHolderPictogramas? = null

    var listaPictogramas = ArrayList<Pictograma>()
    var plan = Planificacion()
    lateinit var adaptador: AdaptadorPresentacion
    var currentDialog: Dialog? = null
    var pasosCompletados = Stack<Int>()
    val _pasoActual = MutableLiveData<Int>()

    var dialog: Dialog? = null

    lateinit var animFondo: Animation
    lateinit var animCard: Animation

    var idUsuario = ""

    var isRunning = false
    var currentRunnable: Runnable? = null
    var currentPosition: Int = 0
    val handler = Handler()

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    val dayOfWeek = dateFormat.format(calendar.time)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val month = monthFormat.format(calendar.time)

    var planificaciones = ArrayList<Evento>()
    var evento = Evento()
    lateinit var eventos: ArrayList<Evento>

    fun dpToPx(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    override fun diaSeleccionado(context: Context?, fecha: LocalDate) {
        fechaSeleccionada = fecha
        CalendarioUtilidades.fechaSeleccionada = fecha
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormatter.format(fecha)

        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        val dayOfWeek = dayOfWeekFormatter.format(fecha)
        val dayOfMonth = fecha.dayOfMonth.toString()
        val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
        val month = monthFormatter.format(fecha)

        if (context != null) {
            _diaText.value =  context.getString(R.string.formatted_date, dayOfWeek, dayOfMonth, month)
        }

        //_dismissDialog.value = true
        mostrarPlan(context)
        //dialog?.dismiss()
    }

    fun mostrarPlan(context: Context?) {
        listaPictogramas = idUsuario.let {
            plan.mostrarPlanificacion(it, selectedDate, context)
        } as ArrayList<Pictograma>

        //Mostrar título de la planificación
        val tituloObtenido = plan.obtenerTituloPlan(idUsuario, selectedDate, context)

        _tituloLiveData.value = tituloObtenido
        _planLiveData.value = listaPictogramas
    }

    fun configureDataEvento(){
        _tituloLiveData.value = _tituloLiveData.value
        _planLiveData.value = _planLiveData.value
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
        isRunning = false
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

    fun configureUser(prefs : android.content.SharedPreferences, context: Context){
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
        planificaciones = userId?.let { evento.obtenerTodosEventos(it, context as Activity) } as ArrayList<Evento>
    }

    fun initializeAnimations(applicationContext: Context) {
        animFondo = AnimationUtils.loadAnimation(applicationContext, R.anim.confeti)
        animCard = AnimationUtils.loadAnimation(applicationContext, R.anim.card)
    }

    fun obtenerEventosFecha(context: Context){
        eventos = idUsuario.let { evento.obtenerEventos(it, context as Activity, CalendarioUtilidades.fechaSeleccionada) } as ArrayList<Evento>
    }

}
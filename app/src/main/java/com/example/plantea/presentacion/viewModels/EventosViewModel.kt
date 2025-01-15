package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.AniadirPictoUtils
import com.example.plantea.presentacion.actividades.AniadirPictoUtils.Companion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.CommonUtils.Companion.toPreservedByteArray
import com.example.plantea.presentacion.actividades.MainActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacionesFuturas
import com.example.plantea.presentacion.adaptadores.AdaptadorPresentacion
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class EventosViewModel: ViewModel(), AdaptadorCalendario.OnItemSelectedListener, AdaptadorPlanificacionesFuturas.OnItemSelectedListener, AdaptadorPresentacion.OnItemSelectedListener, AniadirPictoUtils.Companion.CustomViewModel {

    override val pictograma: Pictograma = Pictograma()
    override var idUsuario: String = ""
    override var _nuevoPicto: SingleLiveEvent<Pictograma?> = SingleLiveEvent()
    override var _listaPictoRandom: SingleLiveEvent<ArrayList<Pictograma>> = SingleLiveEvent()
    override lateinit var adaptadorRandomPictos: AdaptadorNuevoPicto
    override var _listaPictogramas: SingleLiveEvent<ArrayList<Pictograma>> = SingleLiveEvent()
    override var _imageSelected = SingleLiveEvent<Bitmap>()
    override lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    override var saltar = false
    override var isEditImage = false
    override var isCalendarioMensual = false

    var fechaSeleccionada : LocalDate = LocalDate.now()
    var _diaText = MutableLiveData<String>()
    var tituloPlan = String()
    val _fechaActual = MutableLiveData<String>()
    val _diasMes = MutableLiveData<ArrayList<LocalDate?>>()
    val _planLiveData: MutableLiveData<ArrayList<Pictograma>?> = MutableLiveData()
    val _noEvents = SingleLiveEvent<Boolean>()
    var _pasosCompletados = MutableLiveData<ArrayList<Int>?>()

    var _pictoChanged = SingleLiveEvent<Boolean>()
    var posicionSelectedCambio = -1

    var countDownTimer: CountDownTimer? = null

    var imprevistos = ArrayList<Int>()
    var tachados = ArrayList<Int>()

    lateinit var recyclerView: RecyclerView

    var listaPictogramas = ArrayList<Pictograma>()
    var plan = Planificacion()
    lateinit var adaptador: AdaptadorPresentacion
    var currentDialog: Dialog? = null

    var dialog: Dialog? = null

    private lateinit var animFondo: Animation
    private lateinit var animCard: Animation

    var idUsuarioTEA = ""

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

    var evento = Evento()
    lateinit var eventos: ArrayList<Evento>

    //private lateinit var imagenConfeti: ImageView
    //private lateinit var mensajePremio: TextView

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
        _pasosCompletados.value = ArrayList()
        val idUsuarioEvento = if(idUsuarioTEA != ""){ //TODO: Tecnicamente no es necesario hacer esto ya que siempre va a ser idUsuarioTEA o se va a proporcionar los pictogramas si es idUsuario
            idUsuarioTEA
        }else{
            idUsuario
        }

        val check = evento.checkEventosDia(idUsuarioEvento, selectedDate, context)

        if(check > -1){
            //existe un evento visible para este dia
            evento = evento.obtenerEventoPlan(idUsuarioEvento, selectedDate, context)
            listaPictogramas = idUsuarioEvento.let { plan.mostrarPlanificacion(it, evento.id.toString(), context, Locale.getDefault().language) } as ArrayList<Pictograma>
            tituloPlan = evento.nombre.toString()

            for (pictogram in listaPictogramas) {
                if(pictogram.idAPI != 0)
                    pictogram.imagen = BitmapFactory.decodeResource(context?.resources, R.drawable.loading_placeholder)
            }

            _planLiveData.value = listaPictogramas

            CoroutineScope(Dispatchers.Main).launch {
                listaPictogramas.forEach { pictogram ->
                    if (pictogram.idAPI != 0) {
                        pictogram.imagen = withContext(Dispatchers.IO) {
                            CommonUtils.getImagenAPI(pictogram.idAPI)
                        }
                    }
                    adaptador.notifyItemChanged(listaPictogramas.indexOf(pictogram))
                }
            }
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
        idUsuario = prefs.getString("idUsuario", "").toString()
        idUsuarioTEA = prefs.getString("idUsuarioTEA", "").toString()
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

        val prefs: SharedPreferences = context.getSharedPreferences("Preferencias", MODE_PRIVATE)

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialogo_presentacion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val  btnCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogoEvento)
        val pictograma = dialog.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val tituloPictograma = dialog.findViewById<TextView>(R.id.lbl_pictograma)
        val historia = dialog.findViewById<ConstraintLayout>(R.id.Bubble)
        val btnAnterior : MaterialButton? = dialog.findViewById(R.id.btn_anterior)
        val btnSiguiente: MaterialButton? = dialog.findViewById(R.id.btn_siguiente)
        val progress = dialog.findViewById<ProgressBar>(R.id.duracionPicto)
        val duracionText = dialog.findViewById<TextView>(R.id.duracionPictoTiempo)
       // imagenConfeti = dialog.findViewById(R.id.img_confeti)
       // mensajePremio = dialog.findViewById(R.id.txt_premio)

        val  dialogoPresentacion = dialog.findViewById<ConstraintLayout>(R.id.dialogo_presentacion_2)
        pictograma.setImageBitmap(listaPictogramas[posicion].imagen)

        tituloPictograma.text = listaPictogramas[posicion].titulo
        if(prefs.getString("configPictogramas", "default") == "imagen"){
            tituloPictograma.visibility = View.INVISIBLE
        }

        if (listaPictogramas[posicion].duracion.toString() != "null") {
            progress.visibility = View.VISIBLE
            duracionText.visibility = View.VISIBLE
            val time = CommonUtils.formatTimeSeconds(listaPictogramas[posicion].duracion.toString())
            startTimerDialog(time.toLong() * 1000, progress, duracionText)
        } else {
            progress.visibility = View.GONE
            duracionText.visibility = View.GONE
        }

        dialogoPresentacion.clearAnimation()
      /*  imagenConfeti.clearAnimation()
        mensajePremio.clearAnimation()
        imagenConfeti.visibility = View.INVISIBLE
        mensajePremio.visibility = View.INVISIBLE*/

        val textoHistoria = dialog.findViewById<TextView>(R.id.lblBubble)
        val avatarHistoria = dialog.findViewById<ShapeableImageView>(R.id.avatarBubble)

        // Si tenemos historias
        if (listaPictogramas[posicion].historia != null) {
            textoHistoria.text = listaPictogramas[posicion].historia
            historia.visibility = View.VISIBLE
            if (prefs.getBoolean("PlanificadorLogged" , false)) {
                avatarHistoria.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenPlanificador", "")?.toPreservedByteArray))
            } else {
                avatarHistoria.setImageBitmap(CommonUtils.byteArrayToBitmap(prefs.getString("imagenUsuarioTEA", "")?.toPreservedByteArray))
            }
        } else {
            historia.visibility = View.GONE
        }

        if (posicion > 0) {
            btnAnterior?.visibility = View.VISIBLE
        } else {
            btnAnterior?.visibility = View.GONE
        }

        btnAnterior?.setOnClickListener {
            // Quitamos el actual y el anterior ya que en el siguente onItemSeleccionado se va a añadir de nuevo
            _pasosCompletados.value?.removeLast()
            _pasosCompletados.value?.removeLast()

            adaptador.listMarcados = _pasosCompletados.value!!
            adaptador.notifyItemChanged(posicion)

            onItemSeleccionado(context, posicion - 1)
            dialog.dismiss()
        }

        if (posicion < listaPictogramas.size - 1) {
            btnSiguiente?.visibility = View.VISIBLE
        } else {
            btnSiguiente?.visibility = View.GONE
        }

        btnSiguiente?.setOnClickListener {
            recyclerView.findViewHolderForAdapterPosition(posicion + 1)?.itemView?.performClick()
            if(listaPictogramas[posicion + 1].duracion.toString() != "null"){
                startTimerDialog(CommonUtils.formatTimeSeconds(listaPictogramas[posicion + 1].duracion.toString()).toLong() * 1000, progress, duracionText)
            }
            dialog.dismiss()
        }

        if(listaPictogramas[posicion].pictoEntretenimiento != 0){
            val buttonEntretenimiento = dialog.findViewById<MaterialButton>(R.id.showEntretenimiento)
            buttonEntretenimiento.visibility = View.VISIBLE
            buttonEntretenimiento.setOnClickListener {
                animacionEntretenimiento(context, posicion)
                buttonEntretenimiento.visibility = View.GONE
            }
        }else{
            val buttonEntretenimiento = dialog.findViewById<MaterialButton>(R.id.showEntretenimiento)
            buttonEntretenimiento.visibility = View.GONE
        }

        if(adaptador.imprevistos.contains(posicion)){
            _pasosCompletados.value?.add(posicion-1)
            _pasosCompletados.value?.add(posicion)
            }
        else{
            _pasosCompletados.value?.add(posicion)
        }


        _pasosCompletados.postValue(_pasosCompletados.value)
        adaptador.notifyItemChanged(posicion, "marcar")

        if(posicion != 0 && listaPictogramas[posicion-1].duracion.toString() != "null"){
            adaptador.notifyItemChanged(posicion-1, "marcarDuracion")
        }

        currentDialog = dialog

        //Botón cerrar
        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            countDownTimer?.cancel()
        }

        dialog.show()
    }

    override fun checkPosition(posicion: Int): Boolean {
        return if(_pasosCompletados.value?.isEmpty()!!){
            posicion == 0 || imprevistos.contains(posicion)
        }else{
            _pasosCompletados.value?.last()!! + 1 == posicion || (_pasosCompletados.value?.last()!!+2  == posicion && imprevistos.contains(posicion))
        }
    }

   /* private fun animacionesConfeti(context: Context, categoria: Int) {
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
            mensajePremio.text = context.getString(R.string.lbl_mientras_esperamos)
            imagenConfeti.animation = animCard
            animCard.start()
            mensajePremio.animation = animCard
            animCard.start()
        }
    }*/

    private fun animacionEntretenimiento(context: Context, posicion: Int) {
        val entretenimiento = currentDialog?.findViewById<ShapeableImageView>(R.id.img_pictograma)
        val entretenimientoTitle = currentDialog?.findViewById<TextView>(R.id.lbl_pictograma)

        var pictoEntretenimiento = Pictograma()
        if(listaPictogramas[posicion].pictoEntretenimiento == -1){
            val prefs = context.getSharedPreferences("Preferencias", MODE_PRIVATE)
            val imagen = Uri.parse(prefs.getString("imagenObjeto", ""))
            pictoEntretenimiento.imagen = BitmapFactory.decodeFile(imagen.path) //TODO revisar que no peta
            pictoEntretenimiento.titulo = prefs.getString("nombreObjeto", "")
        }else{
            pictoEntretenimiento = pictoEntretenimiento.obtenerPicto(context, listaPictogramas[posicion].pictoEntretenimiento.toString(), Locale.getDefault().language)
        }

       entretenimiento?.setImageBitmap(pictoEntretenimiento.imagen)


        // Add fade-in animation
        entretenimiento?.alpha = 0f
        entretenimiento?.animate()?.apply {
            duration = 1000
            alpha(1f)
            start()
        }
        entretenimientoTitle?.text = pictoEntretenimiento.titulo
    }

    override fun onItemLongClick(activity: Activity, posicion: Int) {
        AniadirPictoUtils.initializeDialog(this, activity)
        posicionSelectedCambio = posicion
    }

    fun checkInitializedAdapter(): Boolean {
        return ::adaptador.isInitialized
    }

    override fun dialogoCambio(itemView: View, progressBar: ProgressBar, duracionText: TextView, context: Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialogo_retraso_tiempo)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val buttonPersonalizado = dialog.findViewById<RadioButton>(R.id.btn_personalizado)
        val buttonGuardar = dialog.findViewById<Button>(R.id.btn_guardar)
        val buttonCerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)
        val layoutTime =  dialog.findViewById<LinearLayout>(R.id.numberPickerLayout)

        var timeSelected = 0L
        var lastChecked = 0

        // tiempo en segundos
        val buttons = mapOf(
            R.id.btn_30 to 30000,
            R.id.btn_1 to 60000,
            R.id.btn_5 to 300000,
            R.id.btn_10 to 600000
        )

        buttons.forEach { (buttonId, time) ->
            dialog.findViewById<RadioButton>(buttonId).setOnClickListener {
                timeSelected = time.toLong()
                if(lastChecked != 0){
                    dialog.findViewById<RadioButton>(lastChecked).isChecked = false
                    if(lastChecked == R.id.btn_personalizado){
                        layoutTime.visibility = View.GONE
                    }
                }
                lastChecked = buttonId

            }
        }

        buttonPersonalizado.setOnClickListener {
            if(lastChecked != 0)
                dialog.findViewById<RadioButton>(lastChecked).isChecked = false

            lastChecked = R.id.btn_personalizado
            layoutTime.visibility = View.VISIBLE

            val timePicker = dialog.findViewById<NumberPicker>(R.id.timePicker)
            timePicker.minValue = 1
            timePicker.maxValue = 59

            timePicker.setOnValueChangedListener { _, _, newVal ->
                timeSelected = newVal * 60000L
            }
        }

        buttonGuardar.setOnClickListener {
             adaptador.countDownTimer?.cancel()
            // if time is more than 1 minute, update the progressbar color
             if(timeSelected + adaptador.timeLeft > 60000){
                 progressBar.progressDrawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.md_theme_light_primary), PorterDuff.Mode.SRC_IN) //poner esto bien, ahora mismo es un apañño
             }
             adaptador.startTimer(timeSelected + adaptador.timeLeft, progressBar,  duracionText)
             dialog.dismiss()
        }

        buttonCerrar.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }

    private fun startTimerDialog(time : Long, progressBar: ProgressBar, duracionText: TextView){
        countDownTimer = object : CountDownTimer(time, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                progressBar.progress = ((millisUntilFinished / 1000).toFloat() / (time / 1000).toFloat() * 100).toInt()
                val hours = millisUntilFinished / 3600000
                val minutes = (millisUntilFinished % 3600000) / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                if(hours == 0L){
                    duracionText.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                }else{
                    duracionText.text = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
                }
            }

            override fun onFinish() {
                countDownTimer?.cancel()
            }
        }
        countDownTimer!!.start()
    }

    //Metodos de la interfaz de AniadirPictoUtils
    override val onItemSelectedListener = object : AdaptadorNuevoPicto.OnItemSelectedListener {
        override fun onNuevoPicto(pictogram: Pictograma?) {
            _nuevoPicto.value = pictogram
        }
    }

    override fun setupTitle(titleDialog: TextView, activity: Activity) {
        titleDialog.text = activity.getString(R.string.lbl_imprevistoPicto).uppercase()
    }

    override fun dialogoAniadirPicto(dialogo: Dialog, view: ViewGroup, activity: Activity, buttons: LinearLayout, btnSiguiente: MaterialButton, btnSaltar: MaterialButton) {
        val lastView = view.getChildAt(view.childCount - 1)
        view.removeAllViews()
        view.addView(activity.layoutInflater.inflate(R.layout.fragment_nuevo_picto_semana, null))
        if(CommonUtils.isMobile(activity)){
            view.layoutParams.height = 800
            view.requestLayout()
        }

        val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
        imagenPicto.setImageBitmap(_nuevoPicto.value?.imagen)

        imagenPicto.setOnClickListener {
            buttons.visibility = View.VISIBLE
            btnSiguiente.visibility = View.VISIBLE
            adaptadorRandomPictos.currentPosition = RecyclerView.NO_POSITION
            adaptadorRandomPictos.notifyDataSetChanged()

            view.removeAllViews()
            view.addView(lastView)
        }

        val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)
        buttonGuardar.text = activity.getString(R.string.cambiar).uppercase()

        buttonGuardar.setOnClickListener {
            if (imagenPicto.drawable == null) {
                Toast.makeText(activity, R.string.toast_necesita_imagen, Toast.LENGTH_SHORT).show()
            } else {
                _pictoChanged.value = true
                dialogo.dismiss() //Cerrar dialogo
            }
        }
    }

    override fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}
package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.DiaMes
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.AniadirPictoUtils
import com.example.plantea.presentacion.actividades.AniadirPictoUtils.Companion
import com.example.plantea.presentacion.actividades.AniadirPictoUtils.Companion.clearButtonsSelected
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendarioMensual
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendarioMensualFechas
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

class CalendarioMensualViewModel: ViewModel(), AdaptadorCalendarioMensual.OnItemSelectedListener, AdaptadorCalendarioMensualFechas.OnItemSelectedListener,  AniadirPictoUtils.Companion.CustomViewModel  {

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

    val _fechaActual = MutableLiveData<String>()
    val _fechaSeleccionada = MutableLiveData<DiaMes>()
    var _dias = MutableLiveData<ArrayList<LocalDate?>>()
    var fechas = ArrayList<DiaMes>()
    lateinit var adaptador: AdaptadorNuevoPicto
    var _addedFecha = SingleLiveEvent<DiaMes>()
    var colorSelected = "default"
    var _newImage = SingleLiveEvent<Boolean>()

    fun configureUser(prefs : android.content.SharedPreferences){
        val userId = if(prefs.getString("idUsuarioTEA", "") == null || prefs.getString("idUsuarioTEA", "") == ""){
            prefs.getString("idUsuario", "")
        } else{
            prefs.getString("idUsuarioTEA", "")
        }
        idUsuario = userId.toString()
    }

    fun obtenerVistaMes() {
        _fechaActual.value = CalendarioUtilidades.formatoMesAnio(CalendarioUtilidades.fechaSeleccionada).uppercase(Locale.getDefault())
        _dias.value = CalendarioUtilidades.obtenerDiasMes(CalendarioUtilidades.fechaSeleccionada)
    }

    override fun diaSeleccionado(context: Context?, position: Int) {
        _fechaSeleccionada.value = fechas[position]
    }

    override fun diaSeleccionadoFecha(context: Context?, position: Int) {
        _fechaSeleccionada.value = fechas[position]
    }

    fun createCalendar(context: Context): MaterialDatePicker<Long> {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("")
            .setTheme(R.style.CalendarPicker)
            .build()

        datePicker.show((context as AppCompatActivity).supportFragmentManager, "DatePicker")
        return datePicker
    }

    override fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override val onItemSelectedListener = object : AdaptadorNuevoPicto.OnItemSelectedListener {
        override fun onNuevoPicto(pictogram: Pictograma?) {
            _nuevoPicto.value = pictogram
            Log.i("Pictograma", pictogram?.titulo.toString())
        }
    }

    override fun setupTitle(titleDialog: TextView, activity: Activity) {
        if(isEditImage){
            titleDialog.text = activity.getString(R.string.editar_imagen)
        }else{
            titleDialog.text = activity.getString(R.string.agregar_fecha)
        }
    }

    override fun dialogoAniadirPicto(dialogo: Dialog, view: ViewGroup, activity: Activity, buttons: LinearLayout, btnSiguiente: MaterialButton, btnSaltar: MaterialButton) {
        if(isEditImage){
            _newImage.value = true
        }else{
            val lastView = view.getChildAt(view.childCount - 1)
            view.removeAllViews()
            view.addView(activity.layoutInflater.inflate(R.layout.dialogo_nueva_fecha, null))
            val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
            if(_nuevoPicto.value?.imagen != null && !saltar){
                imagenPicto.setImageBitmap(_nuevoPicto.value?.imagen)
            }

            imagenPicto.setOnClickListener {
                buttons.visibility = View.VISIBLE
                btnSiguiente.visibility = View.VISIBLE
                btnSaltar.visibility = View.VISIBLE
                adaptadorRandomPictos.currentPosition = RecyclerView.NO_POSITION
                adaptadorRandomPictos.notifyDataSetChanged()

                view.removeAllViews()
                view.addView(lastView)
            }

            val nombreFecha = view.findViewById<TextInputLayout>(R.id.txt_Titulo)
            val fechaTextView = view.findViewById<TextInputLayout>(R.id.txt_Fecha)
            val fechaEditText = view.findViewById<TextInputEditText>(R.id.editTextFecha)
            fechaTextView.editText?.setText(CalendarioUtilidades.formatoFecha(CalendarioUtilidades.fechaSeleccionada))
            colorSelected = "default"

            buttonsColors(dialogo, activity, false)

            val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)

            fechaEditText.setOnClickListener {
                val picker = createCalendar(activity)

                picker.addOnPositiveButtonClickListener {
                    CalendarioUtilidades.fechaSeleccionada = Instant.ofEpochMilli(picker.selection!!).atZone(
                        ZoneId.systemDefault()).toLocalDate()
                    fechaTextView.editText?.setText(CalendarioUtilidades.formatoFecha(CalendarioUtilidades.fechaSeleccionada))
                }
            }

            buttonGuardar.setOnClickListener {
                //if fechaSeleccionada is in viewModel.fechas return error
                if (fechas.any { it.fecha == CalendarioUtilidades.fechaSeleccionada }) {
                    fechaTextView.error = activity.getString(R.string.toast_fecha_existente)
                } else
                    if (nombreFecha.editText?.text.toString().isEmpty()) {
                        nombreFecha.error = activity.getString(R.string.toast_obligatorio)
                    }else{
                        //drawable to bitmap
                        val imagenDia =  if(imagenPicto.drawable == null){
                            null
                        }else{
                            (imagenPicto.drawable as BitmapDrawable).bitmap
                        }
                        val diaMes = DiaMes(CalendarioUtilidades.fechaSeleccionada, nombreFecha.editText?.text.toString().uppercase(), colorSelected, imagenDia)
                        _addedFecha.value = diaMes
                        dialogo.dismiss()
                    }
            }
        }
    }


    fun buttonsColors(dialogo: Dialog, activity: Activity, isEdit: Boolean) {
        val buttonMorado = dialogo.findViewById<MaterialButton>(R.id.fab1)
        val buttonRosa = dialogo.findViewById<MaterialButton>(R.id.fab2)
        val buttonVerde = dialogo.findViewById<MaterialButton>(R.id.fab3)
        val buttonAmarillo = dialogo.findViewById<MaterialButton>(R.id.fab4)
        val buttonAzul = dialogo.findViewById<MaterialButton>(R.id.fab5)
        val buttonDefault = dialogo.findViewById<MaterialButton>(R.id.fab6)

        when (colorSelected) {
            "purple" -> {
                buttonMorado?.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            }
            "pink" -> {
                buttonRosa?.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            }
            "green" -> {
                buttonVerde?.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            }
            "yellow" -> {
                buttonAmarillo?.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            }
            "blue" -> {
                buttonAzul?.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            }
            "default" -> {
                buttonDefault?.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
            }
        }

        buttonAmarillo.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            selectColor("yellow", buttonAmarillo, activity, dialogo, isEdit)
        }

        buttonAzul.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            selectColor("blue", buttonAzul, activity, dialogo, isEdit)
        }

        buttonMorado.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            selectColor("purple", buttonMorado, activity, dialogo, isEdit)
        }

        buttonRosa.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            selectColor("pink", buttonRosa, activity, dialogo, isEdit)
        }

        buttonVerde.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            selectColor("green", buttonVerde, activity, dialogo, isEdit)
        }

        buttonDefault.setOnClickListener {
            clearButtonsSelected(buttonMorado, buttonRosa, buttonVerde, buttonAmarillo, buttonDefault, buttonAzul)
            selectColor("default", buttonDefault, activity, dialogo, isEdit)
        }
    }

    private fun selectColor(color: String, buttonColor: MaterialButton, activity: Activity, dialogo: Dialog, isEdit: Boolean){
        buttonColor.icon = ContextCompat.getDrawable(activity,R.drawable.svg_check)
        colorSelected = color
        if(isEdit) dialogo.findViewById<MaterialCardView>(R.id.card).setCardBackgroundColor(CommonUtils.getColor(activity, colorSelected))
    }

}
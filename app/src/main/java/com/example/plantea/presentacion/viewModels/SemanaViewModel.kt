package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.DiaSemana
import com.example.plantea.dominio.Evento
import com.example.plantea.presentacion.actividades.AniadirPictoUtils
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.adaptadores.AdaptadorTablaSemana
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale


class SemanaViewModel: ViewModel(), AdaptadorNuevoPicto.OnItemSelectedListener, AdaptadorTablaSemana.OnItemSelectedListener {

    var idUsuario = ""
    var _nuevoPicto = SingleLiveEvent<Pictograma?>()
    lateinit var adaptador: AdaptadorNuevoPicto
    var _listaPictoRandom = SingleLiveEvent<ArrayList<Pictograma>>()
    var _listaPictogramas = SingleLiveEvent<ArrayList<Pictograma>>()
    var pictograma = Pictograma()
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var daySelected = ""
    var colorSelected: String? = null
    lateinit var week: ArrayList<DiaSemana>
    var isEdit = false
    var configuration = 0

    var _imageSelected = MutableLiveData<Bitmap>()
    var _itemBorrado = MutableLiveData<Int>()
    var _itemColor = MutableLiveData<Int>()
    var _diaClicked = MutableLiveData<String?>()


    fun configureUser(prefs : SharedPreferences){
        val userId = prefs.getString("idUsuario", "")
        val userIdTEA = prefs.getString("idUsuarioTEA", "")
        idUsuario = if(userIdTEA == ""){
            userId.toString()
        }else{
            userIdTEA.toString()
        }
    }

    override fun onNuevoPicto(pictogram: Pictograma?) {
        _nuevoPicto.value = pictogram
    }

    fun obtenerConfigDias(activity: Activity): ArrayList<DiaSemana> {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        var date = monday
        val days = mutableListOf<String>()
        while (!date.isAfter(sunday)) {
            days.add(date.format(formatter))
            date = date.plusDays(1)
        }

        val semana = DiaSemana()
        return semana.obtenerDias(idUsuario, days, activity)
    }

    fun configurarDaysWeek(configuration: Int): Array<String> {
        val language = Locale.getDefault().language
        val daysFull = if (language == "es") {
            arrayOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
        } else {
            arrayOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
        }
        val daysShort = if (language == "es") {
            arrayOf("L", "M", "X", "J", "V", "S", "D")
        } else {
            arrayOf("M", "T", "W", "T", "F", "S", "S")
        }
        return if (configuration == 2) daysShort else daysFull
    }

    fun getPictogramas(query: String, isNuevoPictoBusqueda: Boolean, activity: Activity) {
        val pictogramasBusqueda = ArrayList<Pictograma>()
        CoroutineScope(Dispatchers.IO).launch {
            val dict = CommonUtils.getDataApi(query)

            withContext(Dispatchers.Main) {
                dict.keys.mapNotNull { key ->
                    dict[key]?.let { (value, id) ->
                        pictogramasBusqueda.add(crearPictoBusqueda(key, value, id, activity))
                    }
                }
            }

            if (pictogramasBusqueda.isNotEmpty()) {
                if(isNuevoPictoBusqueda){
                    _listaPictoRandom.postValue(pictogramasBusqueda)
                } else{
                    _listaPictogramas.postValue(pictogramasBusqueda)
                }

            }
        }
    }

    private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int, activity: Activity): Pictograma {
        val tituloMayus = titulo?.uppercase()
        val favorito = pictograma.getFavorito(activity, id.toString(), idUsuario)
        return Pictograma(id.toString(), tituloMayus, bitmap, 0, 0, favorito)
    }

    override fun onItemSeleccionado(posicion: Int, activity: Activity?) {
        if (activity != null)
            AniadirPictoUtils.initializeDialog(this, activity, false)
        daySelected = week[posicion].dia.toString()
    }

    override fun onBorrarItemSeleccionado(posicion: Int) {
        _itemBorrado.value = posicion
    }

    override fun onColorSelected(posicion: Int, color: String?, activity: Activity) {
        colorSelected = color
        _itemColor.value = posicion
    }

    override fun onDiaClicked(posicion: Int, activity: Activity) {
        if(week[posicion].idEvento != null){
            _diaClicked.value = posicion.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onAsociarEvento(posicion: Int, activity: Activity) {

        val evento = Evento()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val fecha = LocalDate.parse(week[posicion].dia, formatter)
        val eventos = evento.obtenerEventos(idUsuario, activity, fecha)

        if (eventos.isEmpty()) {
            Toast.makeText(activity, "No hay eventos para este día", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            val dialog = Dialog(activity)
            dialog.setContentView(com.example.plantea.R.layout.dialogo_semana_evento)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val spinner = dialog.findViewById<android.widget.Spinner>(com.example.plantea.R.id.spinner_eventos)
            val tituloDialog = dialog.findViewById<android.widget.TextView>(com.example.plantea.R.id.titulo)
            tituloDialog.text = activity.getString(com.example.plantea.R.string.selecciona_el_evento_para_el_dia) + " " + fecha.dayOfMonth

            val guardar = dialog.findViewById<android.widget.Button>(com.example.plantea.R.id.btn_guardar)

            val eventosNombres : ArrayList<String> = ArrayList()
            for (evento in eventos){
                evento.nombre?.let { eventosNombres.add(it) }
            }
            eventosNombres.add(activity.getString(com.example.plantea.R.string.sin_evento))

            val adapter = android.widget.ArrayAdapter(activity.applicationContext, com.example.plantea.R.layout.simple_spinner_item_idioma, eventosNombres)
            spinner.adapter = adapter

            //if evento already is configured, select it
            spinner.setSelection(eventos.indexOfFirst { it.id.toString() == week[posicion].idEvento})

            guardar.setOnClickListener {
                if(spinner.selectedItemPosition == eventos.size){
                    week[posicion].idEvento = null
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val eventoSeleccionado = eventos[spinner.selectedItemPosition]
                week[posicion].idEvento = eventoSeleccionado.id.toString()
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}
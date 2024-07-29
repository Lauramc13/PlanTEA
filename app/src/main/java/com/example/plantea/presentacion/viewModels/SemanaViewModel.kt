package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.DiaSemana
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
    var _imagenNuevoPicto = SingleLiveEvent<String?>()
    lateinit var adaptador: AdaptadorNuevoPicto
    var _listaPictoRandom = SingleLiveEvent<ArrayList<Pictograma>>()
    var _listaPictogramas = SingleLiveEvent<ArrayList<Pictograma>>()
    var pictograma = Pictograma()
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var daySelected = ""
    lateinit var week: ArrayList<DiaSemana>
    var isEdit = false
    var configuration = 0

    var _imageSelected = MutableLiveData<Bitmap>()
    var _itemBorrado = MutableLiveData<Int>()


    fun configureUser(prefs : SharedPreferences){
        val userId = prefs.getString("idUsuario", "")
        idUsuario = userId.toString()
    }

    override fun onNuevoPicto(imagenPicto: String?) {
        _imagenNuevoPicto.value = imagenPicto
    }

    fun guardarImagen(context: Context, nombre: String, imagen: Bitmap): String {
        val cw = ContextWrapper(context)
        val dirImages = cw.getDir("Imagenes", AppCompatActivity.MODE_PRIVATE)
        val myPath = File(dirImages, "$nombre.png")
        val fos: FileOutputStream?
        try {
            fos = FileOutputStream(myPath)
            imagen.compress(Bitmap.CompressFormat.PNG, 10, fos) // calidad a 0 imagen mas pequeña
            fos.flush()
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return myPath.absolutePath
    }

    fun savePicto(image: Drawable){
        if (image is BitmapDrawable) {
            val imageBitmap: Bitmap = image.bitmap
            _imageSelected.value = imageBitmap
        }
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
        return if(byteArray == null){
            null
        }else{
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }


    fun obtenerImagenes(activity: Activity): ArrayList<DiaSemana> {
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
        val diasSemana = ArrayList<DiaSemana>()
        val imagenes = semana.obtenerImagenes(idUsuario, days, activity)
        for(i in 0..6){
            val bitmap = byteArrayToBitmap(imagenes[i])
            val dia = DiaSemana(days[i], bitmap)
            diasSemana.add(dia)
        }
        return diasSemana
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
        val archivo = CommonUtils.crearImagen(bitmap, titulo, activity)
        return Pictograma(id.toString(), tituloMayus, archivo, 0, 0, favorito, true)
    }

    override fun onItemSeleccionado(posicion: Int, activity: Activity?) {
        if (activity != null)
            AniadirPictoUtils.initializeDialog(this, activity, false)
        daySelected = week[posicion].dia.toString()
    }

    override fun onBorrarItemSeleccionado(posicion: Int) {
        _itemBorrado.value = posicion
    }
}
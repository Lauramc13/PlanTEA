package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class SemanaViewModel: ViewModel(), AdaptadorNuevoPicto.OnItemSelectedListener {

    var idUsuario = ""
    var _imagenNuevoPicto = SingleLiveEvent<String?>()
    lateinit var adaptador: AdaptadorNuevoPicto
    var _listaPictoRandom = SingleLiveEvent<ArrayList<Pictograma>>()
    var _listaPictogramas  = SingleLiveEvent<ArrayList<Pictograma>>()
    var pictograma = Pictograma()
    val _image = MutableLiveData<Uri?>()
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var daySelected = ""

    var _imageSelected = MutableLiveData<Bitmap>()


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

}
package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Cuaderno
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorCategoriasCuaderno
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CuadernoViewModel: ViewModel(), AdaptadorCategoriasCuaderno.OnItemSelectedListener,  AdaptadorNuevoPicto.OnItemSelectedListener{

    var picto = Pictograma()
    var cuaderno = Cuaderno()
    var isPlanificador = true
    var isBusqueda = false
    var sourceAPI = false
    var listaPictosAgregados: ArrayList<String> = ArrayList()
    var idCuaderno: Int = 0
    var listaPictogramas: ArrayList<Pictograma>? = null
    var originalPictogramas: ArrayList<Pictograma>? = null
    var listaCuadernos: ArrayList<Cuaderno>? = ArrayList()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var listaPictoCuaderno: ArrayList<Cuaderno>
    var isTermometro: Boolean = true
    var tituloCuaderno : String = ""
    lateinit var image: ShapeableImageView

    var _imagenNuevoPicto = SingleLiveEvent<String?>()
    lateinit var adaptador: AdaptadorNuevoPicto //Adaptador del recyclerview del dialogo añadir picto
    lateinit var adaptadorCuaderno : AdaptadorCategoriasCuaderno //Adaptador del recyclerview de los cuadernos
    var _listaPictoRandom = SingleLiveEvent<ArrayList<Pictograma>>()
    val pictograma = Pictograma()
    var idUsuario = ""

    val _lastPictoClicked = SingleLiveEvent<Boolean>()
    val _posicionPictoClicked = SingleLiveEvent<Int>()
    val _cerrarFragment = SingleLiveEvent<Boolean>()
    val _queryBusqueda = SingleLiveEvent<String>()
    val _pictoBusquedaAdded = SingleLiveEvent<Pictograma>()
    val _removePicto = SingleLiveEvent<Pictograma>()

    //AdaptadorCategoriasCuaderno
    override fun categoriaCuaderno(position: Int, cuadernoId: Int){
        if(position == listaPictoCuaderno.lastIndex && isPlanificador){
            _lastPictoClicked.value = true
        }else {
            idCuaderno = cuadernoId
            _posicionPictoClicked.value = position
        }
    }

    override fun onNuevoPicto(imagenPicto: String?) {
        _imagenNuevoPicto.value = imagenPicto
    }

    fun getPictogramas(query: String, activity: Activity) {
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
                    _listaPictoRandom.postValue(pictogramasBusqueda)
            }
        }
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

    private fun crearPictoBusqueda(bitmap: Bitmap, titulo: String?, id: Int, activity: Activity): Pictograma {
        val tituloMayus = titulo?.uppercase()
        val archivo = CommonUtils.crearImagen(bitmap, titulo, activity)
        return Pictograma(id.toString(), tituloMayus, archivo, 0, 0, false, true)
    }
}


package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Cuaderno
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorCategoriasCuaderno
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno
import com.google.android.material.imageview.ShapeableImageView

class CuadernoViewModel: ViewModel(), AdaptadorCategoriasCuaderno.OnItemSelectedListener{

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
    val _image = MutableLiveData<Uri?>()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var listaPictoCuaderno: ArrayList<Cuaderno>
    var isTermometro: Boolean = true
    var tituloCuaderno : String = ""
    lateinit var image: ShapeableImageView

    var _imagenNuevoPicto = SingleLiveEvent<String>()

    val _lastPictoClicked = SingleLiveEvent<Boolean>()
    val _posicionPictoClicked = SingleLiveEvent<Int>()
    val _cerrarFragment = SingleLiveEvent<Boolean>()
    val _queryBusqueda = SingleLiveEvent<String>()
    val _pictoBusquedaAdded = SingleLiveEvent<Pictograma>()
    val _removePicto = SingleLiveEvent<Pictograma>()

    @SuppressLint("IntentReset")
    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun createPickMedia(fragment: Fragment, context: Context?, view: View) {
        pickMedia = fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = context?.contentResolver?.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val ruta = context?.let { CommonUtils.getPathFromUri(it, uri)}

                context?.let {
                    if (ruta != null) {
                        CommonUtils.guardarImagen(it, ruta, bitmap)
                        _image.value = uri

                    }
                }

            } else {
                if (context != null) {
                    Toast.makeText(context, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    //AdaptadorCategoriasCuaderno
    override fun categoriaCuaderno(position: Int, cuadernoId: Int){
        if(position == listaPictoCuaderno.lastIndex && isPlanificador){
            _lastPictoClicked.value = true
        }else {
            idCuaderno = cuadernoId
            _posicionPictoClicked.value = position
        }
    }
}

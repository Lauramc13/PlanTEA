package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.TraductorActivity
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.Locale
import com.example.plantea.R
import java.io.File
import java.io.FileOutputStream


class TraductorViewModel : ViewModel(), AdaptadorPictogramasTraductor.OnItemSelectedListener{

    var listaPictogramas: ArrayList<Pictograma> = ArrayList()
    private var listaTraducir : ArrayList<String> = ArrayList()
    private var listaPictoBuscador: MutableList<MutableMap<Bitmap, Pair<String, Int>>> = mutableListOf()
    lateinit var adaptador: AdaptadorPictogramasTraductor
    var _visibilityButtons = MutableLiveData<Boolean>()
    lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var bitmap : Bitmap? = null
    var ruta : String? = null
    var posicionSelected = -1

    val _dialogMessage = SingleLiveEvent<Int>()
    val _traduccionEnded = SingleLiveEvent<Boolean>()

    val _listaPictogramasTraduccion = MutableLiveData<ArrayList<Pictograma>>()
    var textInputContent = ""

    init {
        _listaPictogramasTraduccion.value = listaPictogramas
        _visibilityButtons.value = false
    }

    fun traducirFrase(texto: CharSequence?): Boolean {
        listaTraducir.clear()
        listaPictogramas.clear()
        listaPictoBuscador.clear()
        if (!texto.isNullOrBlank()) {
            val words = texto.split("\\s+".toRegex())
            listaTraducir.addAll(words)
            getPictogramas()
            return true
        }
        return false
    }

    private fun getPictogramas() {
       val job = CoroutineScope(Dispatchers.IO).launch {
            for (word in listaTraducir) {
                val dict = CommonUtils.getDataApi(word)
                if(dict.size == 1){
                    withContext(Dispatchers.Main) {
                        dict.keys.first().let{ key ->
                            dict[key]?.let {
                                crearPictoTraduccion(key, word, 0, null)
                                _listaPictogramasTraduccion.value = listaPictogramas
                            }
                        }
                    }
                }else{
                    withContext(Dispatchers.Main) {
                        dict.keys.first().let{ key ->
                            dict[key]?.let { (_, id) ->
                                crearPictoTraduccion(key, word, id, null)
                                _listaPictogramasTraduccion.value = listaPictogramas
                            }
                        }
                    }
                }

                listaPictoBuscador.add(dict)
            }
       }

       job.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                _traduccionEnded.value = true
            }
       }
    }

    private fun crearPictoTraduccion(bitmap: Bitmap, titulo: String?, idAPI: Int, posicion: Int?) {
        val id = generateBitmapId(bitmap)
        if(posicion != null){
            listaPictogramas[posicion] = Pictograma(id.toString(), titulo?.uppercase(), bitmap,  idAPI, 0, false)
        }else {
            listaPictogramas.add(Pictograma(id.toString(), titulo?.uppercase(), bitmap, idAPI, 0,false))
        }
    }


    //Genero un id unico para cada bitmap
    private fun generateBitmapId(bitmap: Bitmap): Int {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(byteArray)

        return ByteBuffer.wrap(digest.copyOfRange(0, 4)).int
    }

    private fun crearPlanificacion(context: Context, titulo: String) {
        val prefs = context.getSharedPreferences("Preferencias", AppCompatActivity.MODE_PRIVATE)
        val idUsuario = prefs.getString("idUsuario", "")

        val plan = Planificacion()
        val idPlan = idUsuario?.let { it1 ->
            plan.crearPlanificacion(context as TraductorActivity,  it1, titulo.uppercase(Locale.getDefault()))
        }
        val creada = plan.addPictogramasPlanTraductor(idPlan, context as TraductorActivity, listaPictogramas, idUsuario)

        if (creada == true) {
            _dialogMessage.value = R.string.toast_planificacion_creada
        } else {
            _dialogMessage.value = R.string.toast_error_crear_planificacion
        }
    }

    fun dialogGuardar(context: Context){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialogo_crear_planificacion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val titulo : TextInputLayout = dialog.findViewById(R.id.txt_title)
        val iconoCerrar : ImageView = dialog.findViewById(R.id.icono_CerrarDialogo)
        val btnCrear : Button = dialog.findViewById(R.id.btn_create)

        btnCrear.setOnClickListener{
            val tituloString = titulo.editText?.text.toString()

            //if tituloString is empty -> error
            if(tituloString.isEmpty()){
                titulo.error = "El campo no puede estar vacío"
                Toast.makeText(context, R.string.toast_campo_vacio, Toast.LENGTH_SHORT).show()
            }else{
                crearPlanificacion(context, tituloString)
                CommonUtils.hideKeyboard(context, titulo)
                dialog.dismiss()
            }
        }

        iconoCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun dialogoTraduccion(context: Context){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialogo_historia_traduccion)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val titulo : TextInputLayout = dialog.findViewById(R.id.txt_title)
        val iconoCerrar : ImageView = dialog.findViewById(R.id.icono_CerrarDialogo)
        val btnCrear : Button = dialog.findViewById(R.id.btn_create)

        btnCrear.setOnClickListener{
            var tituloString = titulo.editText?.text.toString()

            //if tituloString is empty -> error
            if(tituloString.isEmpty()){
                tituloString = ""
            }

            CommonUtils.guardarPDF(context, tituloString.uppercase(), listaPictogramas)
            CommonUtils.hideKeyboard(context, titulo)
            dialog.dismiss()
        }

        iconoCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onItemSeleccionado(posicion: Int, context: Context){
        try {
            val entryList =  listaPictoBuscador[posicion].entries.toList()
            val desiredId = listaPictogramas[posicion].id
            var positionItem = 0

            for ((index, entry) in entryList.withIndex()) {
                val bitmapId = generateBitmapId(entry.key).toString()
                if (bitmapId == desiredId) {
                    positionItem = index
                    break
                }
            }

            if (positionItem == entryList.lastIndex) {
                positionItem = 0
            } else {
                positionItem++
            }

            crearPictoTraduccion(entryList[positionItem].key, entryList[positionItem].value.first, entryList[positionItem].value.second, posicion)
            adaptador.notifyItemChanged(posicion)

        }catch (e: Exception){
            Log.d("ERROR", e.toString())
        }
    }

    @SuppressLint("IntentReset")
    override fun onLongItemSeleccionado(posicion: Int, context: Context){
        posicionSelected = posicion
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun imageSelected(){
         listaPictogramas[posicionSelected].imagen = bitmap!!
         listaPictogramas[posicionSelected].idAPI = 0

         adaptador.notifyItemChanged(posicionSelected)
    }



    override fun onItemEliminado(posicion: Int) {
        listaPictogramas.removeAt(posicion)
        listaPictoBuscador.removeAt(posicion)
        adaptador.notifyItemRemoved(posicion)
    }

}
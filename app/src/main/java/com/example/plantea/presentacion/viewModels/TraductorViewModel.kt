package com.example.plantea.presentacion.viewModels

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.ninio.TraductorActivity
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

class TraductorViewModel : ViewModel(), AdaptadorPictogramasTraductor.OnItemSelectedListener{

    var listaPictogramas: ArrayList<Pictograma> = ArrayList()
    private var listaTraducir : ArrayList<String> = ArrayList()
    private var listaPictoBuscador: MutableList<MutableMap<Bitmap, Pair<String, Int>>> = mutableListOf()
    lateinit var adaptador: AdaptadorPictogramasTraductor
    var _visibilityButtons = MutableLiveData<Boolean>()
    var speechInProgress = false

    val _dialogMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val _listaPictogramas = MutableLiveData<ArrayList<Pictograma>>()
    var textInputContent = ""

    init {
        _listaPictogramas.value = listaPictogramas
        _visibilityButtons.value = false
    }

    fun traducirFrase(texto: CharSequence?, context: Context): Boolean {
        listaTraducir.clear()
        listaPictogramas.clear()
        listaPictoBuscador.clear()
        if (!texto.isNullOrBlank()) {
            val words = texto.split("\\s+".toRegex())
            listaTraducir.addAll(words)
            getPictogramas(context)
            return true
        }
        return false
    }

    private fun getPictogramas(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            for (word in listaTraducir) {
                val dict = CommonUtils.getDataApi(word)

                withContext(Dispatchers.Main) {
                    dict.keys.first().let{ key ->
                        dict[key]?.let {
                            crearPictoTraduccion(key, word, null, context)
                            _listaPictogramas.value = listaPictogramas
                        }
                    }
                }
                listaPictoBuscador.add(dict)
            }
        }
    }

    private fun crearPictoTraduccion(bitmap: Bitmap, titulo: String?, posicion: Int?, context: Context) {
        val archivo = CommonUtils.crearImagen(bitmap, titulo, context)
        val id = generateBitmapId(bitmap)

        //Si sabemos la posicion del pictograma, estamos cambiando la imagen del pictograma
        if(posicion != null){
            listaPictogramas[posicion] = Pictograma(id.toString(), titulo?.uppercase(), archivo, 0, 0, false, true)
        }else {
            listaPictogramas.add(Pictograma(id.toString(), titulo?.uppercase(), archivo, 0, 0, false, true))
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
        val creada = plan.addPictogramasPlan(idPlan, context as TraductorActivity, listaPictogramas)

        if (creada == true) {
            _dialogMessage.value = "Planificación $titulo creada"
        } else {
            _dialogMessage.value = "Error al crear la planificación"
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
                Toast.makeText(context, "No puedes dejar el campo vacío", Toast.LENGTH_LONG).show()
            }else{
                crearPlanificacion(context, tituloString)
                dialog.dismiss()
            }
        }

        iconoCerrar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onItemSeleccionado(posicion: Int, context: Context){
        try {
            val entryList =  listaPictoBuscador[posicion].entries.toList()
            val desiredId = listaPictogramas[posicion].id
            var position = 0

            for ((index, entry) in entryList.withIndex()) {
                val bitmapId = generateBitmapId(entry.key).toString()
                if (bitmapId == desiredId) {
                    position = index
                    break
                }
            }

            if (position == entryList.lastIndex) {
                position = 0
            } else {
                position++
            }

            crearPictoTraduccion(entryList[position].key, listaPictogramas[posicion].titulo, posicion, context)
            adaptador.notifyItemChanged(posicion)

        }catch (e: Exception){
            Log.d("ERROR", e.toString())
        }
    }



}
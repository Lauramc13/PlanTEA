package com.example.plantea.presentacion.actividades.ninio

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.security.MessageDigest


class TraductorActivity : AppCompatActivity(), AdaptadorPictogramasTraductor.OnItemSelectedListener, CommonUtils.TextToSpeechListener{

    var listaPictogramas: ArrayList<Pictograma> = ArrayList()
    private var listaTraducir : ArrayList<String> = ArrayList()
    lateinit var escucharButton : Button
    private var navigationHandler = NavegacionUtils()
    private lateinit var textoATraducir : TextInputLayout
    private lateinit var adaptador: AdaptadorPictogramasTraductor
    private lateinit var recyclerView: RecyclerView
    private var listaPictoBuscador: MutableList<MutableMap<Bitmap, Pair<String, Int>>> = mutableListOf()

    override fun onResume() {
        super.onResume()
        navigationHandler.configurarDatos(this, R.id.traductor)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationHandler.destroyPopup()
    }

    override fun onStop() {
        super.onStop()
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traductor)

        navigationHandler.inicializarVariables(this, R.id.traductor, TraductorActivity::class.java)

        val traducirButton: Button = findViewById(R.id.traducirButton)
        escucharButton = findViewById(R.id.escucharButton)


        val backButton: Button = findViewById(R.id.goBackButton)
        textoATraducir = findViewById(R.id.textoTraducir)
        recyclerView = findViewById(R.id.recycler_plan)
        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        textoATraducir.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
        recyclerView.layoutManager = layoutManagerLinear

        var speechInProgress = false
        CommonUtils.initializeTextToSpeech(this)
        CommonUtils.listener = this


        backButton.setOnClickListener {
            finish()
        }

        traducirButton.setOnClickListener {
            listaTraducir.clear()
            listaPictogramas.clear()
            listaPictoBuscador.clear()
            val texto = textoATraducir.editText?.text?.trim()
            if (!texto.isNullOrBlank()) {
                val words = texto.split("\\s+".toRegex())
                listaTraducir.addAll(words)
                getPictogramas()
                CommonUtils.hideKeyboard(this@TraductorActivity, textoATraducir)
            }
        }

        escucharButton.setOnClickListener {
            if (!speechInProgress) {
                escucharButton.text = getString(R.string.str_parar)
                CommonUtils.textToSpeechOn(listaPictogramas, 1000)
                speechInProgress = true
            } else {
                escucharButton.text = getString(R.string.str_escuchar)
                CommonUtils.textToSpeechOff()
                speechInProgress = false
            }

        }

        textoATraducir.editText?.setOnKeyListener{_, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Simulate button click when "Enter" is pressed
                traducirButton.performClick()
                return@setOnKeyListener true
            }
            false
        }

    }

        private fun getPictogramas() {
        CoroutineScope(Dispatchers.IO).launch {
            for (word in listaTraducir) {
                val dict = CommonUtils.getDataApi(word)

                withContext(Dispatchers.Main) {
                    dict.keys.first().let{ key ->
                        dict[key]?.let {
                            crearPictoTraduccion(key, word, null)
                            mostrarTraduccion()
                        }
                    }
                }
                listaPictoBuscador.add(dict)
            }
        }
    }

    private fun mostrarTraduccion() {
        Handler(Looper.getMainLooper()).post {
            adaptador = AdaptadorPictogramasTraductor(listaPictogramas, this)
            recyclerView.adapter = adaptador
        }
    }

   private fun crearPictoTraduccion(bitmap: Bitmap, titulo: String?, posicion: Int?) {
        val archivo = CommonUtils.crearImagen(bitmap, titulo, this)
        val id = generateBitmapId(bitmap)

        //Si sabemos la posicion del pictograma, estamos cambiando la imagen del pictograma
        if(posicion != null){
            listaPictogramas[posicion] = Pictograma(id, titulo?.uppercase(), archivo, 0, 0, false, true)
        }else {
            listaPictogramas.add(Pictograma(id, titulo?.uppercase(), archivo, 0, 0, false, true))
        }
    }

    //Genero un id unico para cada bitmap
    private fun generateBitmapId(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val md5 = MessageDigest.getInstance("MD5")
        val digest = md5.digest(byteArray)

        return digest.fold("") { str, byte -> str + "%02x".format(byte) }
    }

    override fun onItemSeleccionado(posicion: Int) {
        try {
            val entryList =  listaPictoBuscador[posicion].entries.toList()
            val desiredId = listaPictogramas[posicion].id
            var position = 0

            for ((index, entry) in entryList.withIndex()) {
                val bitmapId = generateBitmapId(entry.key)
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

            crearPictoTraduccion(entryList[position].key, listaPictogramas[posicion].titulo, posicion)
            adaptador.notifyItemChanged(posicion)

        }catch (e: Exception){
            Log.d("ERROR", e.toString())
        }
    }

    override fun onSpeechDone() {
        escucharButton.text = getString(R.string.str_escuchar)
    }

}
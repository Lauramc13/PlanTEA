package com.example.plantea.presentacion.actividades.ninio

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.NavegacionUtils
import com.example.plantea.dominio.Pictograma
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.MainActivity
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity
import com.example.plantea.presentacion.fragmentos.NavigationBottomFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.Locale


class TraductorActivity : AppCompatActivity(), AdaptadorPictogramasTraductor.OnItemSelectedListener, CommonUtils.TextToSpeechListener{

    var listaPictogramas: ArrayList<Pictograma> = ArrayList()
    private var listaTraducir : ArrayList<String> = ArrayList()
    lateinit var escucharButtonPalabra : Button
    lateinit var escucharButtonFrase : Button
    lateinit var guardarButton : Button
    private lateinit var textoATraducir : TextInputLayout
    private lateinit var adaptador: AdaptadorPictogramasTraductor
    private lateinit var recyclerView: RecyclerView
    private var listaPictoBuscador: MutableList<MutableMap<Bitmap, Pair<String, Int>>> = mutableListOf()
    var speechInProgress = false


    override fun onStop() {
        super.onStop()
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traductor)

        val traducirButton: Button = findViewById(R.id.traducirButton)
        escucharButtonPalabra = findViewById(R.id.escucharButtonPalabra)
        escucharButtonFrase = findViewById(R.id.escucharButtonFrase)
        guardarButton = findViewById(R.id.guardarButton)

        textoATraducir = findViewById(R.id.textoTraducir)
        recyclerView = findViewById(R.id.recycler_plan)
        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        textoATraducir.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
        recyclerView.layoutManager = layoutManagerLinear

        CommonUtils.initializeTextToSpeech(this)
        CommonUtils.listener = this

        traducirButton.setOnClickListener {
            listaTraducir.clear()
            listaPictogramas.clear()
            listaPictoBuscador.clear()
            val texto = textoATraducir.editText?.text?.trim()
            if (!texto.isNullOrBlank()) {
                guardarButton.visibility = View.VISIBLE
                val words = texto.split("\\s+".toRegex())
                listaTraducir.addAll(words)
                getPictogramas()
                CommonUtils.hideKeyboard(this@TraductorActivity, textoATraducir)
            }
        }

        escucharButtonPalabra.setOnClickListener {
            if (!speechInProgress) {
                escucharButtonPalabra.text = getString(R.string.str_parar)
                escucharButtonFrase.isEnabled = false
                CommonUtils.textToSpeechOn(listaPictogramas)
                speechInProgress = true
            } else {
                CommonUtils.textToSpeech.stop()
            }
        }

        escucharButtonFrase.setOnClickListener {
            if (!speechInProgress) {
                escucharButtonFrase.text = getString(R.string.str_parar)
                escucharButtonPalabra.isEnabled = false
                CommonUtils.textToSpeechFrase(textoATraducir.editText?.text.toString())
                speechInProgress = true
            } else {
                CommonUtils.textToSpeechOff()
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

        guardarButton.setOnClickListener {
            //dialog of dialogo_olvidar_password
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialogo_crear_planificacion)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val titulo : TextInputLayout = dialog.findViewById(R.id.txt_title)
            val iconoCerrar : ImageView = dialog.findViewById(R.id.icono_CerrarDialogo)
            val btnCrear : Button = dialog.findViewById(R.id.btn_create)

            btnCrear.setOnClickListener{
                val tituloString = titulo.editText?.text.toString()

                //if tituloString is empty -> error
                if(tituloString.isEmpty()){
                    titulo.error = "ESTO ES UN ERROR"
                    Toast.makeText(applicationContext, "No puedes dejar el campo vacío", Toast.LENGTH_LONG).show()
                }else{
                    val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
                    val idUsuario = prefs.getString("idUsuario", "")

                    val plan = Planificacion()
                    val idPlan = idUsuario?.let { it1 ->
                        plan.crearPlanificacion(this@TraductorActivity,  it1, tituloString.uppercase(Locale.getDefault()))
                    }
                    val creada = plan.addPictogramasPlan(idPlan, this@TraductorActivity, listaPictogramas)

                    if (creada == true) {
                        Toast.makeText(
                            applicationContext,
                            "Planificación $tituloString creada",
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error al crear la planificación",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            iconoCerrar.setOnClickListener { dialog.dismiss() }
            dialog.show()
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

    override fun onItemSeleccionado(posicion: Int) {
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

            crearPictoTraduccion(entryList[position].key, listaPictogramas[posicion].titulo, posicion)
            adaptador.notifyItemChanged(posicion)

        }catch (e: Exception){
            Log.d("ERROR", e.toString())
        }
    }

    override fun onSpeechDone() {
        runOnUiThread {
            escucharButtonPalabra.isEnabled = true
            escucharButtonFrase.isEnabled = true
            escucharButtonPalabra.text = getString(R.string.str_escuchar)
            escucharButtonFrase.text = getString(R.string.str_escucharFrase)
            speechInProgress = false
        }
    }
}
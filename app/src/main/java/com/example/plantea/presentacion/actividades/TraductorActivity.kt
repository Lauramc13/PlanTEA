package com.example.plantea.presentacion.actividades

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.example.plantea.presentacion.viewModels.TraductorViewModel
import com.google.android.material.textfield.TextInputLayout


import java.util.UUID


class TraductorActivity : AppCompatActivity(), CommonUtils.TextToSpeechListener{
    private lateinit var escucharButtonPalabra : Button
    private lateinit var escucharButtonFrase : Button
    private lateinit var guardarButton : Button
    //private lateinit var guardarPDFButton : Button
    private lateinit var textoATraducir : TextInputLayout
    private lateinit var recyclerView: RecyclerView
    private var atras : Button? = null

    private val viewModel by viewModels<TraductorViewModel>()

    override fun onStop() {
        super.onStop()
        CommonUtils.handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.textInputContent = textoATraducir.editText?.text.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traductor)

        val traducirButton: Button = findViewById(R.id.traducirButton)
        escucharButtonPalabra = findViewById(R.id.escucharButtonPalabra)
        escucharButtonFrase = findViewById(R.id.escucharButtonFrase)
        guardarButton = findViewById(R.id.guardarButton)
        //guardarPDFButton = findViewById(R.id.guardarButtonPDF)
        textoATraducir = findViewById(R.id.textoTraducir)
        textoATraducir.editText?.setText(viewModel.textInputContent)
        atras = findViewById(R.id.atras)

        if(textoATraducir.editText?.text.toString().isNotEmpty()){
            textoATraducir.requestFocus()
        }


        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val isPlanificador = prefs.getBoolean("PlanificadorLogged", false)

        createPickMedia()

        recyclerView = findViewById(R.id.recycler_plan)

        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        textoATraducir.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
        recyclerView.layoutManager = layoutManagerLinear

        CommonUtils.initializeTextToSpeech(this)
        CommonUtils.listener = this

        traducirButton.setOnClickListener {
            // if there is no internet connection, show a snackbar
            if (!CommonUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.toast_sin_conexion, Toast.LENGTH_SHORT).show()
            }else{
                if(viewModel.traducirFrase(textoATraducir.editText?.text?.trim(), this)) {
                    traducirButton.isEnabled = false
                    viewModel._visibilityButtons.value = true
                    CommonUtils.hideKeyboard(this@TraductorActivity, textoATraducir)
                }else{
                    Toast.makeText(this, R.string.toast_campo_vacio, Toast.LENGTH_SHORT).show()

                }
            }
        }

        atras?.setOnClickListener {
            finish()
        }

        escucharButtonPalabra.setOnClickListener {
            if (!viewModel.speechInProgress) {
                escucharButtonPalabra.text = getString(R.string.str_parar)
                escucharButtonFrase.isEnabled = false
                CommonUtils.textToSpeechOn(viewModel.listaPictogramas)
                viewModel.speechInProgress = true
            } else {
                CommonUtils.textToSpeech.stop()
                escucharButtonPalabra.text = getString(R.string.str_escuchar)
                escucharButtonPalabra.isEnabled = true
                escucharButtonFrase.isEnabled = true
                viewModel.speechInProgress = false
            }
        }

        escucharButtonFrase.setOnClickListener {
            if (!viewModel.speechInProgress) {
                escucharButtonFrase.text = getString(R.string.str_parar)
                escucharButtonPalabra.isEnabled = false
                val frase = textoATraducir.editText?.text.toString()
                CommonUtils.textToSpeech.speak(frase, TextToSpeech.QUEUE_ADD, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)

                viewModel.speechInProgress = true
            } else {
                CommonUtils.textToSpeech.stop()
                escucharButtonFrase.text = getString(R.string.str_escucharFrase)
                escucharButtonFrase.isEnabled = true
                escucharButtonPalabra.isEnabled = true
                viewModel.speechInProgress = false
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

            val inflater = LayoutInflater.from(this)
            val customView = inflater.inflate(R.layout.popup_guardar_traduccion, null)
            val popupWindow = PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popupWindow.width = guardarButton.width

            customView.findViewById<TextView>(R.id.item_planificacion).setOnClickListener {
                if(viewModel.listaPictogramas.isNotEmpty()){
                    viewModel.dialogGuardar(this)
                }else{
                    Toast.makeText(this, R.string.toast_error_guardar_traduccion_vacia, Toast.LENGTH_SHORT).show()
                }
                popupWindow.dismiss()
            }

            customView.findViewById<TextView>(R.id.item_pdf).setOnClickListener {
                if(viewModel.listaPictogramas.isNotEmpty()){
                    viewModel.dialogoTraduccion(this, findViewById(android.R.id.content))
                }else{
                    Toast.makeText(this, R.string.toast_error_guardar_traduccion_vacia, Toast.LENGTH_SHORT).show()
                }
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(guardarButton, 0, -10)

        }


        /////////////  Observers  //////////////

        //Cuando se actualiza la lista de pictogramas, actualizamos el adaptador
        viewModel._listaPictogramas.observe(this) { listaPictogramas ->
            viewModel.adaptador = AdaptadorPictogramasTraductor(listaPictogramas, viewModel)
            recyclerView.adapter = viewModel.adaptador
        }

        //Si hay un mensaje de error o de éxito, lo mostramos a traves de un Snackbar
        viewModel._dialogMessage.observe(this){ message ->
            Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
        }

        //Si hay pictogramas, mostramos los botones
        viewModel._visibilityButtons.observe(this) { visibility ->
            if(visibility){
                escucharButtonPalabra.visibility = View.VISIBLE
                escucharButtonFrase.visibility = View.VISIBLE
                if(isPlanificador){
                    guardarButton.visibility = View.VISIBLE
                //    guardarPDFButton.visibility = View.VISIBLE
                }
            }else{
                escucharButtonPalabra.visibility = View.GONE
                escucharButtonFrase.visibility = View.GONE
                guardarButton.visibility = View.GONE
                //guardarPDFButton.visibility = View.GONE
            }
        }

        viewModel._traduccionEnded.observe(this) {
            traducirButton.isEnabled = true
        }

    }

    override fun onSpeechDone() {
        runOnUiThread {
            escucharButtonPalabra.isEnabled = true
            escucharButtonFrase.isEnabled = true
            escucharButtonPalabra.text = getString(R.string.str_escuchar)
            escucharButtonFrase.text = getString(R.string.str_escucharFrase)
            viewModel.speechInProgress = false
        }
    }

    private fun createPickMedia() {
        viewModel.pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                val nombreFile = viewModel.listaPictogramas[viewModel.posicionSelected].titulo + UUID.randomUUID().toString()
                val inputStream = this.contentResolver?.openInputStream(uri)
                viewModel.bitmap = BitmapFactory.decodeStream(inputStream)

                viewModel.ruta = CommonUtils.guardarImagen(applicationContext, nombreFile, viewModel.bitmap!!)
                viewModel.imageSelected()
            } else {
                Toast.makeText(this, R.string.toast_no_imagen_seleccionada, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
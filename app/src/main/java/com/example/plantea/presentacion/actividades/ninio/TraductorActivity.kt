package com.example.plantea.presentacion.actividades.ninio

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasTraductor
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.viewModels.TraductorViewModel
import com.google.android.material.textfield.TextInputLayout


class TraductorActivity : AppCompatActivity(), CommonUtils.TextToSpeechListener{
    lateinit var escucharButtonPalabra : Button
    lateinit var escucharButtonFrase : Button
    lateinit var guardarButton : Button
    private lateinit var textoATraducir : TextInputLayout
    private lateinit var recyclerView: RecyclerView

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
        textoATraducir = findViewById(R.id.textoTraducir)
        textoATraducir.editText?.setText(viewModel.textInputContent)

        if(textoATraducir.editText?.text.toString().isNotEmpty()){
            textoATraducir.requestFocus()
        }

        recyclerView = findViewById(R.id.recycler_plan)

        val layoutManagerLinear = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        textoATraducir.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
        recyclerView.layoutManager = layoutManagerLinear

        CommonUtils.initializeTextToSpeech(this)
        CommonUtils.listener = this

        traducirButton.setOnClickListener {
            if(viewModel.traducirFrase(textoATraducir.editText?.text?.trim(), this)){
                viewModel._visibilityButtons.value = true
                CommonUtils.hideKeyboard(this@TraductorActivity, textoATraducir)
            }else{
                Toast.makeText(applicationContext, "No puedes dejar el campo vacío", Toast.LENGTH_LONG).show()
            }
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
          viewModel.dialogGuardar(this)
        }

        /////////////  Observers  //////////////

        //Cuando se actualiza la lista de pictogramas, actualizamos el adaptador
        viewModel._listaPictogramas.observe(this) { listaPictogramas ->
            viewModel.adaptador = AdaptadorPictogramasTraductor(listaPictogramas, viewModel)
            recyclerView.adapter = viewModel.adaptador
        }

        //Si hay un mensaje de error o de éxito, lo mostramos a traves de un Toast
        viewModel._dialogMessage.observe(this){ message ->
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }

        //Si hay pictogramas, mostramos los botones
        viewModel._visibilityButtons.observe(this) { visibility ->
            if(visibility){
                escucharButtonPalabra.visibility = View.VISIBLE
                escucharButtonFrase.visibility = View.VISIBLE
                guardarButton.visibility = View.VISIBLE
            }else{
                escucharButtonPalabra.visibility = View.GONE
                escucharButtonFrase.visibility = View.GONE
                guardarButton.visibility = View.GONE
            }
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
}
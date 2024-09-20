package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.viewModels.ConfiguracionPictogramasViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ConfiguracionPictogramasActivity : AppCompatActivity() {

    private val viewModel by viewModels<ConfiguracionPictogramasViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_pictogramas)

        val buttonSiguiente = findViewById<MaterialButton>(R.id.btn_siguiente)

        val cardDefault = findViewById<MaterialCardView>(R.id.cardViewDefault)
        val cardImagen = findViewById<MaterialCardView>(R.id.cardViewImagen)
        val cardTexto = findViewById<MaterialCardView>(R.id.cardViewTexto)

        val radioButtonDefault = findViewById<RadioButton>(R.id.radioButtonDefault)
        val radioButtonImagen = findViewById<RadioButton>(R.id.radioButtonImagen)
        val radioButtonTexto = findViewById<RadioButton>(R.id.radioButtonTexto)

        radioButtonDefault.isClickable = false
        radioButtonImagen.isClickable = false
        radioButtonTexto.isClickable = false

        cardDefault.setOnClickListener {
            changeSelected(cardDefault, radioButtonDefault)
        }

        cardImagen.setOnClickListener {
            changeSelected(cardImagen, radioButtonImagen)
        }

        cardTexto.setOnClickListener {
            changeSelected(cardTexto, radioButtonTexto)
        }

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)


        var isFromConfig = false
        if(intent.extras != null){
             isFromConfig = intent.extras?.getBoolean("editPreferences") == true
            if(isFromConfig){
                buttonSiguiente.text = getString(R.string.btn_Guardar)
            }
            prefs.getString("configPictogramas", "")?.let { config ->
                when(config){
                    "default" -> changeSelected(cardDefault, radioButtonDefault)
                    "imagen" -> changeSelected(cardImagen, radioButtonImagen)
                    "texto" -> changeSelected(cardTexto, radioButtonTexto)
                    else -> changeSelected(cardDefault, radioButtonDefault)
                }
            }
        }

        buttonSiguiente.setOnClickListener {
            val configPictogramas = configPictogramas()
            val usuario = Usuario()
            val idUsuario = prefs.getString("idUsuario", "")
            prefs.edit().putString("configPictogramas", configPictogramas).apply()
            usuario.cambiarConfiguracionPictogramas(configPictogramas, idUsuario, this)

            if(isFromConfig){
                finish()
            }else{
                val intent = Intent(this, TutorialActivity::class.java)
                intent.putExtra("isFromManual", false)
                startActivity(intent)
            }
        }



        if(viewModel.lastSelectedCardId != null)
            changeSelected(findViewById(viewModel.lastSelectedCardId!!), findViewById(viewModel.lastSelectedRadioButtonId!!))
        else
            changeSelected(cardDefault, radioButtonDefault)
    }

    private fun configPictogramas(): String{
        //when
        when (viewModel.lastSelectedCardId) {
            R.id.cardViewDefault -> return "default"
            R.id.cardViewImagen -> return "imagen"
            R.id.cardViewTexto -> return "texto"
        }
        return "default"
    }

    private fun changeSelected(card: MaterialCardView, radioButton: RadioButton){
        viewModel.lastSelectedCardId?.let { id ->
            val lastCard = findViewById<MaterialCardView>(id)
            if(CommonUtils.isDarkMode(this))
                lastCard.strokeColor = ContextCompat.getColor(this, R.color.md_theme_dark_background2)
            else
                lastCard.strokeColor = ContextCompat.getColor(this, R.color.darkerGray)
        }

        viewModel.lastSelectedRadioButtonId?.let { id ->
            val lastRadioButton = findViewById<RadioButton>(id)
            lastRadioButton.isChecked = false
        }

        card.strokeColor = ContextCompat.getColor(this, R.color.seedLight)
        radioButton.isChecked = true

        viewModel.lastSelectedCardId = card.id
        viewModel.lastSelectedRadioButtonId = radioButton.id
    }

}
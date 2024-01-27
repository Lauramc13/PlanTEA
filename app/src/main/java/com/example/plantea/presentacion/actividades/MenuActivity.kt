package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R

class MenuActivity : AppCompatActivity() {
    private lateinit var layoutEmociones : LinearLayout

    override fun onResume(){
        super.onResume()
        configurarOpciones()
    }

    private fun configurarOpciones(){
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val infoUsuario = prefs.getBoolean("info_usuario", false)
        if (infoUsuario) {
            layoutEmociones.visibility = View.GONE
        }else{
            layoutEmociones.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val cardCalendario: CardView = findViewById(R.id.card_Calendario)
        val cardEmociones: CardView = findViewById(R.id.card_Emociones)
        val cardPlanificacion: CardView = findViewById(R.id.card_Planificacion)
        layoutEmociones = findViewById(R.id.layout_Emociones)

        configurarOpciones()

        //Activamos icono volver atrás
        cardCalendario.setOnClickListener {
            val intent = Intent(applicationContext, CalendarioActivity::class.java)
            startActivity(intent)
        }
        cardEmociones.setOnClickListener {
            val intent = Intent(applicationContext, CuadernoActivity::class.java)
            startActivity(intent)
        }
        cardPlanificacion.setOnClickListener {
            val intent = Intent(applicationContext, PlanActivity::class.java)
            startActivity(intent)
        }
    }

}
package com.example.plantea.presentacion.actividades

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.ninio.CuadernoActivity
import com.example.plantea.presentacion.actividades.ninio.PlanActivity
import com.example.plantea.presentacion.actividades.planificador.CalendarioActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var cardCalendario: CardView
    private lateinit var cardEmociones: CardView
    private lateinit var cardPlanificacion: CardView
    lateinit var btn_logout: Button
    lateinit var icono_cerrar_login : AppCompatImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        cardCalendario = findViewById(R.id.card_Calendario)
        cardEmociones = findViewById(R.id.card_Emociones)
        cardPlanificacion = findViewById(R.id.card_Planificacion)

        //Activamos icono volver atrás
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_ayuda, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_ayuda -> {
                val manual = Intent(applicationContext, ManualActivity::class.java)
                startActivity(manual)
            }
            android.R.id.home -> {
                val it = Intent(applicationContext, MainActivity::class.java)
                startActivity(it)
            }
        }
        return true
    }
}
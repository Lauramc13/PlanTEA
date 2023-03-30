package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R

class RegisterActivity : AppCompatActivity(){

    private lateinit var btnRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btnRegister = findViewById(R.id.btn_register)

        btnRegister.setOnClickListener {
            val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
            val accesoConfiguracion = prefs.getBoolean("configuracion", false)
            Handler().postDelayed({
                if (accesoConfiguracion) {
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@RegisterActivity, ConfiguracionActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }, 2000)
        }
    }


}
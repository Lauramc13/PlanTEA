package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Preferencias
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val accesoConfiguracion = prefs.getBoolean("userAccount", false)
        Handler(Looper.getMainLooper()).postDelayed({
            if (accesoConfiguracion) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@SplashActivity, PreLoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 2000) // TODO: QUITAR EL SPLASH ACTIVITY
    }
}
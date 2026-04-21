package com.example.plantea.presentacion.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.plantea.R

/**
 * Actividad que muestra los créditos de la aplicación.
 * En esta actividad se muestran los enlaces a las páginas de los recursos utilizados en la aplicación.
 */

class CreditsActivity : AppCompatActivity(){

    override fun finish() {
        super.finish()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_left, R.anim.slide_out_right)
        }else{
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !prefs.getBoolean("darkMode", false)

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val arasaacLink : TextView = findViewById(R.id.arasaacLink)
        val pixelsLink : TextView = findViewById(R.id.pixelsLink)
        val heroIconsLinK: TextView = findViewById(R.id.heroiconsLink)

        arasaacLink.paintFlags = arasaacLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        arasaacLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://arasaac.org/terms-of-use"))
            startActivity(intent)
        }

        pixelsLink.paintFlags = pixelsLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        pixelsLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pixels.market/license#free-design"))
            startActivity(intent)
        }

        heroIconsLinK.paintFlags = heroIconsLinK.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        heroIconsLinK.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://heroicons.com/"))
            startActivity(intent)
        }
    }
}
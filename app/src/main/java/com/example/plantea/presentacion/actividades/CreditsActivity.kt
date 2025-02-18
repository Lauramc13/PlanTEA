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

        if(CommonUtils.isMobile(this)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val arasaacLink : TextView = findViewById(R.id.arasaacLink)
        val manyLink : TextView = findViewById(R.id.manypixelsLink)
        val flaticonLink : TextView = findViewById(R.id.flatIconLink)
        val freepikLink : TextView = findViewById(R.id.freepikLink)
        val svgRepoLink: TextView = findViewById(R.id.svgRepoLink)

        arasaacLink.paintFlags = arasaacLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        arasaacLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://arasaac.org/terms-of-use"))
            startActivity(intent)
        }

        manyLink.paintFlags = manyLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        manyLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.manypixels.co/terms-of-service"))
            startActivity(intent)
        }

        flaticonLink.paintFlags = flaticonLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        flaticonLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freepikcompany.com/legal#nav-flaticon"))
            startActivity(intent)
        }

        freepikLink.paintFlags = freepikLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        freepikLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freepikcompany.com/legal#nav-freepik"))
            startActivity(intent)
        }

        svgRepoLink.paintFlags = svgRepoLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        svgRepoLink.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.svgrepo.com/page/licensing/"))
            startActivity(intent)
        }
    }
}
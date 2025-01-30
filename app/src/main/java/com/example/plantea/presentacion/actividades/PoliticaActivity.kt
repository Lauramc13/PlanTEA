package com.example.plantea.presentacion.actividades

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R

class PoliticaActivity: AppCompatActivity() {

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_politica)
    }
}
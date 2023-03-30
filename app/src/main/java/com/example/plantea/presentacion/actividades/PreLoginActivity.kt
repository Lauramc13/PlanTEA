package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.ninio.PlanActivity

class PreLoginActivity : AppCompatActivity(){

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prelogin)

        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_registrar)

        btnLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        })

        btnRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        })

    }
}
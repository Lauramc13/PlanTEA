package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.plantea.R
import com.example.plantea.presentacion.actividades.ninio.PlanActivity

class PreLoginActivity : AppCompatActivity(){

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var username: EditText
    private lateinit var password: EditText

    override fun onBackPressed() {
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val userAccount = prefs.getBoolean("userAccount", false)

        if(userAccount){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val userAccount = prefs.getBoolean("userAccount", false)

        if(userAccount){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        setContentView(R.layout.activity_prelogin)
        username = findViewById(R.id.txt_UserName)
        password = findViewById(R.id.txt_Password)

        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_registrar)

        btnLogin.setOnClickListener {
            if (username.text.toString() == "" || password.text.toString() == "") {
                Toast.makeText(
                    applicationContext,
                    "Tienes que rellenar todos los campos",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val user_username = prefs.getString("username", "")
                val user_password = prefs.getString("password", "")
                if (user_username == username.text.toString() && user_password == password.text.toString()) {
                    val editor = prefs.edit()
                    editor.putBoolean("userAccount", true)
                    editor.commit()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Las credenciales son incorrectas", Toast.LENGTH_LONG).show()
                }
            }

        }

        btnRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        })

    }
}
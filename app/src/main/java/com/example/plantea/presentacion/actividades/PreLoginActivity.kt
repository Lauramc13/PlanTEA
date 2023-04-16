package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador

class PreLoginActivity : AppCompatActivity(){

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var username: EditText
    private lateinit var password: EditText

    var usuario = Usuario_Planificador()
    var user = Usuario_Planificador()


    @Deprecated("Deprecated in Java")
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
                if (usuario.comprobarUsuario(username.text.toString(), password.text.toString(), this@PreLoginActivity) == true) {
                    user = usuario.obtenerUsuario(username.text.toString(), this@PreLoginActivity)
                    val editor = prefs.edit()
                    editor.putBoolean("userAccount", true)
                    editor.putString("nombrePlanificador", user.getName())
                    editor.putString("username", user.getUsername())
                    editor.putString("nombreUsuarioTEA", user.getName())
                    editor.putString("imagenPlanificado", user.getImagen())
                    editor.putString("imagenUsuarioTEA", user.getImagen())
                    editor.putString("nombreObjeto", user.getObjeto())
                    editor.apply()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Las credenciales son incorrectas", Toast.LENGTH_LONG).show()
                }
            }

        }

        btnRegister.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}
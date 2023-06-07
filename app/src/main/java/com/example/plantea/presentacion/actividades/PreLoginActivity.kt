package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import com.google.android.material.textfield.TextInputLayout
import java.security.MessageDigest

class PreLoginActivity : AppCompatActivity(){

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var username: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var background: ImageView

    var usuario = Usuario_Planificador()
    var user = Usuario_Planificador()


    //@Deprecated("Deprecated in Java")
    //override fun onBackPressed() {
      //  val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
      //  val userAccount = prefs.getBoolean("userAccount", false)

      //  if(userAccount){
        //    val intent = Intent(applicationContext, MainActivity::class.java)
          //  startActivity(intent)
       // }

        //super.onSupportNavigateUp()
    // }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        val userAccount = prefs.getBoolean("userAccount", false)

     //   if(userAccount){
           // val intent = Intent(applicationContext, MainActivity::class.java)
         //   startActivity(intent)
       // }



        setContentView(R.layout.activity_prelogin)
        background = findViewById(R.id.imageView6)

        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode) {
            background.setImageResource(R.drawable.backgroundlogindark)
        } else {
            background.setImageResource(R.drawable.backgroundlogin)
        }


        username = findViewById(R.id.txt_UserName)
        password = findViewById(R.id.txt_Password)

        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_registrar)

        btnLogin.setOnClickListener {
            if (username.editText?.text.toString() == "" || password.editText?.text.toString() == "") {
                username.error = "ESTO ES UN ERROR"
                password.error = "ESTO ES UN ERROR"
                Toast.makeText(
                    applicationContext,
                    "Tienes que rellenar todos los campos",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val passCifrada = hashPassword(password.editText?.text.toString())
                if (usuario.comprobarUsuario(username.editText?.text.toString(), passCifrada, this@PreLoginActivity) == true) {
                    user = usuario.obtenerUsuario(username.editText?.text.toString(), this@PreLoginActivity)
                    val id = usuario.consultarId(username.editText?.text.toString(), this@PreLoginActivity)
                    val editor = prefs.edit()
                    editor.putString("idUsuario", id)
                    Log.d("USUARIO", "$id")
                    editor.putBoolean("userAccount", true)
                    editor.putString("nombrePlanificador", user.getName())
                    editor.putString("username", user.getUsername())
                    editor.putString("nombreUsuarioTEA", user.getNameTEA())
                    if (user.getNameTEA() != "") {
                        editor.putBoolean("info_usuario", true)
                    }
                    editor.putString("imagenPlanificador", user.getImagen())
                    editor.putString("imagenUsuarioTEA", user.getImagenTEA())
                    editor.putString("nombreObjeto", user.getObjeto())
                    if(user.getObjeto() != "") {
                        editor.putBoolean("info_objeto", true)
                    }
                    editor.putString("imagenObjeto", user.getImagenObjeto())
                    editor.apply()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    username.error = "ESTO ES UN ERROR"
                    password.error = "ESTO ES UN ERROR"
                    Toast.makeText(applicationContext, "Las credenciales son incorrectas", Toast.LENGTH_LONG).show()
                }
            }

        }

        btnRegister.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}
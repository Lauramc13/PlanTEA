package com.example.plantea.presentacion.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import java.security.MessageDigest

class PreLoginActivity : AppCompatActivity(){

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var username: EditText
    private lateinit var password: EditText

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
                val passCifrada = hashPassword(password.text.toString())
                if (usuario.comprobarUsuario(username.text.toString(), passCifrada, this@PreLoginActivity) == true) {
                    user = usuario.obtenerUsuario(username.text.toString(), this@PreLoginActivity)
                    val id = usuario.consultarId(username.text.toString(), this@PreLoginActivity)
                    val editor = prefs.edit()
                    editor.putString("idUsuario", id)
                    Log.d("USUARIO", "$id")
                    editor.putBoolean("userAccount", true)
                    editor.putString("nombrePlanificador", user.getName())
                    editor.putString("username", user.getUsername())
                    editor.putString("nombreUsuarioTEA", user.getName())
                    editor.putString("imagenPlanificador", user.getImagen())
                    editor.putString("imagenUsuarioTEA", user.getImagenTEA())
                    editor.putString("nombreObjeto", user.getObjeto())
                    editor.putString("imagenObjeto", user.getImagenObjeto())
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
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}
package com.example.plantea.presentacion.actividades

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.plantea.R
import com.example.plantea.dominio.Usuario_Planificador
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest

class PreLoginActivity : AppCompatActivity(){

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var background: ImageView
    private lateinit var Signin : Button
    private lateinit var prefs : SharedPreferences

    var usuario = Usuario_Planificador()
    var user = Usuario_Planificador()

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("Preferencias", MODE_PRIVATE)
        setContentView(R.layout.activity_prelogin)
        background = findViewById(R.id.imageView6)
        Signin = findViewById(R.id.Signin)

        FirebaseApp.initializeApp(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id_google))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        Signin.setOnClickListener {
            Toast.makeText(this, "Iniciando sesión", Toast.LENGTH_SHORT).show()
            signInGoogle()
        }

        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode) {
            background.setImageResource(R.drawable.backgroundlogindark)
        } else {
            background.setImageResource(R.drawable.backgroundlogin)
        }

        email = findViewById(R.id.txt_Email)
        password = findViewById(R.id.txt_Password)

        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_registrar)
        btnRegister.paintFlags = btnRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        btnLogin.setOnClickListener {
            email.error = null
            password.error = null
            
            val emptyTextViews = mutableListOf<TextView>()

            if (email.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(email.editText!!)
                email.error = "ESTO ES UN ERROR"
            }

            if (password.editText?.text.toString().isEmpty()) {
                emptyTextViews.add(password.editText!!)
                password.error = "ESTO ES UN ERROR"
            }

            if (emptyTextViews.isNotEmpty()) {
                Toast.makeText(applicationContext, "Tienes que rellenar todos los campos", Toast.LENGTH_LONG).show()
            } else {
                val passCifrada = hashPassword(password.editText?.text.toString())
                if (usuario.comprobarUsuario(email.editText?.text.toString(), passCifrada, this@PreLoginActivity) == true) {
                   configurarDatos(email.editText?.text.toString())
                } else {
                    email.error = "ESTO ES UN ERROR"
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

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val id = usuario.consultarId(account?.email.toString(), this@PreLoginActivity)
                if (id != null){
                    configurarDatos(account?.email.toString())
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }else{
                    //IR A REGISTER PERO CON ALGUNOS DATOS YA COMPLETOS
                    val intent = Intent(applicationContext, RegisterActivity::class.java)
                    intent.putExtra("IS_GOOGLE_USER", true)
                    intent.putExtra("EMAIL", account?.email.toString())
                    intent.putExtra("NAME", account?.givenName.toString())
                    startActivity(intent)
                    finish()
                }
            } catch (e: ApiException) {
                Log.w("FAILURE", "Google Sign-In Failed: ${e.statusCode}")
            }
        }
    }

    /*
    //ESTO ES PARA COMPROBAR SI YA SE HA INICIADO SESION CON GOOGLE AL ABRIR LA APLICACION
    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }*/

    fun configurarDatos(email: String){
        user = usuario.obtenerUsuario(email, this@PreLoginActivity)
        val id = usuario.consultarId(email, this@PreLoginActivity)
        val editor = prefs.edit()
        editor.putString("idUsuario", id)
        editor.putBoolean("userAccount", true)
        editor.putString("nombrePlanificador", user.getName())
        editor.putString("email", user.getEmail())
        editor.putString("nombreUsuarioTEA", user.getNameTEA())
       //  if(user.getPassword() == ""){
       //      editor.putBoolean("isGoogleUser", true)
       //  }
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
        finish()

    }
}
package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.actividades.EncryptionUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class PreLoginViewModel: ViewModel() {
    var usuario = Usuario()
    lateinit var mGoogleSignInClient: GoogleSignInClient
   // private lateinit var firebaseAuth: FirebaseAuth

    var email =""
    var password = ""

    val _errorEmail = SingleLiveEvent<String>()
    val _errorPassword = SingleLiveEvent<String>()

    fun initGoogleSignInClient(context: Context) {
        //auth = Firebase.auth

      //  FirebaseApp.initializeApp(context)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(context, R.string.client_id_google))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        //firebaseAuth = FirebaseAuth.getInstance()
    }

    fun comprobarTextViewsVacios(emailText: String, passwordText: String): Boolean {
        var textViewsFilled = true
        if (emailText.isEmpty()) {
            _errorEmail.value = "No puedes dejar el campo vacío"
            textViewsFilled = false
        }

        if (passwordText.isEmpty()) {
            _errorPassword.value = "No puedes dejar el campo vacío"
            textViewsFilled = false
        }
        return textViewsFilled
    }

    fun iniciarSesion(activity: Activity, context: Context, emailText: String, passwordText: String): Boolean {
        val passwordCifrada = EncryptionUtils.getEncrypt(passwordText, context)
        return usuario.checkCredentials(emailText, passwordCifrada, activity)
    }


    fun configurarDatos(email: String, prefs: SharedPreferences, activity: Activity){
        val user = usuario.obtenerUsuario(email, activity)
        val id = usuario.consultarId(email, activity)
        val editor = prefs.edit()
        editor.putString("idUsuario", id)
        editor.putBoolean("userAccount", true)
        editor.putString("nombrePlanificador", user.getName())
        editor.putString("nombreUsuarioPlanificador", user.getUsername())
        editor.putString("email", user.getEmail())
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
    }
}
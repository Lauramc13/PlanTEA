package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PreLoginViewModel: ViewModel() {
    var usuario = Usuario()
    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    var email =""
    var password = ""

    val _errorEmail = SingleLiveEvent<String>()
    val _errorPassword = SingleLiveEvent<String>()

    fun initGoogleSignInClient(context: Context) {
        auth = Firebase.auth

        FirebaseApp.initializeApp(context)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(context, R.string.client_id_google))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun comprobarTextViewsVacios(emailText: String, passwordText: String): Boolean {
        if (emailText.isEmpty()) {
            _errorEmail.value = "No puedes dejar el campo vacío"
            return false
        }

        if (passwordText.isEmpty()) {
            _errorPassword.value = "No puedes dejar el campo vacío"
            return false
        }
        return true
    }

    fun iniciarSesion(activity: Activity, emailText: String, passwordText: String, callback: (Boolean) -> Unit = { _ -> }) {
        auth.signInWithEmailAndPassword(emailText, passwordText)
        .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                Log.d("pruebas", "signInWithEmail:success")
                callback(true)
            } else {
                Log.w("pruebas", "signInWithEmail:failure", task.exception)
                callback(false)
            }
        }
    }

    fun restablecerContrasesnia(): Boolean{
        var suscessful = false
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener{task ->
                suscessful = if(task.isSuccessful){
                    true
                }else{
                    true
                }
            }
        return suscessful
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
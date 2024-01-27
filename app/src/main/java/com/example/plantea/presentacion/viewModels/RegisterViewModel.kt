package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel: ViewModel() {
    var isClicked = true
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var name = ""
    var email = ""
    var username = ""
    var password = ""
    var password2 = ""
    var objeto = ""
    var namePlanificado = ""

    fun updateButtonIcon(context: Context): Drawable?{
        // Actualizar el icono del botón segun el estado
        val iconResource = if (isClicked) R.drawable.question_simple else R.drawable.svg_close
        return ContextCompat.getDrawable(context, iconResource)
    }

    fun accountCreated(activity: Activity, prefs: SharedPreferences, isCheckedUsuarioPlan: Boolean, isCheckedObjeto: Boolean){
        val usuario = Usuario()
        usuario.crearUsuario(name, email, username, objeto, namePlanificado, activity)
        val id = usuario.consultarId(email, activity)
        val editor = prefs.edit()
        editor.putString("idUsuario", id)
        editor.putString("nombreUsuarioPlanificador", username)
        editor.putBoolean("info_usuario", isCheckedUsuarioPlan)
        editor.putBoolean("info_objeto", isCheckedObjeto)
        editor.putString("email", email)
        editor.putString("nombrePlanificador", name)
        editor.putString("nombreUsuarioTEA", namePlanificado)
        editor.putString("nombreObjeto", objeto)
        editor.putBoolean("editPreferences", false)
        editor.apply()
    }

    fun createAccountGoogle(): Boolean{
        auth.currentUser
        var suscess = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                suscess = task.isSuccessful
            }
        return suscess

    }
}
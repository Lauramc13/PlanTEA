package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.actividades.EncryptionUtils

class RegisterViewModel: ViewModel() {
    var isClicked = true
    var name = ""
    var email = ""
    var username = ""
    var password = ""
    var password2 = ""
    var objeto = ""
    var namePlanificado = ""

    val _accountCreated = SingleLiveEvent<Boolean>()

    fun updateButtonIcon(context: Context): Drawable?{
        // Actualizar el icono del botón segun el estado
        val iconResource = if (isClicked) R.drawable.question_simple else R.drawable.svg_close
        return ContextCompat.getDrawable(context, iconResource)
    }

    //Comprobar si el email es valido
    fun accountCreated(activity: Activity, prefs: SharedPreferences, isCheckedUsuarioPlan: Boolean, isCheckedObjeto: Boolean){
        val usuario = Usuario()
        val passwordCifrada = EncryptionUtils.encrypt(password, activity.applicationContext)
        usuario.crearUsuario(name, email, passwordCifrada, username, objeto, namePlanificado, activity)
        val id = usuario.consultarId(email, activity)
        val editor = prefs.edit()
        editor.putString("idUsuario", id)
        editor.putBoolean("userAccount", true)
        editor.putString("nombreUsuarioPlanificador", username)
        editor.putBoolean("info_usuario", isCheckedUsuarioPlan)
        editor.putBoolean("info_objeto", isCheckedObjeto)
        editor.putString("email", email)
        editor.putString("nombrePlanificador", name)
        editor.putString("nombreUsuarioTEA", namePlanificado)
        editor.putString("nombreObjeto", objeto)
        editor.apply()
        _accountCreated.value = true
    }

   /* fun registerUser(chekedObjeto: Boolean, checkedUserTEA: Boolean, prefs: SharedPreferences, activity: Activity) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    accountCreated(activity, prefs, checkedUserTEA, chekedObjeto)
                }else{
                   _accountCreated.value = false
                }
            }
    }*/


}
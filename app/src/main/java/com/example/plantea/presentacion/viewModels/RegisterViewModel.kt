package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.Categoria
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
        val validUser = usuario.crearUsuario(name, email, username, activity)

        if(validUser){
            val id = usuario.consultarId(email, activity).toString()
            val passwordCifrada = EncryptionUtils.encrypt(password, id, activity.applicationContext)
            usuario.actualizarPass(id, passwordCifrada, activity)

            // crear categorias por defecto
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
        }else{
            val error = activity.getString(R.string.error_crear_cuenta)
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
        }
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
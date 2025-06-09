package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionUsuarios
import com.example.plantea.dominio.objetos.Usuario
import com.example.plantea.presentacion.actividades.EncryptionUtils

class RegisterViewModel: ViewModel() {
    var name = ""
    var email = ""
    var password = ""
    var password2 = ""
    var objeto = ""
    var namePlanificado = ""

    val seAccountCreated = SingleLiveEvent<Boolean>()

    //Comprobar si el email es valido
    fun accountCreated(activity: Activity, prefs: SharedPreferences, isCheckedUsuarioPlan: Boolean, isCheckedObjeto: Boolean){
        val gUsuario = GestionUsuarios()
        val validUser = gUsuario.crearUsuario(name, email, activity)

        if(validUser){
            val id = gUsuario.consultarId(email, activity).toString()
            val passwordCifrada = EncryptionUtils.encrypt(password, id, activity.applicationContext)
            gUsuario.actualizarPass(id, passwordCifrada, activity)

            // crear categorias por defecto
            val editor = prefs.edit()
            editor.putString("idUsuario", id)
            editor.putBoolean("userAccount", true)
            editor.putBoolean("info_usuario", isCheckedUsuarioPlan)
            editor.putBoolean("info_objeto", isCheckedObjeto)
            editor.putString("email", email)
            editor.putString("nombrePlanificador", name)
            editor.putString("nombreUsuarioTEA", namePlanificado)
            editor.putString("nombreObjeto", objeto)
            editor.apply()
            seAccountCreated.value = true
        }else{
            val error = activity.getString(R.string.error_crear_cuenta)
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.plantea.presentacion.viewModels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.plantea.dominio.Usuario
import com.example.plantea.presentacion.actividades.EncryptionUtils

class PasswordViewModel: ViewModel(){
    var usuario = Usuario()

    var viejaPass =""
    var nuevaPass = ""
    var confirmaPass = ""

    fun currentPasswordCorrect(activity: Activity, context: Context, emailText: String, passwordText: String, idUsuario: String): Boolean {
        val passwordCifrada = EncryptionUtils.getEncrypt(passwordText, context, idUsuario)
        return usuario.checkCredentials(emailText, passwordCifrada, activity)
    }

    fun actualizarPassword(idUsuario: String, passwordNueva: String, actividad: Activity){
        val passwordCifrada = EncryptionUtils.getEncrypt(passwordNueva, actividad.applicationContext, idUsuario)
        usuario.actualizarPass(idUsuario, passwordCifrada, actividad)
    }
}
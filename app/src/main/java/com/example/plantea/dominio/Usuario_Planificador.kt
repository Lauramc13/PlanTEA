package com.example.plantea.dominio

import android.app.Activity

class Usuario_Planificador {
    private var password: String? = null
    private var gestorUsuario = GestionUsuarios()
    private var resultado: Boolean? = null

    constructor()
    constructor(pass: String?) {
        password = pass
    }

    fun crearUsuario(name:String?, username: String?, password: String?, actividad: Activity?): Boolean {
        resultado = gestorUsuario.crearUsuario(name, username, password, actividad)
        return resultado!!
    }

    fun comprobarPass(password: String, actividad: Activity?): Boolean {
        resultado = gestorUsuario.comprobarPassword(password, actividad)
        return resultado!!
    }

    fun confirmarPass(passwordVieja: String, passwordNueva: String, passwordConfirma: String, actividad: Activity?): Boolean {
        resultado = if (passwordNueva == passwordConfirma) {
            gestorUsuario.cambiarPassword(passwordNueva, passwordVieja, actividad)
        } else {
            false
        }
        return resultado!!
    }
}
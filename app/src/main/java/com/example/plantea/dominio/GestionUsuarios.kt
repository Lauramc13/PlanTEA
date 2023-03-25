package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD

class GestionUsuarios {
    private var conectorBD: ConectorBD? = null
    private var resultado = false
    fun crearPassword(password: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.insertarPass(password)
        conectorBD!!.cerrar()
        return resultado
    }

    fun comprobarPassword(password: String, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.consultarPass(password)
        conectorBD!!.cerrar()
        return resultado
    }

    fun cambiarPassword(passwordNueva: String, passwordVieja: String, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        resultado = conectorBD!!.actualizarPass(passwordNueva, passwordVieja)
        conectorBD!!.cerrar()
        return resultado
    }
}
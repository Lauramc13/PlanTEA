package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD

class GestionActividades {

    private var conectorBD: ConectorBD? = null


    fun crearActividad(name: String?, imagen: String?, idUsuario: String?, actividad: Activity?): String? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.insertarActividad(name, imagen, idUsuario)
        conectorBD!!.cerrar()
        return resultado
    }

    fun borrarActividad(idActividad: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.borrarActividad(idActividad)
        conectorBD!!.cerrar()
        return resultado
    }

    fun actualizarActividad(idActividad: String?, name: String?, imagen: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.actualizarActividad(idActividad, name, imagen)
        conectorBD!!.cerrar()
        return resultado
    }

    fun getActividades(idUsuario: String?, actividad: Activity?): ArrayList<Actividad>? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val actividades = conectorBD!!.getActividades(idUsuario)
        conectorBD!!.cerrar()
        return actividades
    }
}

package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD

class GestionActividades {

    private var conectorBD: ConectorBD? = null


    fun crearActividad(name: String?, imagen: ByteArray?, idUsuario: String?, actividad: Activity?): String? {
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

    fun actualizarActividad(idActividad: String?, name: String?, imagen: ByteArray?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.actualizarActividad(idActividad, name, imagen)
        conectorBD!!.cerrar()
        return resultado
    }

    fun addCategoria(idActividad: String?, idCategoria: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.addCategoriaActividad(idActividad, idCategoria)
        conectorBD!!.cerrar()
        return resultado
    }

    fun removeCategoria(idActividad: String?, idCategoria: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.removeCategoriaActividad(idActividad, idCategoria)
        conectorBD!!.cerrar()
        return resultado
    }

    fun getActividades(idUsuario: String?, idCategoria: String?, actividad: Activity?): ArrayList<Actividad>? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val actividades = conectorBD!!.getActividades(idUsuario, idCategoria)
        conectorBD!!.cerrar()
        return actividades
    }

    fun getAllActividades(idUsuario: String?, actividad: Activity?): ArrayList<Actividad>? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val actividades = conectorBD!!.getAllActividades(idUsuario)
        conectorBD!!.cerrar()
        return actividades
    }
}

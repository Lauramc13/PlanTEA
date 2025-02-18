package com.example.plantea.dominio

import android.app.Activity
import android.graphics.Bitmap

class Actividad {
    var id: String? = null
    var name: String? = null
    var imagen: Bitmap? = null
    var idUsuario: String? = null
    var idCategoria : ArrayList<String>? = null

    private var gestorActividad = GestionActividades()

    constructor()

    constructor(idActividad : String?, name: String?, imagen: Bitmap?,  idCategoria: ArrayList<String>?, idUsuario: String?) {
        this.id = idActividad
        this.name = name
        this.imagen = imagen
        this.idUsuario = idUsuario
        this.idCategoria = idCategoria
    }

    fun crearActividad(name: String?, imagen: ByteArray?, idUsuario: String?, actividad: Activity?): String? {
        return gestorActividad.crearActividad(name, imagen, idUsuario, actividad)
    }

    fun borrarActividad(idActividad: String?, actividad: Activity?): Boolean {
        return gestorActividad.borrarActividad(idActividad, actividad)
    }

    fun actualizarActividad(idActividad: String?, name: String?, imagen: ByteArray?, actividad: Activity?): Boolean {
        return gestorActividad.actualizarActividad(idActividad, name, imagen, actividad)
    }

    fun addCategoria(idActividad: String?, idCategoria: String?, actividad: Activity?): Boolean {
        return gestorActividad.addCategoria(idActividad, idCategoria, actividad)
    }

    fun removeCategoria(idActividad: String?, idCategoria: String?, actividad: Activity?): Boolean {
        return gestorActividad.removeCategoria(idActividad, idCategoria, actividad)
    }

    fun getActividades(idUsuario: String?, idCategoria: String?, actividad: Activity?): ArrayList<Actividad>? {
        return gestorActividad.getActividades(idUsuario, idCategoria, actividad)
    }

    fun getAllActividades(idUsuario: String?, actividad: Activity?): ArrayList<Actividad>? {
        return gestorActividad.getAllActividades(idUsuario, actividad)
    }
}
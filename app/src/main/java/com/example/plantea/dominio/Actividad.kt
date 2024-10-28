package com.example.plantea.dominio

import android.app.Activity

class Actividad {
    var id: String? = null
    var name: String? = null
    var imagen: String? = null
    var idUsuario: String? = null

    private var gestorActividad = GestionActividades()

    constructor()

    constructor(idActividad : String?, name: String?, imagen: String?, idUsuario: String?) {
        this.id = idActividad
        this.name = name
        this.imagen = imagen
        this.idUsuario = idUsuario
    }

    fun crearActividad(name: String?, imagen: String?, idUsuario: String?, actividad: Activity?): String? {
        return gestorActividad.crearActividad(name, imagen, idUsuario, actividad)
    }

    fun borrarActividad(idActividad: String?, actividad: Activity?): Boolean {
        return gestorActividad.borrarActividad(idActividad, actividad)
    }

    fun actualizarActividad(idActividad: String?, name: String?, imagen: String?, actividad: Activity?): Boolean {
        return gestorActividad.actualizarActividad(idActividad, name, imagen, actividad)
    }

    fun getActividades(idUsuario: String?, actividad: Activity?): ArrayList<Actividad>? {
        return gestorActividad.getActividades(idUsuario, actividad)
    }


}
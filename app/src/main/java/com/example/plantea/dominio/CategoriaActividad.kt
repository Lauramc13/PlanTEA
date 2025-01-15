package com.example.plantea.dominio

import android.app.Activity

class CategoriaActividad {
    var id: String? = null
    var name: String? = null
    var idUsuario: String? = null

    private var gestorCategoriaActividad = GestionCategoriasActividad()

    constructor()

    constructor(idCategoria : String?, name: String?, idUsuario: String?) {
        this.id = idCategoria
        this.name = name
        this.idUsuario = idUsuario
    }

    fun crearCategoria(name: String?, idUsuario: String?, actividad: Activity?): String {
        return gestorCategoriaActividad.crearCategoria(name, idUsuario, actividad)
    }

    fun borrarCategoria(id: String?, idUsuario: String?, actividad: Activity?): Boolean {
        return gestorCategoriaActividad.borrarCategoria(id, idUsuario, actividad)
    }

    fun editarCategoria(id: String?, name: String?, actividad: Activity?): Boolean {
        return gestorCategoriaActividad.editarCategoria(id, name, actividad)
    }


    fun getCategorias(idUsuario: String?, actividad: Activity?): ArrayList<CategoriaActividad> {
        return gestorCategoriaActividad.getCategorias(idUsuario, actividad)
    }

    fun getCategoriaID(name: String?, idUsuario: String?, actividad: Activity?): String {
        return gestorCategoriaActividad.getCategoriaID(name, idUsuario, actividad)
    }
}
package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import android.media.Image

class Categoria {
    var categoria = 0
    var titulo: String? = null
    var imagen: String? = null
    var color: String? = null
    var listaCategorias: ArrayList<String>? = null
    var gestorCategorias = GestionCategorias()
    constructor()

    fun consultarCategorias(actividad: Activity?): ArrayList<*>? {
        listaCategorias = ArrayList()
        listaCategorias = gestorCategorias.listarCategorias(actividad)
        return listaCategorias
    }

    fun obtenerCategoria(context: Context, nombre: String?): Int {
        categoria = gestorCategorias.obtenerIdCategoria(context, nombre)
        return categoria
    }

    fun obtenerCategorias(actividad: Activity?, idUsuario: String): ArrayList<Categoria> {
        return gestorCategorias.obtenerCategoriasPrincipales(actividad, idUsuario)
    }

    fun crearCategoria(actividad: Activity?, nombre: String?, imagen: String?, principal: Int, color: String, idUsuario: String) {
        gestorCategorias.insertarCategoria(actividad, nombre, imagen, principal, color, idUsuario)
    }

    fun eliminarCategoria(actividad: Activity?, idUsuario: String, idCategoria: Int) {
        gestorCategorias.eliminarCategoria(actividad, idUsuario, idCategoria)
    }


}
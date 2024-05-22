package com.example.plantea.dominio

import android.app.Activity
import android.content.Context

class Categoria {
    var categoria = 0
    var titulo: String? = null
    var imagen: String? = null
    var color: String? = null
    private var listaCategorias: ArrayList<String>? = null
    private var gestorCategorias = GestionCategorias()

    fun consultarCategorias(actividad: Activity?, language: String): ArrayList<*>? {
        listaCategorias = ArrayList()
        listaCategorias = gestorCategorias.listarCategorias(actividad, language)
        return listaCategorias
    }

    fun obtenerCategoria(context: Context, nombre: String?, language: String): Int {
        categoria = gestorCategorias.obtenerIdCategoria(context, nombre, language)
        return categoria
    }

    fun obtenerCategoriaById(context: Context, idCategoria: Int, language: String): String {
        return gestorCategorias.obtenerCategoriaById(context, idCategoria, language)
    }

    fun obtenerCategoriasPrincipales(actividad: Activity?, idUsuario: String, language: String): ArrayList<Categoria> {
        return gestorCategorias.obtenerCategoriasPrincipales(actividad, idUsuario, language)
    }

    fun crearCategoria(actividad: Activity?, nombre: String?, imagen: String?, principal: Int, color: String, idUsuario: String): Int {
        return gestorCategorias.insertarCategoria(actividad, nombre, imagen, principal, color, idUsuario)
    }

    fun eliminarCategoria(actividad: Activity?, idUsuario: String, idCategoria: Int) {
        gestorCategorias.eliminarCategoria(actividad, idUsuario, idCategoria)
    }

    fun checkCategoriaExiste(context: Context?, toString: String, idUsuario: String, language: String): Boolean {
        return gestorCategorias.checkCategoriaExiste(context, toString, idUsuario, language)
    }
}
package com.example.plantea.dominio

import android.app.Activity
import android.content.Context

class Categoria {
    var categoria = 0
    var listaCategorias: ArrayList<String>? = null
    var gestorCategorias = GestionCategorias()

    constructor()
    constructor(categoria: Int) {
        this.categoria = categoria
    }

    fun consultarCategorias(actividad: Activity?): ArrayList<*>? {
        listaCategorias = ArrayList()
        listaCategorias = gestorCategorias.listarCategorias(actividad)
        return listaCategorias
    }

    fun obtenerCategoria(context: Context, nombre: String?): Int {
        categoria = gestorCategorias.obtenerIdCategoria(context, nombre)
        return categoria
    }

    fun crearCategoria(actividad: Activity?, nombre: String?) {
        gestorCategorias.insertarSubcategoria(actividad, nombre)
    }
}
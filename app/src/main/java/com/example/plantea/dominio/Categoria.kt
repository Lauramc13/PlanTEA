package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap

class Categoria {
    private var categoria = 0
    private var titulo: String? = null
    private var imagen: Bitmap? = null
    private var color: String? = null

    private var gestorCategorias = GestionCategorias()

    //getters
    fun getCategoria() = categoria
    fun getTitulo() = titulo
    fun getImagen() = imagen
    fun getColor() = color

    constructor()
    constructor(categoria: Int = 0, titulo: String?, imagen: Bitmap?, color: String?) {
        this.categoria = categoria
        this.titulo = titulo
        this.imagen = imagen
        this.color = color
    }

    fun obtenerCategoria(context: Context, nombre: String?, language: String): Int {
        return gestorCategorias.obtenerIdCategoria(context, nombre, language)
    }

    fun duplicateCategoria(context: Context, idUsuario: String, idCategoria: Int): Int {
        return gestorCategorias.duplicateCategoria(context, idUsuario, idCategoria)
    }

    fun obtenerCategoriasPrincipales(actividad: Activity?, idUsuario: String, language: String): ArrayList<Categoria> {
        return gestorCategorias.obtenerCategoriasPrincipales(actividad, idUsuario, language)
    }

    fun crearCategoria(actividad: Activity?, nombre: String?, imagen: ByteArray?, color: String, idUsuario: String): Int {
        return gestorCategorias.insertarCategoria(actividad, nombre, imagen, color, idUsuario)
    }

    fun eliminarCategoria(actividad: Activity?, idUsuario: String, idCategoria: Int) {
        gestorCategorias.eliminarCategoria(actividad, idUsuario, idCategoria)
    }

    fun checkCategoriaExiste(context: Context?, toString: String, idUsuario: String, language: String): Boolean {
        return gestorCategorias.checkCategoriaExiste(context, toString, idUsuario, language)
    }

}
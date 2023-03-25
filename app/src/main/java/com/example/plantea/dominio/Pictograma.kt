package com.example.plantea.dominio

import android.app.Activity
import java.io.Serializable

class Pictograma : Serializable {
    var titulo: String? = null
    var imagen: String? = null
    var categoria = 0
    var cuaderno = 0
    var listaPictogramas: ArrayList<Pictograma>? = null
    var listaConsultas: ArrayList<String>? = null
    var gestorPictogramas = GestionPictogramas()

    constructor() {}
    constructor(titulo: String?, imagen: String?, categoria: Int, cuaderno: Int) {
        this.titulo = titulo
        this.imagen = imagen
        this.categoria = categoria
        this.cuaderno = cuaderno
    }

    fun obtenerPictogramas(actividad: Activity, idcategoria: Int): ArrayList<*> {
        listaPictogramas = ArrayList()
        listaPictogramas = gestorPictogramas.listarPictogramas(actividad, idcategoria)
        return listaPictogramas as ArrayList<Pictograma>
    }

    fun nuevoPictograma(actividad: Activity?, nombre: String?, imagen: String?, categoria: String?) {
        gestorPictogramas.insertarPictograma(actividad, nombre, imagen, categoria)
    }

    fun obtenerPictogramasCuaderno(actividad: Activity?, identificador: Int): ArrayList<*>? {
        listaPictogramas = ArrayList()
        listaPictogramas = gestorPictogramas.listarPictogramasCuaderno(actividad, identificador)
        return listaPictogramas
    }

    fun obtenerConsultas(actividad: Activity?, idcategoria: Int): ArrayList<*>? {
        listaConsultas = ArrayList()
        listaConsultas = gestorPictogramas.listarConsultas(actividad, idcategoria)
        return listaConsultas
    }

    fun obtenerImagenEvento(actividad: Activity?, consulta: String?, idCategoria: Int): String? {
        val ruta: String?
        ruta = gestorPictogramas.obtenerImagenPictograma(actividad, consulta, idCategoria)
        return ruta
    }
}
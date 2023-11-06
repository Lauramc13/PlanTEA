package com.example.plantea.dominio

import android.app.Activity
import java.io.Serializable

class Pictograma : Serializable {
    var id: String? = null
    var titulo: String? = null
    var imagen: String? = null
    var categoria = 0
    var cuaderno = 0
    var historia: String? = null
    var favorito: Boolean = false
    var sourceAPI: Boolean = false
    var listaPictogramas: ArrayList<Pictograma>? = null
    private var listaConsultas: ArrayList<String>? = null
    private var gestorPictogramas = GestionPictogramas()

    constructor()
    constructor(titulo: String?, imagen: String?, categoria: Int, cuaderno: Int) {
        this.titulo = titulo
        this.imagen = imagen
        this.categoria = categoria
        this.cuaderno = cuaderno
    }

    constructor(id:String?, titulo: String?, imagen: String?, categoria: Int, cuaderno: Int, favorito: Boolean, sourceAPI: Boolean) {
        this.id = id
        this.titulo = titulo
        this.imagen = imagen
        this.categoria = categoria
        this.cuaderno = cuaderno
        this.favorito = favorito
        this.sourceAPI = sourceAPI
    }

    fun obtenerPictogramas(actividad: Activity, idcategoria: Int, userId: String?): ArrayList<*> {
        listaPictogramas = ArrayList()
        listaPictogramas = gestorPictogramas.listarPictogramas(actividad, idcategoria, userId)
        return listaPictogramas as ArrayList<Pictograma>
    }

    fun obtenerFavoritos(actividad: Activity?, idUsuario: String?): ArrayList<Pictograma> {
        listaPictogramas = ArrayList()
        listaPictogramas = gestorPictogramas.obtenerFavoritos(actividad, idUsuario)
        return listaPictogramas!!
    }

    fun nuevoPictograma(actividad: Activity?, nombre: String?, imagen: String?, categoria: String?, idUsuario: String?) {
        gestorPictogramas.insertarPictograma(actividad, nombre, imagen, categoria, idUsuario)
    }

    fun nuevoPictogramaCuaderno(actividad: Activity?, nombre: String, imagen: String?, idCuaderno: Int, idUsuario: String): Int {
        return gestorPictogramas.insertarPictogramaCuaderno(actividad, nombre, imagen, idCuaderno, idUsuario)
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

    fun obtenerImagenEvento(actividad: Activity?, idCategoria: Int): String? {
        return gestorPictogramas.obtenerImagenPictograma(actividad, idCategoria)
    }

    fun insertarFavorito(actividad: Activity?, idUsuario: String?, id: String?, titulo: String?, imagen: String?) {
        gestorPictogramas.insertarFavorito(actividad, idUsuario, id, titulo, imagen)
    }

    fun borrarFavorito(actividad: Activity?, idUsuario: String?, idPicto: String?) {
        gestorPictogramas.borrarFavorito(actividad, idUsuario, idPicto)
    }

    fun getFavorito(actividad: Activity?, idPicto: String?, idUsuario: String?): Boolean {
        return gestorPictogramas.getFavorito(actividad, idPicto, idUsuario)
    }

    fun guardarPictoCuaderno(actividad: Activity?, id: String?, titulo: String?, imagen: String?, idCuaderno: Int) {
        return gestorPictogramas.guardarPictoCuaderno(actividad, id, titulo, imagen, idCuaderno)
    }

    fun borrarPictoCuadernoBusqueda(actividad: Activity?, id: String?, idCuaderno: Int) {
        return gestorPictogramas.borrarPictoCuadernoBusqueda(actividad, id, idCuaderno)
    }

    fun borrarPictoCuaderno(actividad: Activity?, id: String?, idCuaderno: Int) {
        return gestorPictogramas.borrarPictoCuaderno(actividad, id, idCuaderno)
    }
}
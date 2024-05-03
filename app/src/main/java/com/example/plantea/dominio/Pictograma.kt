package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import java.io.Serializable

class Pictograma : Serializable {
    var id: String? = null
    var titulo: String? = null
    var imagen: String? = null
    var categoria = 0
    var cuaderno = 0
    var historia: String? = null
    var duracion: String? = null
    var pictoEntretenimiento = 0
    var favorito: Boolean = false
    var sourceAPI: Boolean = false
    var listaPictogramas: ArrayList<Pictograma>? = null
    private var gestorPictogramas = GestionPictogramas()
    //private var listaConsultas: ArrayList<String>? = null
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

    fun obtenerPictogramas(context: Context, idcategoria: Int, idUsuario: String?, language: String): ArrayList<*> {
        listaPictogramas = ArrayList()
        listaPictogramas = gestorPictogramas.obtenerPictogramas(context, idcategoria, idUsuario, language)
        return listaPictogramas as ArrayList<Pictograma>
    }

    fun obtenerFavoritos(actividad: Context?, idUsuario: String?, language: String): ArrayList<Pictograma> {
        listaPictogramas = ArrayList()
        listaPictogramas = gestorPictogramas.obtenerFavoritos(actividad, idUsuario, language)
        return listaPictogramas!!
    }

    fun nuevoPictograma(actividad: Activity?, nombre: String?, imagen: String?, categoria: String?, idUsuario: String?): String {
       return gestorPictogramas.insertarPictograma(actividad, nombre, imagen, categoria, idUsuario)
    }

    fun nuevoPictogramaCuaderno(actividad: Activity?, nombre: String, imagen: String?, idCuaderno: Int, idUsuario: String): Int {
        return gestorPictogramas.insertarPictogramaCuaderno(actividad, nombre, imagen, idCuaderno, idUsuario)
    }

    fun obtenerPictogramasCuaderno(actividad: Activity?, idCuaderno: Int, language: String): ArrayList<*>? {
        listaPictogramas = ArrayList()
        listaPictogramas = gestorPictogramas.listarPictogramasCuaderno(actividad, idCuaderno, language)
        return listaPictogramas
    }

    /*fun obtenerConsultas(actividad: Activity?, idcategoria: Int): ArrayList<*>? {
        listaConsultas = ArrayList()
        listaConsultas = gestorPictogramas.listarConsultas(actividad, idcategoria)
        return listaConsultas
    }

    fun obtenerImagenEvento(actividad: Activity?, idCategoria: Int): String? {
        return gestorPictogramas.obtenerImagenPictograma(actividad, idCategoria)
    }*/

    fun insertarFavorito(context: Context?, idUsuario: String?, id: String?, titulo: String?, imagen: String?, sourceAPI: Boolean) {
        gestorPictogramas.insertarFavorito(context, idUsuario, id, titulo, imagen, sourceAPI)
    }

    fun borrarFavorito(context: Context?, idUsuario: String?, idPicto: String?) {
        gestorPictogramas.borrarFavorito(context, idUsuario, idPicto)
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

    fun obtenerPicto(context: Context?, id: String?): Pictograma {
        return gestorPictogramas.obtenerPicto(context, id)
    }

    fun getRandomPictograms(context: Context?, idUsuario: String?, language: String): ArrayList<Pictograma> {
        return gestorPictogramas.getRandomPictograms(context, idUsuario, language)
    }

    fun copy(): Pictograma {
        return Pictograma(this.id, this.titulo, this.imagen, this.categoria, this.cuaderno, this.favorito, this.sourceAPI)
    }

}
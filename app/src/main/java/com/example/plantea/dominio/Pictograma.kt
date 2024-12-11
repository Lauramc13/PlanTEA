package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import java.io.Serializable

class Pictograma: Serializable {
    var id: String? = null
    var titulo: String? = null

    @Transient
    var imagen: Bitmap? = null

    var idAPI = 0
    var categoria = 0
    var historia: String? = null
    var duracion: String? = null
    var pictoEntretenimiento = 0
    var favorito: Boolean = false
    var listaPictogramas: ArrayList<Pictograma>? = null
    private var gestorPictogramas = GestionPictogramas()

    constructor()
    constructor(titulo: String?, imagen: Bitmap?, idAPI : Int, categoria: Int) {
        this.titulo = titulo
        this.imagen = imagen
        this.categoria = categoria
        this.idAPI = idAPI
    }

    constructor(id:String?, titulo: String?, imagen: Bitmap?, idAPI: Int, categoria: Int, favorito: Boolean) {
        this.id = id
        this.titulo = titulo
        this.imagen = imagen
        this.idAPI = idAPI
        this.categoria = categoria
        this.favorito = favorito
    }

    fun obtenerPictogramas(context: Context, idcategoria: Int, idUsuario: String?, language: String): ArrayList<*> {
        return gestorPictogramas.obtenerPictogramas(context, idcategoria, idUsuario, language)
    }

    fun obtenerFavoritos(actividad: Context?, idUsuario: String?, language: String): ArrayList<Pictograma> {
        listaPictogramas = ArrayList()
        listaPictogramas = gestorPictogramas.obtenerFavoritos(actividad, idUsuario, language)

        return listaPictogramas!!
    }

    fun nuevoPictogramaLocal(actividad: Activity?, nombre: String?, imagen: ByteArray?, categoria: String?, idUsuario: String?): String {
       return gestorPictogramas.insertarPictogramaLocal(actividad, nombre, imagen, categoria, idUsuario)
    }

    fun nuevoPictogramaAPI(actividad: Activity?, nombre: String?, id: String?, categoria: String?): String {
        return gestorPictogramas.insertarPictogramaAPI(actividad, nombre, id, categoria)
    }
    fun insertarFavorito(context: Context?, idUsuario: String?, id: String?, titulo: String?, idAPI: Int) {
        gestorPictogramas.insertarFavorito(context, idUsuario, id, titulo, idAPI)
    }

    fun borrarFavorito(context: Context?, idUsuario: String?, idPicto: String?) {
        gestorPictogramas.borrarFavorito(context, idUsuario, idPicto)
    }

    fun getFavorito(actividad: Activity?, idPicto: String?, idUsuario: String?): Boolean {
        return gestorPictogramas.getFavorito(actividad, idPicto, idUsuario)
    }

    fun obtenerPicto(context: Context?, id: String?, language: String): Pictograma {
        return gestorPictogramas.obtenerPicto(context, id, language)
    }

    fun getRandomPictograms(context: Context?, idUsuario: String?, language: String): ArrayList<Pictograma> {
        return gestorPictogramas.getRandomPictograms(context, idUsuario, language)
    }

    fun copy(): Pictograma {
        return Pictograma(this.id, this.titulo, this.imagen, this.idAPI, this.categoria, this.favorito)
    }

    fun guardarHistoria(actividad: Activity?, id: String, idPicto: String?, historia: String?) {
        gestorPictogramas.guardarHistoria(actividad, id, idPicto, historia)

    }

    fun guardarDuracion(actividad: Activity?, toString: String, id: String, s: String?) {
        gestorPictogramas.guardarDuracion(actividad, toString, id, s)
    }

    fun guardarPictoEntretenimiento(actividad: Activity?, id: String, idPicto: String?, idEntretenimiento: String?) {
        gestorPictogramas.guardarPictoEntretenimiento(actividad, id, idPicto, idEntretenimiento)
    }

}
package com.example.plantea.dominio

import android.app.Activity
import android.graphics.Bitmap
import java.time.LocalDate

class DiaMes {
    // Atributos: fecha, titulo, color, imagen, idUsuario
    var fecha: LocalDate? = null
    var titulo: String? = null
    var color: String? = null
    var imagen: Bitmap? = null
    private val gestorMes = GestionMes()

    // Constructor
    constructor()
    constructor(fecha: LocalDate?, titulo: String?, color: String?, imagen: Bitmap?) {
        this.fecha = fecha
        this.titulo = titulo
        this.color = color
        this.imagen = imagen
    }

    fun obtenerDiasMes(idUsuario: String, fecha:String?, actividad: Activity?): ArrayList<DiaMes> {
        return gestorMes.obtenerDiasMes(idUsuario, fecha, actividad)
    }

    fun guardarDia(idUsuario: String, titulo: String?, imagen: ByteArray?, color: String?, fecha: String?, actividad: Activity?) {
        gestorMes.guardarDia(idUsuario, titulo,  imagen, color, fecha, actividad)
    }

    fun borrarImagen(idUsuario: String, fecha: String, actividad: Activity?) {
        gestorMes.borrarImagen(idUsuario, fecha, actividad)
    }

    fun borrarColor(idUsuario: String, fecha: String, actividad: Activity?) {
        gestorMes.borrarColor(idUsuario, fecha, actividad)
    }

    fun borrarDia(idUsuario: String, fecha: String, actividad: Activity?) {
        gestorMes.borrarDia(idUsuario, fecha, actividad)
    }

    fun editarDia(idUsuario: String, titulo: String?, imagen: ByteArray?, color: String?, fecha: String?, actividad: Activity?) {
        gestorMes.editarDia(idUsuario, titulo, imagen, color, fecha, actividad)
    }

}
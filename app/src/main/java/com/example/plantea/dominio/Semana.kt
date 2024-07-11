package com.example.plantea.dominio

import android.app.Activity
import android.graphics.Bitmap

class Semana {

    var dia: String? = null
    var imagen: Bitmap? = null
    private val gestorSemana = GestionSemana()

    constructor()

    constructor(day: String?, imageDay: Bitmap?){
        dia = day
        imagen = imageDay
    }

    fun obtenerconfig(idUsuario: String, actividad: Activity?): Int {
        return gestorSemana.obtenerconfig(idUsuario ,actividad)
    }

    fun obtenerImagenes(idUsuario: String, days: MutableList<String>, actividad: Activity?): MutableList<ByteArray?> {
        return gestorSemana.obtenerImagenes(idUsuario, days, actividad)
    }

    fun guardarImagen(idUsuario: String, imagen: ByteArray?, fecha: String, actividad: Activity?) {
        gestorSemana.guardarImagen(idUsuario, imagen, fecha, actividad)
    }

}
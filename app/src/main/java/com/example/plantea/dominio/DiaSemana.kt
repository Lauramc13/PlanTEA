package com.example.plantea.dominio

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import com.example.plantea.presentacion.actividades.CommonUtils

class DiaSemana {
    var dia: String? = null
    var imagen: Bitmap? = null
    var color: String? = null
    private val gestorSemana = GestionSemana()

    constructor()

    constructor(day: String?, imageDay: Bitmap?, color: String?) {
        dia = day
        imagen = imageDay
        this.color = color
    }


    fun obtenerconfig(idUsuario: String, actividad: Activity?): Int {
        return gestorSemana.obtenerconfig(idUsuario ,actividad)
    }

    fun obtenerDias(idUsuario: String, days: MutableList<String>, actividad: Activity?): ArrayList<DiaSemana> {
        return gestorSemana.obtenerConfigDias(idUsuario, days, actividad)
    }

    fun guardarSemana(idUsuario: String, imagen: ByteArray?, color:String?, fecha: String?, actividad: Activity?) {
        gestorSemana.guardarSemana(idUsuario, imagen, color, fecha, actividad)
    }

    fun borrarImagen(idUsuario: String, fecha: String, actividad: Activity?) {
        gestorSemana.borrarImagen(idUsuario, fecha, actividad)
    }

    fun borrarColor(idUsuario: String, fecha: String, actividad: Activity?) {
        gestorSemana.borrarColor(idUsuario, fecha, actividad)
    }

    fun guardarConfiguracionWeek(idUsuario: String, configurationWeek: Int, actividad: Activity?){
        gestorSemana.guardarConfiguracionWeek(idUsuario, configurationWeek, actividad)
    }

}
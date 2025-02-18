package com.example.plantea.dominio

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import com.example.plantea.presentacion.actividades.CommonUtils

class DiaSemana() {
    var dia: String? = null
    var imagen: Bitmap? = null
    var color: String? = null
    var idEvento: String? = null
    private val gestorSemana = GestionSemana()

    fun obtenerconfig(idUsuario: String, actividad: Activity?): Int {
        return gestorSemana.obtenerconfig(idUsuario, actividad)
    }

    fun obtenerColoresHeader(idUsuario: String, actividad: Activity?): ArrayList<String>? {
        return gestorSemana.obtenerColoresHeader(idUsuario, actividad)
    }

    fun obtenerDias(idUsuario: String, days: MutableList<String>, actividad: Activity?): ArrayList<DiaSemana> {
        return gestorSemana.obtenerConfigDias(idUsuario, days, actividad)
    }

    fun guardarSemana(idUsuario: String, imagen: ByteArray?, color:String?, fecha: String?, idEvento: String?, actividad: Activity?) {
        gestorSemana.guardarSemana(idUsuario, imagen, color, fecha, idEvento, actividad)
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

    fun guardarColorsHeader(idUsuario: String, colorsHeader: ArrayList<String>, actividad: Activity?){
        gestorSemana.guardarColorsHeader(idUsuario, colorsHeader, actividad)
    }
}
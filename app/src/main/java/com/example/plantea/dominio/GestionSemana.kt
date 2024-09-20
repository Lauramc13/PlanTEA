package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.CommonUtils

class GestionSemana {


    private var conectorBD: ConectorBD? = null
    fun obtenerconfig(idUsuario: String, activity: Activity?): Int {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        val cursor = conectorBD!!.obtenerConfiguracionSemana(idUsuario)
        var resultado = 0
        if (cursor.moveToFirst()) {
            resultado = cursor.getInt(0) // if its 0 change it to 1
            if (resultado == 0) {
                resultado = 1
            }
        }

        conectorBD!!.cerrar()
        return resultado
    }

    fun obtenerConfigDias(idUsuario: String, days: MutableList<String>, activity: Activity?): ArrayList<DiaSemana> {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        val dias = ArrayList<DiaSemana>()

        for(day in days){
            val dia = DiaSemana() // Create a new instance inside the loop
            dia.dia = day
            val cursor = conectorBD!!.obtenerConfigDias(idUsuario, day)
            if (cursor.moveToFirst()) {
                dia.imagen = CommonUtils.byteArrayToBitmap(cursor.getBlob(0))
                dia.color = cursor.getString(1)
            }
            dias.add(dia)
        }

        conectorBD!!.cerrar()
        return dias
    }

    fun guardarSemana(idUsuario: String, imagen: ByteArray?, color: String?, fecha: String?, activity: Activity?) {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        conectorBD!!.guardarSemana(idUsuario, imagen, color, fecha)
        conectorBD!!.cerrar()
    }

    fun borrarImagen(idUsuario: String, fecha: String, activity: Activity?) {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        conectorBD!!.borrarImagenSemana(idUsuario, fecha)
        conectorBD!!.cerrar()
    }

    fun borrarColor(idUsuario: String, fecha: String, activity: Activity?) {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        conectorBD!!.borrarColorSemana(idUsuario, fecha)
        conectorBD!!.cerrar()
    }


    fun guardarConfiguracionWeek(idUsuario: String, configurationWeek: Int, actividad: Activity?){
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.guardarConfiguracionWeek(idUsuario, configurationWeek)
        conectorBD!!.cerrar()
    }

}
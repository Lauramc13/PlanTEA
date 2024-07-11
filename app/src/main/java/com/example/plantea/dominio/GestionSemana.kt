package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD

class GestionSemana {


    private var conectorBD: ConectorBD? = null
    fun obtenerconfig(idUsuario: String, activity: Activity?): Int {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        val cursor = conectorBD!!.obtenerConfiguracionSemana(idUsuario)
        var resultado = 0
        if (cursor.moveToFirst()) {
            resultado = cursor.getInt(0)
        }

        conectorBD!!.cerrar()
        return resultado
    }

    fun obtenerImagenes(idUsuario: String, days: MutableList<String>, activity: Activity?): MutableList<ByteArray?> {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        val imagenes = mutableListOf<ByteArray?>()
        for(day in days){
            imagenes.add(conectorBD!!.obtenerImagenDia(idUsuario, day))
        }

        conectorBD!!.cerrar()
        return imagenes
    }

    fun guardarImagen(idUsuario: String, imagen: ByteArray?, fecha: String, activity: Activity?) {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        conectorBD!!.guardarImagenSemana(idUsuario, imagen, fecha)
        conectorBD!!.cerrar()
    }

}
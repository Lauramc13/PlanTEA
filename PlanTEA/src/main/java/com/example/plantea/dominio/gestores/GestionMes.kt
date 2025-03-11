package com.example.plantea.dominio.gestores

import android.app.Activity
import android.graphics.BitmapFactory
import com.example.plantea.dominio.objetos.DiaMes
import com.example.plantea.persistencia.ConectorBD
import java.time.LocalDate

class GestionMes {
    private var conectorBD: ConectorBD? = null

    fun obtenerDiasMes(idUsuario: String, fecha: String?, activity: Activity?): ArrayList<DiaMes> {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()

        val dias = ArrayList<DiaMes>()

        val cursor = conectorBD!!.obtenerDiaMes(idUsuario, fecha)
        for (i in 0 until cursor.count) {
            cursor.moveToPosition(i)
            val dia = DiaMes()
            dia.nombre = cursor.getString(0)
            dia.fecha = LocalDate.parse(cursor.getString(1))
            if (cursor.getBlob(2) != null) {
                val img = cursor.getBlob(2)
                dia.imagen = BitmapFactory.decodeByteArray(img, 0, img.size)
            }
            dia.color = cursor.getString(3)
            dias.add(dia)
        }

        conectorBD!!.cerrar()
        return dias
    }

    fun guardarDia(idUsuario: String, titulo: String?,  imagen: ByteArray?, color: String?, fecha: String?, activity: Activity?) {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        conectorBD!!.guardarDia(idUsuario, titulo, imagen, color, fecha)
        conectorBD!!.cerrar()
    }

    fun borrarDia(idUsuario: String, fecha: String, activity: Activity?) {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        conectorBD!!.borrarDia(idUsuario, fecha)
        conectorBD!!.cerrar()
    }

    fun editarDia(idUsuario: String, titulo: String?, imagen: ByteArray?, color: String?, fechaNueva:String?, fecha: String?, activity: Activity?) {
        conectorBD = ConectorBD(activity)
        conectorBD!!.abrir()
        conectorBD!!.editarDia(idUsuario, titulo, imagen, color, fechaNueva, fecha)
        conectorBD!!.cerrar()
    }
}
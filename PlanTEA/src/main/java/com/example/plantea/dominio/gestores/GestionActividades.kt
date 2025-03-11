package com.example.plantea.dominio.gestores

import android.app.Activity
import android.content.Context
import com.example.plantea.dominio.objetos.Actividad
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.CommonUtils

/*
* Clase que se encarga de obtener los datos de la base de datos y tratarlos para ser devuelto a las actividades.
 */

class GestionActividades {
    private var conectorBD: ConectorBD? = null

    fun crearActividad(name: String?, imagen: ByteArray?, idUsuario: String?, actividad: Activity?): String? {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.insertarActividad(name, imagen, idUsuario)
        conectorBD!!.cerrar()
        return resultado
    }

    fun borrarActividad(idActividad: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.borrarActividad(idActividad)
        conectorBD!!.cerrar()
        return resultado
    }

    fun actualizarActividad(idActividad: String?, name: String?, imagen: ByteArray?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.actualizarActividad(idActividad, name, imagen)
        conectorBD!!.cerrar()
        return resultado
    }

    fun addCategoriaActividad(idActividad: String?, idCategoria: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.addCategoriaActividad(idActividad, idCategoria)
        conectorBD!!.cerrar()
        return resultado
    }

    fun removeCategoriaActividad(idActividad: String?, idCategoria: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.removeCategoriaActividad(idActividad, idCategoria)
        conectorBD!!.cerrar()
        return resultado
    }

    fun getActividades(idUsuario: String?, idCategoria: String?, actividad: Activity?): ArrayList<Actividad> {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val actividades = ArrayList<Actividad>()
        val cursor = conectorBD!!.getActividades(idUsuario, idCategoria)
        if (cursor.moveToFirst()) {
            do {
                val actividad = Actividad()
                actividad.id = cursor.getString(0)
                actividad.nombre = cursor.getString(1)
                actividad.imagen = CommonUtils.byteArrayToBitmap(cursor.getBlob(2))
                actividad.idUsuario = cursor.getString(3)
                actividad.idCategoria = ArrayList(cursor.getString(4)?.split(",") ?: emptyList())
                actividades.add(actividad)
            } while (cursor.moveToNext())
        }
        cursor.close()
        conectorBD!!.cerrar()
        return actividades
    }

    fun getAllActividades(idUsuario: String?, actividad: Activity?): ArrayList<Actividad> {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val actividades = ArrayList<Actividad>()
        val cursor = conectorBD!!.getAllActividades(idUsuario)
        if (cursor.moveToFirst()) {
            do {
                val a = Actividad()
                a.id = cursor.getString(0)
                a.nombre = cursor.getString(1)
                a.imagen = CommonUtils.byteArrayToBitmap(cursor.getBlob(2))
                a.idUsuario = cursor.getString(3)
                a.idCategoria = ArrayList(cursor.getString(4)?.split(",") ?: emptyList())
                actividades.add(a)
            } while (cursor.moveToNext())
        }
        cursor.close()
        conectorBD!!.cerrar()
        return actividades
    }

    fun getAllActividadesAsPictogramas(idUsuario: String?, actividad: Activity?): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val actividades = ArrayList<Pictograma>()
        val cursor = conectorBD!!.getAllActividades(idUsuario)
        if (cursor.moveToFirst()) {
            do {
                val a = Pictograma()
                a.id = cursor.getString(0)
                a.titulo = cursor.getString(1)
                a.imagen = CommonUtils.byteArrayToBitmap(cursor.getBlob(2))
                actividades.add(a)
            } while (cursor.moveToNext())
        }
        cursor.close()
        conectorBD!!.cerrar()
        return actividades
    }

    fun getActividadById(idActividad: String?, context: Context?): Actividad {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        val actividad = Actividad()
        val cursor = conectorBD!!.getActividadById(idActividad)
        if (cursor.moveToFirst()) {
            actividad.id = cursor.getString(0)
            actividad.nombre = cursor.getString(1)
            actividad.imagen = CommonUtils.byteArrayToBitmap(cursor.getBlob(2))
            actividad.idUsuario = cursor.getString(3)
        }
        cursor.close()
        conectorBD!!.cerrar()
        return actividad
    }
}

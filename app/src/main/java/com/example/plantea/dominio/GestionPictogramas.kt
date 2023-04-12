package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD
import java.io.Serializable

class GestionPictogramas : Serializable {
    private var listaPictogramas: ArrayList<Pictograma>? = null
    private var listaConsultas: ArrayList<String>? = null
    private var conectorBD: ConectorBD? = null
    fun listarPictogramas(actividad: Activity?, idcategoria: Int): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        listaPictogramas = ArrayList()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarPictogramas(idcategoria)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.titulo = c.getString(0)
                pictograma.imagen = c.getString(1)
                pictograma.categoria = c.getInt(2)
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaPictogramas!!
    }

    fun insertarPictograma(actividad: Activity?, nombre: String?, imagen: String?, categoria: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarPictograma(nombre, imagen, categoria)
        conectorBD!!.cerrar()
    }

    fun listarPictogramasCuaderno(actividad: Activity?, identificador: Int): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        listaPictogramas = ArrayList()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarPictogramasCuaderno(identificador)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.titulo = c.getString(0)
                pictograma.imagen = c.getString(1)
                pictograma.cuaderno = c.getInt(2)
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        for (i in listaPictogramas!!.indices) {
            print(listaPictogramas!![i].titulo + listaPictogramas!![i].cuaderno)
        }
        return listaPictogramas as ArrayList<Pictograma>
    }

    fun listarConsultas(actividad: Activity?, idcategoria: Int): ArrayList<String> {
        conectorBD = ConectorBD(actividad)
        listaConsultas = ArrayList()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarConsulta(idcategoria)
        if (c.moveToFirst()) {
            do {
                listaConsultas!!.add(c.getString(0))
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaConsultas!!
    }

    fun obtenerImagenPictograma(actividad: Activity?, consulta: String?, idCategoria: Int): String? {
        conectorBD = ConectorBD(actividad)
        var ruta: String? = null
        conectorBD!!.abrir()
        val c = conectorBD!!.obtenerRutaPictograma(consulta, idCategoria)
        if (c.moveToFirst()) {
            do {
                ruta = c.getString(0)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return ruta
    }
}
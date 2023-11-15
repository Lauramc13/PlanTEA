package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD

class GestionCategorias {
    private var listaCategorias: ArrayList<String>? = null
    private var conectorBD: ConectorBD? = null

    fun listarCategorias(actividad: Activity?): ArrayList<String> {
        conectorBD = ConectorBD(actividad)
        listaCategorias = ArrayList()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarCategorias()
        if (c.moveToFirst()) {
            do {
                listaCategorias!!.add(c.getString(0))
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaCategorias!!
    }

    fun obtenerIdCategoria(actividad: Activity?, nombre: String?): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        var categoria = 0
        val c = conectorBD!!.obtenerIdCategoria(nombre)
        if (c.moveToFirst()) {
            categoria = c.getInt(0)
        }
        c.close()
        conectorBD!!.cerrar()
        return categoria
    }

    fun insertarSubcategoria(actividad: Activity?, nombre: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarSubcategoria(nombre)
        conectorBD!!.cerrar()
    }
}
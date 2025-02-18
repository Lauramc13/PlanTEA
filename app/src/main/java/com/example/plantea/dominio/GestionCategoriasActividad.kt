package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import com.example.plantea.persistencia.ConectorBD

class GestionCategoriasActividad {
    private var conectorBD: ConectorBD? = null

    fun crearCategoria(nombre: String?, idUsuario: String?, actividad: Activity?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val idCategoria = conectorBD!!.crearCategoriaActividad(nombre, idUsuario).toString()
        conectorBD!!.cerrar()
        return idCategoria
    }

    fun borrarCategoria(idCategoria: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.borrarCategoriaActividad(idCategoria)
        conectorBD!!.cerrar()
        return resultado
    }

    fun editarCategoria(idCategoria: String?, nombre: String?, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val resultado = conectorBD!!.editarCategoriaActividad(idCategoria, nombre)
        conectorBD!!.cerrar()
        return resultado
    }

    fun getCategorias(idUsuario: String?, actividad: Activity?): ArrayList<CategoriaActividad> {
        conectorBD = ConectorBD(actividad)
        val listCategorias = ArrayList<CategoriaActividad>()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarCategoriasActividad(idUsuario)
        if (c.moveToFirst()) {
            do {
                val categoria = CategoriaActividad(c.getString(0), c.getString(1), c.getString(2))
                listCategorias.add(categoria)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listCategorias
    }

    fun getCategoriaID(nombre: String?, idUsuario: String?, actividad: Activity?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val cursor = conectorBD!!.obtenerIdCategoriaActividad(nombre, idUsuario)
        var idCategoria = ""
        if (cursor.moveToFirst()) {
            idCategoria = cursor.getString(0)
        }

        conectorBD!!.cerrar()
        return idCategoria
    }
}
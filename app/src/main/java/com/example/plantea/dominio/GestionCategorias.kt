package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
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

    fun obtenerIdCategoria(context: Context, nombre: String?): Int {
        conectorBD = ConectorBD(context)
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

    fun obtenerCategoriasPrincipales(actividad: Activity?, idUsuario:String): ArrayList<Categoria> {
        conectorBD = ConectorBD(actividad)
        val listCategorias = ArrayList<Categoria>()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarCategoriasPrincipales(idUsuario)
        if (c.moveToFirst()) {
            do {
                val categoria = Categoria()
                categoria.categoria = c.getInt(0)
                categoria.titulo = c.getString(1)
                categoria.imagen = c.getString(2)
                categoria.color = c.getString(3)
                listCategorias.add(categoria)

            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listCategorias
    }

   /* fun insertarSubcategoria(actividad: Activity?, nombre: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarSubcategoria(nombre)
        conectorBD!!.cerrar()
    }*/

    fun insertarCategoria(actividad: Activity?, nombre: String?, imagen: String?, principal: Int, color: String, idUsuario: String) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarCategoria(nombre, imagen, principal, color, idUsuario)
        conectorBD!!.cerrar()
    }

    fun eliminarCategoria(actividad: Activity?, idUsuario: String, idCategoria: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.eliminarCategoria(idUsuario, idCategoria)
        conectorBD!!.cerrar()
    }

    fun checkCategoriaExiste(context: Context?, toString: String, idUsuario: String): Boolean {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        val c = conectorBD!!.checkCategoriaExiste(toString, idUsuario)
        var existe = true
        if (c.moveToFirst()) {
            //if count is 1, then the entry was found
            if(c.getInt(0) == 0){
                existe = false
            }
        }
        c.close()
        conectorBD!!.cerrar()
        return existe
    }

    /* fun obtenerTituloCategoria(context: Context, idCategoria: Int): String {
         conectorBD = ConectorBD(context)
         conectorBD!!.abrir()
         var titulo = ""
         titulo = conectorBD!!.obtenerTituloCategoria(idCategoria).toString()
         conectorBD!!.cerrar()
         return titulo
     }*/
}
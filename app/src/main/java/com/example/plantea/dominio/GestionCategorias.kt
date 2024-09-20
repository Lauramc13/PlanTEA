package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.CommonUtils
import java.io.FileOutputStream

class GestionCategorias {
    private var listaCategorias: ArrayList<String>? = null
    private var conectorBD: ConectorBD? = null

   /* fun listarCategorias(actividad: Activity?, language: String, idUsuario: String?): ArrayList<String> {
        conectorBD = ConectorBD(actividad)
        listaCategorias = ArrayList()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarCategoriasPrincipales(idUsuario, language)
        if (c.moveToFirst()) {
            do {
                listaCategorias!!.add(c.getString(1))

            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaCategorias!!
    }*/

    fun obtenerIdCategoria(context: Context, nombre: String?, language: String): Int {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        var categoria = 0
        val c = conectorBD!!.obtenerIdCategoria(nombre, language)
        if (c.moveToFirst()) {
            categoria = c.getInt(0)
        }
        c.close()
        conectorBD!!.cerrar()
        return categoria
    }

    fun duplicateCategoria(context: Context, idUsuario: String, idCategoria: Int): Int {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        val id =  conectorBD!!.duplicateCategoria(idUsuario, idCategoria)
        conectorBD!!.cerrar()
        return id
    }

    fun obtenerCategoriasPrincipales(actividad: Activity?, idUsuario:String, language: String): ArrayList<Categoria> {
        conectorBD = ConectorBD(actividad)
        val listCategorias = ArrayList<Categoria>()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarCategoriasPrincipales(idUsuario, language)
        if (c.moveToFirst()) {
            do {
                val img = c.getBlob(2)
                val imgBitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
                val categoria = Categoria(c.getInt(0), c.getString(1), imgBitmap, c.getString(3))
                listCategorias.add(categoria)

            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listCategorias
    }

    fun insertarCategoria(actividad: Activity?, nombre: String?, imagen: ByteArray?, color: String, idUsuario: String): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val idCategoria  = conectorBD!!.insertarCategoria(nombre, imagen, color, idUsuario).toInt()

        conectorBD!!.cerrar()
        return idCategoria
    }

    fun eliminarCategoria(actividad: Activity?, idUsuario: String, idCategoria: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.eliminarCategoria(idUsuario, idCategoria)
        conectorBD!!.cerrar()
    }

    fun checkCategoriaExiste(context: Context?, toString: String, idUsuario: String, language: String): Boolean {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        val c = conectorBD!!.checkCategoriaExiste(toString, idUsuario, language)
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

}
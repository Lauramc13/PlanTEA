package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import com.example.plantea.persistencia.ConectorBD
import java.io.Serializable

class GestionPictogramas : Serializable {
    private var listaPictogramas: ArrayList<Pictograma>? = null
    private var listaConsultas: ArrayList<String>? = null
    private var conectorBD: ConectorBD? = null

    // fun listarPictogramas(actividad: Activity?, idcategoria: Int): ArrayList<Pictograma> {
    //     conectorBD = ConectorBD(actividad)
    //     listaPictogramas = ArrayList()
    //     conectorBD!!.abrir()
    //     val c = conectorBD!!.listarPictogramas(idcategoria)
    //     if (c.moveToFirst()) {
    //         do {
    //             val pictograma = Pictograma()
    //             pictograma.titulo = c.getString(0)
    //             pictograma.imagen = c.getString(1)
    //             pictograma.categoria = c.getInt(2)
    //             listaPictogramas!!.add(pictograma)
    //         } while (c.moveToNext())
    //     }
    //     c.close()
    //     conectorBD!!.cerrar()
    //     return listaPictogramas!!
    // }


    fun obtenerPictogramas(context: Context, idcategoria: Int, userId: String?, language: String): ArrayList<Pictograma> {
        conectorBD = ConectorBD(context)
        listaPictogramas = ArrayList()
        conectorBD!!.abrir()

        val c = conectorBD!!.listarPictogramasPrueba(idcategoria, userId, language)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getString(0)
                pictograma.titulo = c.getString(1)
                pictograma.imagen = c.getString(2)
                pictograma.categoria = c.getInt(3)
                pictograma.favorito = c.getInt(4) == 1 // Convert favorito value to Boolean
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()

        return listaPictogramas!!
    }


    fun insertarPictograma(actividad: Activity?, nombre: String?, imagen: String?, categoria: String?, idUsuario: String?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val id = conectorBD!!.insertarPictograma(nombre, imagen, categoria, idUsuario)
        conectorBD!!.cerrar()
        return id
    }

    /*fun insertarPictogramaCuaderno(actividad: Activity?, nombre: String, imagen: String?, idCuaderno: Int, idUsuario: String): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val id = conectorBD!!.insertarPictogramaCuaderno(nombre, imagen, idCuaderno, idUsuario)
        conectorBD!!.cerrar()
        return id
    }

    fun listarPictogramasCuaderno(actividad: Activity?, idCuaderno: Int, language: String): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        listaPictogramas = ArrayList()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarPictogramasCuaderno(idCuaderno, language)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getString(0)
                pictograma.titulo = c.getString(1)
                pictograma.imagen = c.getString(2)
                pictograma.cuaderno = c.getInt(3)
                pictograma.sourceAPI = c.getInt(4) == 1
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        /*for (i in listaPictogramas!!.indices) {
            print(listaPictogramas!![i].titulo + listaPictogramas!![i].cuaderno)
        }*/
        return listaPictogramas as ArrayList<Pictograma>
    }*/

  /*  fun listarConsultas(actividad: Activity?, idcategoria: Int): ArrayList<String> {
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
    }*/

   /* fun obtenerImagenPictograma(actividad: Activity?, idCategoria: Int): String? {
        conectorBD = ConectorBD(actividad)
        var ruta: String? = null
        conectorBD!!.abrir()
        val c = conectorBD!!.obtenerRutaPictograma(idCategoria)
        if (c.moveToFirst()) {
            do {
                ruta = c.getString(0)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return ruta
    }*/

    fun obtenerFavoritos(actividad: Context?, idUsuario: String?, language: String): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        listaPictogramas = ArrayList()
        conectorBD!!.abrir()
        val c = conectorBD!!.obtenerFavoritos(idUsuario, language)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getString(0)
                pictograma.titulo = c.getString(1)
                pictograma.imagen = c.getString(2)
                pictograma.categoria = 10
                pictograma.favorito = true
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaPictogramas!!
    }

    fun insertarFavorito(context: Context?, idUsuario: String?, id:String?, titulo: String?, imagen: String?, sourceAPI: Boolean){
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        conectorBD!!.insertarFavorito(idUsuario, id, titulo, imagen, sourceAPI)
        conectorBD!!.cerrar()
    }

    fun borrarFavorito(context: Context?, idUsuario: String?, idPicto: String?){
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        conectorBD!!.borrarFavorito(idUsuario, idPicto)
        conectorBD!!.cerrar()
    }
    fun getFavorito(actividad: Activity?, idPicto: String?, idUsuario: String?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val exists = conectorBD!!.getFavorito(idPicto, idUsuario)
        conectorBD!!.cerrar()
        return exists
    }

    /*fun guardarPictoCuaderno(actividad: Activity?, id: String?, titulo: String?, imagen: String?, idCuaderno: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarPictogramaCuadernoBusqueda(id, titulo, imagen, idCuaderno)
        conectorBD!!.cerrar()
    }

    fun borrarPictoCuadernoBusqueda(actividad: Activity?, id: String?, idCuaderno: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.borrarPictogramaCuadernoBusqueda(id, idCuaderno)
        conectorBD!!.cerrar()
    }

    fun borrarPictoCuaderno(actividad: Activity?, id: String?, idCuaderno: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.borrarPictogramaCuaderno(id, idCuaderno)
        conectorBD!!.cerrar()
    }*/

    fun obtenerPicto(context: Context?, id: String?, language: String): Pictograma {
        val pictograma = Pictograma()
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        val c = conectorBD!!.obtenerPictograma(id, language)
        if (c.moveToFirst()) {
            do {
                pictograma.id = c.getString(0)
                pictograma.imagen = c.getString(1)
                pictograma.titulo = c.getString(2)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return pictograma
    }

    fun getRandomPictograms(context: Context?, idUsuario: String?, language: String): ArrayList<Pictograma> {
        conectorBD = ConectorBD(context)
        listaPictogramas = ArrayList()
        conectorBD!!.abrir()
        val c = conectorBD!!.listarPictogramasAleatorios(idUsuario, language)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getString(0)
                pictograma.titulo = c.getString(1)
                pictograma.imagen = c.getString(2)
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaPictogramas!!

    }


}
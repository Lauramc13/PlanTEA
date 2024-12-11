package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.firebase.installations.Utils
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

        /*val c = if(idcategoria <= 5){
            conectorBD!!.listarPictogramasCategoria(idcategoria, userId, language)
        }else{
            conectorBD!!.listarPictogramasCategoriaUsuario(idcategoria, userId, language)

        }
*/
        val c = conectorBD!!.listarPictogramasCategoria(idcategoria, userId, language)

        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getString(0)
                pictograma.titulo = c.getString(1)
                if(c.getBlob(2) == null){
                    pictograma.idAPI = c.getInt(3)
                }else{
                    pictograma.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(2))
                }
                pictograma.categoria = c.getInt(4)
                pictograma.favorito = c.getInt(5) == 1 // Convert favorito value to Boolean
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()

        return listaPictogramas!!
    }


    fun insertarPictogramaLocal(actividad: Activity?, nombre: String?, imagen: ByteArray?, categoria: String?, idUsuario: String?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val id = conectorBD!!.insertarPictogramaLocal(nombre, imagen, categoria, idUsuario)
        conectorBD!!.cerrar()
        return id
    }

    fun insertarPictogramaAPI(actividad: Activity?, nombre: String?, id: String?, categoria: String?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val id = conectorBD!!.insertarPictogramaAPI(nombre, id, categoria)
        conectorBD!!.cerrar()
        return id
    }

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
                if(c.getBlob(2) == null){
                    pictograma.idAPI = c.getInt(3)
                }else{
                    pictograma.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(2))
                }
                pictograma.categoria = 5
                pictograma.favorito = true
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaPictogramas!!
    }

    fun insertarFavorito(context: Context?, idUsuario: String?, id:String?, titulo: String?, idAPI: Int) {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        conectorBD!!.insertarFavorito(idUsuario, id, titulo, idAPI)
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
                if(c.getBlob(1) == null){
                    pictograma.idAPI = c.getInt(2)
                    // pictograma.imagen = CommonUtils.getImagenAPI(pictograma.idAPI)
                }else{
                    pictograma.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(1))
                }
                pictograma.categoria = c.getInt(3)
                pictograma.titulo = c.getString(4)
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
                if(c.getBlob(2) == null){
                    pictograma.idAPI = c.getInt(3)
                }else{
                    pictograma.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(2))
                }
                listaPictogramas!!.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaPictogramas!!

    }

    fun guardarHistoria(actividad: Activity?, id: String, idPicto: String?, historia: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarHistoria(id, idPicto, historia)
        conectorBD!!.cerrar()
    }

    fun guardarDuracion(actividad: Activity?, toString: String, id: String, s: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarDuracion(toString, id, s)
        conectorBD!!.cerrar()
    }

    fun guardarPictoEntretenimiento(actividad: Activity?, id: String, idPicto: String?, idEntretenimiento: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarPictoEntretenimiento(id, idPicto, idEntretenimiento)
        conectorBD!!.cerrar()
    }
}
package com.example.plantea.dominio.gestores

import android.app.Activity
import android.content.Context
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.CommonUtils
import java.io.Serializable

class GestionPictogramas : Serializable {
    private var conectorBD: ConectorBD? = null

    fun obtenerPictogramas(context: Context, idcategoria: Int, userId: String?, language: String): ArrayList<Pictograma> {
        conectorBD = ConectorBD(context)
        val listaPictogramas = ArrayList<Pictograma>()
        conectorBD!!.abrir()

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
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()

        return listaPictogramas
    }


    fun insertarPictogramaLocal(actividad: Activity?, nombre: String?, imagen: ByteArray?, categoria: String?, idUsuario: String?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val id = conectorBD!!.insertarPictogramaLocal(nombre, imagen, categoria, idUsuario)
        conectorBD!!.cerrar()
        return id
    }

    fun nuevoPictogramaAPI(actividad: Activity?, nombre: String?, idPicto: String?, categoria: String?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val id = conectorBD!!.insertarPictogramaAPI(nombre, idPicto, categoria)
        conectorBD!!.cerrar()
        return id
    }

    fun obtenerFavoritos(actividad: Context?, idUsuario: String?, language: String): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        val listaPictogramas = ArrayList<Pictograma>()
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
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaPictogramas
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
        val cursor = conectorBD!!.getFavorito(idPicto, idUsuario)
        val exists = cursor.moveToFirst()
        cursor.close()
        conectorBD!!.cerrar()
        return exists
    }

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
        val listaPictogramas = ArrayList<Pictograma>()
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
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return listaPictogramas
    }

    //idPicto exists in
    fun checkPictoAPI(actividad: Activity?, idPicto: Int?): String {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val cursor = conectorBD!!.checkPictoAPI(idPicto)
        val id = if(cursor.moveToFirst()) cursor.getString(0) else ""
        cursor.close()
        conectorBD!!.cerrar()
        return id
    }

    fun guardarHistoria(actividad: Activity?, id: String, idPicto: String?, historia: String?, posicion: Int?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarHistoria(id, idPicto, historia, posicion)
        conectorBD!!.cerrar()
    }

    fun guardarDuracion(actividad: Activity?, posicion: Int?, toString: String, id: String, s: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarDuracion(posicion, toString, id, s)
        conectorBD!!.cerrar()
    }

    fun guardarPictoEntretenimiento(actividad: Activity?, posicion: Int?, id: String, idPicto: String?, idEntretenimiento: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.insertarPictoEntretenimiento(id, posicion, idPicto, idEntretenimiento)
        conectorBD!!.cerrar()
    }

    fun editPictogramTitle(posicion: Int?, titulo: String?, idEvento: String?, idPictograma: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.modificarTituloPictogramaEvento(posicion, titulo, idEvento, idPictograma)
        conectorBD!!.cerrar()
    }
}
package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.CommonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class GestionPlanificaciones : Serializable {
    private lateinit var conectorBD: ConectorBD
    private lateinit var listaPlanes: ArrayList<Planificacion>
    private lateinit var listaPictogramas: ArrayList<Pictograma>

    fun insertarPlanificacion(actividad: Activity?, idUsuario: String, titulo: String?): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val idPlan: Int = conectorBD.insertarPlanificacion(idUsuario, titulo)
        conectorBD.cerrar()
        return idPlan
    }

    fun addPictogramasPlan(actividad: Activity?, idPlan: Int?, listaPlanificacion: ArrayList<Pictograma>): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        try{
            for(pictogram in listaPlanificacion){
                var idPicto = pictogram.id
                if(pictogram.idAPI != 0) {
                    idPicto = conectorBD.insertarPictogramaAPI(pictogram.titulo, pictogram.idAPI.toString(), null)
                }
                conectorBD.addPictogramasPlanificacion(idPlan, idPicto, pictogram.historia, pictogram.duracion, pictogram.pictoEntretenimiento)

            }
        }catch (e: Exception){
            Log.d("Warning", e.toString() )
            return false
        }
        conectorBD.cerrar()
        return true
    }

    fun addPictogramasPlanTraductor(actividad: Activity?, idPlan: Int?, listaPlanificacion: ArrayList<Pictograma>, idUsuario: String?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        try{
            for(pictogram in listaPlanificacion){
                var idPicto = pictogram.id
                if(pictogram.idAPI != 0) {
                    idPicto = conectorBD.insertarPictogramaAPI(pictogram.titulo, pictogram.idAPI.toString(), null)
                }else{
                    idPicto = conectorBD.insertarPictogramaLocal(pictogram.titulo, CommonUtils.bitmapToByteArray(pictogram.imagen), null, idUsuario)
                }
                conectorBD.addPictogramasPlanificacion(idPlan, idPicto, pictogram.historia, pictogram.duracion, pictogram.pictoEntretenimiento)

            }
        }catch (e: Exception){
            Log.d("Warning", e.toString() )
            return false
        }
        conectorBD.cerrar()
        return true
    }



    /*fun insertarPictogramaPlan(idPlan: Int, actividad: Activity?, idPicto: Int, idPictoAPI: Int): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        for (i in pictogramas.indices) {
            resultado = conectorBD.insertarPictogramaPlan(idPicto, idPictoAPI, historia, idPlan)
        }
        conectorBD.cerrar()
        return resultado
    }*/



    fun listarPlanificaciones(idUsuario: String, actividad: Activity?): ArrayList<*> {
        listaPlanes = ArrayList()
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val c = conectorBD.listarPlanificaciones(idUsuario)
        if (c.moveToFirst()) {
            do {
                val plan = Planificacion(c.getString(0), c.getInt(1))
                listaPlanes.add(plan)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaPlanes
    }

    fun eliminarPlanificacion(actividad: Activity?, idPlan: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val idEventos = mutableListOf<Int>()
        val cursor = conectorBD.obtenerIdEvento(idPlan)
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("id_evento")
            do {
                val idEvento = cursor.getInt(columnIndex)
                idEventos.add(idEvento)
            } while (cursor.moveToNext())
        }
        cursor.close()

        for (idEvento in idEventos) {
            conectorBD.borrarEventoFromPlanificacion(idEvento, idPlan)
        }

        conectorBD.borrarPlanificacion(idPlan)
        conectorBD.cerrar()
    }

    fun obtenerPictogramasPlanificacion(actividad: Activity?, idPlan: Int, language: String, idUsuario: String): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        listaPictogramas = ArrayList()
        conectorBD.abrir()
        val c = conectorBD.listarPictogramasPlanificacion(idPlan, language, idUsuario)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getInt(0).toString()
                pictograma.titulo = c.getString(1)
                if( c.getBlob(2) == null){
                    pictograma.idAPI = c.getInt(3)
                    // pictograma.imagen = CommonUtils.getImagenAPI(pictograma.idAPI)
                }else{
                    pictograma.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(2))
                }
                pictograma.categoria = c.getInt(4)
                pictograma.historia = c.getString(5)
                pictograma.duracion = c.getString(6)
                pictograma.pictoEntretenimiento = c.getInt(7)
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaPictogramas
    }

    fun actualizarPlanificacion(actividad: Activity?, idUsuario: String, idPlan: Int, nombre: String?, pictogramas: ArrayList<Pictograma>) {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        conectorBD.actualizarPlanificacion(idPlan, 1)

        val id = conectorBD.insertarPlanificacion(idUsuario, nombre)

        try{
            for(pictogram in pictogramas){
                conectorBD.addPictogramasPlanificacion(id, pictogram.id, pictogram.historia, pictogram.duracion, pictogram.pictoEntretenimiento)
            }
        }catch (e: Exception){
            Log.d("Warning", e.toString())
        }
        conectorBD.cerrar()
    }

    fun obtenerPictogramas(idUsuario: String, id: String, context: Context?, language: String): ArrayList<*> {
        conectorBD = ConectorBD(context)
        listaPictogramas = ArrayList()
        conectorBD.abrir()
        var idPlan = 0
        val c = conectorBD.obtenerPlanificacion(id, idUsuario)
        if (c.moveToFirst()) {
            idPlan = c.getInt(0)
                val c2 = conectorBD.listarPictogramasPlanificacion(idPlan, language, idUsuario)
                if (c2.moveToFirst()) {
                    do {
                        val pictograma = Pictograma()
                        pictograma.id = c2.getInt(0).toString()
                        pictograma.titulo = c2.getString(1)
                        if(c2.getBlob(2) == null){
                            pictograma.idAPI = c2.getInt(3)
                        }else{
                            pictograma.imagen = CommonUtils.byteArrayToBitmap(c2.getBlob(2))
                        }
                        pictograma.categoria = c2.getInt(4)
                        pictograma.historia = c2.getString(5)
                        pictograma.duracion = c2.getString(6)
                        pictograma.pictoEntretenimiento = c2.getInt(7)
                        listaPictogramas.add(pictograma)
                    }
                while (c2.moveToNext())
            }
            c2.close()
            }

        c.close()
        conectorBD.cerrar()
        return listaPictogramas
    }

}
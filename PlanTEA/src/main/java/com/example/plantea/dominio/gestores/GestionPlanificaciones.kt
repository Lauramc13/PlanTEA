package com.example.plantea.dominio.gestores

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.dominio.objetos.Planificacion
import com.example.plantea.persistencia.ConectorBD
import com.example.plantea.presentacion.actividades.CommonUtils
import java.io.Serializable

class GestionPlanificaciones : Serializable {
    private lateinit var conectorBD: ConectorBD

    fun crearPlanificacion(actividad: Activity?, idUsuario: String, titulo: String?): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val idPlan: Int = conectorBD.insertarPlanificacion(idUsuario, titulo)
        conectorBD.cerrar()
        return idPlan
    }

    fun addPictogramasPlan(idPlan: Int?, idUsuario: String?, listaPlanificacion: ArrayList<Pictograma>, actividad: Activity?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        try{
            for(pictogram in listaPlanificacion){
                var idPicto = pictogram.id
                if(pictogram.idAPI != 0 && pictogram.idAPI != -1) {
                    idPicto = conectorBD.insertarPictogramaAPI(pictogram.titulo, pictogram.idAPI.toString(), null, idUsuario)
                }else if(pictogram.idAPI == -1){
                    val imagen = CommonUtils.bitmapToByteArray(pictogram.imagen)
                    idPicto = conectorBD.insertarPictogramaLocal(pictogram.titulo, imagen, null, idUsuario)
                }
                conectorBD.addPictogramasPlanificacion(idPlan, idPicto)

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
                var idPicto = if(pictogram.idAPI != 0) {
                    conectorBD.insertarPictogramaAPI(pictogram.titulo, pictogram.idAPI.toString(), null, idUsuario)
                }else{
                    conectorBD.insertarPictogramaLocal(pictogram.titulo, CommonUtils.bitmapToByteArray(pictogram.imagen), null, idUsuario)
                }
                conectorBD.addPictogramasPlanificacion(idPlan, idPicto)

            }
        }catch (e: Exception){
            Log.d("Warning", e.toString() )
            return false
        }
        conectorBD.cerrar()
        return true
    }

    fun mostrarPlanificacionesDisponibles(idUsuario: String, actividad: Activity?): ArrayList<*> {
        val listaPlanes = ArrayList<Planificacion>()
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val c = conectorBD.listarPlanificaciones(idUsuario)
        if (c.moveToFirst()) {
            do {
                val plan = Planificacion(c.getInt(1).toString(), c.getString(0))
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
            conectorBD.borrarEventoSemana(idEvento.toString())
        }

        conectorBD.borrarPlanificacion(idPlan)
        conectorBD.cerrar()
    }

    fun obtenerPictogramasPlanificacionNEW(actividad: Activity?, idPlan: Int, language: String, idUsuario: String): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        val listaPictogramas = ArrayList<Pictograma>()
        conectorBD.abrir()
        val c = conectorBD.listarPictogramasPlanificacion(idPlan, language, idUsuario)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getInt(0).toString()
                pictograma.titulo = c.getString(1)
                if( c.getBlob(2) == null){
                    pictograma.idAPI = c.getInt(3)
                }else{
                    pictograma.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(2))
                }
                pictograma.categoria = c.getInt(4)
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaPictogramas
    }

    fun obtenerPictogramasEvento(context: Context?, idPlan: Int?, idEvento: Int?, language: String?, idUsuario: String?): ArrayList<Pictograma> {
        val pictos = obtenerPictogramasPlanificacionEvento(context, idPlan, idEvento, language, idUsuario)

        val indicesABorrar = pictos.mapIndexedNotNull { index, picto ->
            if (index > 0 && picto.isImprevisto) index - 1 else null
        }.toSet()

        val pictosFiltrados = pictos.filterIndexed { index, _ -> index !in indicesABorrar }
        return pictosFiltrados as ArrayList<Pictograma>
    }

    fun obtenerPictogramasPlanificacionEvento(context: Context?, idPlan: Int?, idEvento: Int?, language: String?, idUsuario: String?): ArrayList<Pictograma> {
        conectorBD = ConectorBD(context)
        conectorBD.abrir()

        val listaPictogramas = ArrayList<Pictograma>()
        val idPlan2 = if(idPlan == null){
            val cursor = conectorBD.obtenerPlanificacion(idEvento.toString(), idUsuario!!)
            if (cursor.moveToFirst())
                cursor.getInt(0) else 0
        }else{
            idPlan
        }

        val c = conectorBD.listarPictogramasPlanificacionEvento(idPlan2, idEvento, language, idUsuario)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getInt(0).toString()
                pictograma.titulo = c.getString(1)
                if( c.getBlob(2) == null){
                    pictograma.idAPI = c.getInt(3)
                }else{
                    pictograma.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(2))
                }
                pictograma.categoria = c.getInt(4)
                pictograma.historia = c.getString(5)
                pictograma.duracion = c.getString(6)
                pictograma.isImprevisto = c.getInt(7) == 1
                pictograma.pictoEntretenimiento = c.getInt(8)
                pictograma.posicion = c.getInt(9)
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
                conectorBD.addPictogramasPlanificacion(id, pictogram.id)
            }
        }catch (e: Exception){
            Log.d("Warning", e.toString())
        }
        conectorBD.cerrar()
    }

    fun obtenerTitulosPlanificaciones(idUsuario: String, actividad: Activity?): ArrayList<String> {
        val listaPlanificaciones = ArrayList<String>()
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val c = conectorBD.listarPlanificaciones(idUsuario)
        if (c.moveToFirst()) {
            do {
                listaPlanificaciones.add(c.getString(0))
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaPlanificaciones
    }

//    fun obtenerPictogramasPlanificacion(actividad: Activity?, idPlan: Int, language: String, idUsuario: String): ArrayList<Pictograma> {
//        conectorBD = ConectorBD(actividad)
//        val listaPictogramas = ArrayList<Pictograma>()
//        conectorBD.abrir()
//        val c = conectorBD.listarPictogramasPlanificacion(idPlan, language, idUsuario)
//        if (c.moveToFirst()) {
//            do {
//                val pictograma = Pictograma()
//                pictograma.id = c.getInt(0).toString()
//                pictograma.titulo = c.getString(1)
//                if( c.getBlob(2) == null){
//                    pictograma.idAPI = c.getInt(3)
//                }else{
//                    pictograma.imagen = CommonUtils.byteArrayToBitmap(c.getBlob(2))
//                }
//                pictograma.categoria = c.getInt(4)
//                pictograma.historia = c.getString(5)
//                pictograma.duracion = c.getString(6)
//                pictograma.pictoEntretenimiento = c.getInt(7)
//                listaPictogramas.add(pictograma)
//            } while (c.moveToNext())
//        }
//        c.close()
//        conectorBD.cerrar()
//        return listaPictogramas
//    }

}
package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.plantea.persistencia.ConectorBD
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
                if(pictogram.sourceAPI){
                    conectorBD.addPictogramaAPI(pictogram.id, pictogram.titulo, pictogram.imagen)
                    conectorBD.addPictogramasPlanificacion(idPlan, null, pictogram.id, pictogram.historia, pictogram.duracion)
                }else{
                    conectorBD.addPictogramasPlanificacion(idPlan, pictogram.id, null, pictogram.historia, pictogram.duracion)
                }
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
                val plan = Planificacion()
                plan.titulo = c.getString(0)
                plan.id = c.getInt(1)
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
        conectorBD.borrarPlanificacion(idPlan)
        conectorBD.cerrar()
    }

    fun obtenerPictogramasPlanificacion(actividad: Activity?, idPlan: Int): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        listaPictogramas = ArrayList()
        conectorBD.abrir()
        val c = conectorBD.listarPictogramasPlanificacion(idPlan)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.id = c.getInt(0).toString()
                pictograma.titulo = c.getString(1)
                pictograma.imagen = c.getString(2)
                pictograma.categoria = c.getInt(3)
                pictograma.historia = c.getString(4)
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaPictogramas
    }

    fun actualizarPlanificacion(actividad: Activity?, idPlan: Int, nombre: String?, pictogramas: ArrayList<Pictograma>) {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        conectorBD.actualizarPlanificacion(idPlan, nombre)
        try{
            for(pictogram in pictogramas){
                if(pictogram.sourceAPI){
                    conectorBD.addPictogramaAPI(pictogram.id, pictogram.titulo, pictogram.imagen)
                    conectorBD.addPictogramasPlanificacion(idPlan, null, pictogram.id, pictogram.historia, pictogram.duracion)
                }else{
                    conectorBD.addPictogramasPlanificacion(idPlan, pictogram.id, null, pictogram.historia, pictogram.duracion)
                }
            }
        }catch (e: Exception){
            Log.d("Warning", e.toString())
        }
        conectorBD.cerrar()
    }

    fun obtenerPictogramas(idUsuario: String, fecha: String, context: Context?): ArrayList<*> {

        conectorBD = ConectorBD(context)
        listaPictogramas = ArrayList()
        conectorBD.abrir()
        val c = conectorBD.obtenerPlanificacion(idUsuario, fecha)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.titulo = c.getString(0)
                pictograma.imagen = c.getString(1)
                pictograma.categoria = c.getInt(2)
                pictograma.historia = c.getString(3)
                pictograma.duracion = c.getString(4)
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaPictogramas
    }

    //Obtener el titulo de la planificacion a seguir
   /* fun obtenerTituloPlan(idUsuario: String, fecha: String, context: Context?): String {
        conectorBD = ConectorBD(context)

        conectorBD.abrir()
        var titulo = " " // set a default value
        val c = conectorBD.listarTituloPlan(idUsuario, fecha)
        if (c.moveToFirst()) {
            titulo = c.getString(0)
        }
        conectorBD.cerrar()
        return titulo
    }*/


}
package com.example.plantea.dominio

import android.app.Activity
import com.example.plantea.persistencia.ConectorBD
import java.io.Serializable

class GestionPlanificaciones : Serializable {
    private lateinit var conectorBD: ConectorBD
    private var resultado: Boolean = false
    private lateinit var listaPlanes: ArrayList<Planificacion>
    private lateinit var listaPictogramas: ArrayList<Pictograma>
    fun insertarPictogramaPlan(idUsuario: String, actividad: Activity?, pictogramas: java.util.ArrayList<Pictograma>, titulo: String?): Boolean {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        val id_plan: Int = conectorBD.insertarPlanificacion(idUsuario, titulo)
        for (i in pictogramas.indices) {
            resultado = conectorBD.insertarPictogramaPlan(pictogramas[i].titulo,
                pictogramas[i].imagen, pictogramas[i].categoria, id_plan)
        }
        conectorBD.cerrar()
        return resultado
    }

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

    fun eliminarPlanificacion(actividad: Activity?, id_plan: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        conectorBD.borrarPlanificacion(id_plan)
        conectorBD.cerrar()
    }

    fun obtenerPictogramasPlanificacion(actividad: Activity?, id_plan: Int): ArrayList<Pictograma> {
        conectorBD = ConectorBD(actividad)
        listaPictogramas = ArrayList()
        conectorBD.abrir()
        val c = conectorBD.listarPictogramasPlanificacion(id_plan)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.titulo = c.getString(0)
                pictograma.imagen = c.getString(1)
                pictograma.categoria = c.getInt(2)
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaPictogramas
    }

    fun actualizarPlanificacion(actividad: Activity?, id_plan: Int, nombre: String?, pictogramas: ArrayList<Pictograma>) {
        conectorBD = ConectorBD(actividad)
        conectorBD.abrir()
        conectorBD.actualizarPlanificacion(id_plan, nombre)
        for (i in pictogramas.indices) {
            conectorBD.insertarPictogramaPlan(pictogramas[i].titulo,
                pictogramas[i].imagen, pictogramas[i].categoria, id_plan)
        }
        conectorBD.cerrar()
    }

    fun obtenerPictogramas(idUsuario: String, actividad: Activity?): ArrayList<*> {

        conectorBD = ConectorBD(actividad)
        listaPictogramas = ArrayList()
        conectorBD.abrir()
        val c = conectorBD.obtenerPlanficacion(idUsuario)
        if (c.moveToFirst()) {
            do {
                val pictograma = Pictograma()
                pictograma.titulo = c.getString(0)
                pictograma.imagen = c.getString(1)
                pictograma.categoria = c.getInt(2)
                listaPictogramas.add(pictograma)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD.cerrar()
        return listaPictogramas
    }

    //Obtener el titulo de la planificacion a seguir
    fun obtenerTituloPlan(idUsuario: String, actividad: Activity?): String {
        conectorBD = ConectorBD(actividad)

        conectorBD.abrir()
        var titulo = " " // set a default value
        val c = conectorBD.listarTituloPlan(idUsuario)
        if (c.moveToFirst()) {
            titulo = c.getString(0)
        }
        conectorBD.cerrar()
        return titulo
    }
}
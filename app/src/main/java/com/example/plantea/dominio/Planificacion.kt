package com.example.plantea.dominio

import android.app.Activity
import java.io.Serializable

class Planificacion : Serializable {
    lateinit var titulo: String
    var id = 0
    private var resultado = false
    lateinit var listaPlanes: ArrayList<Planificacion>
    lateinit var listaPictogramas: ArrayList<Pictograma>
    var gestionPlan = GestionPlanificaciones()

    constructor() {}
    constructor(titulo: String, id: Int) {
        this.titulo = titulo
        this.id = id
    }

    fun crearPlanificacion(actividad: Activity?, pictogramas: java.util.ArrayList<Pictograma>, titulo: String?): Boolean {
        resultado = gestionPlan.insertarPictogramaPlan(actividad, pictogramas, titulo)
        return resultado
    }

    fun mostrarPlanificacionesDisponibles(actividad: Activity?): ArrayList<*>? {
        listaPlanes = ArrayList()
        listaPlanes = gestionPlan.listarPlanificaciones(actividad) as ArrayList<Planificacion>
        return listaPlanes
    }

    fun eliminarPlanificacion(actividad: Activity?, id_plan: Int) {
        gestionPlan.eliminarPlanificacion(actividad, id_plan)
    }

    fun obtenerPictogramasPlanificacion(actividad: Activity?, id_plan: Int): ArrayList<*>? {
        listaPictogramas = ArrayList()
        listaPictogramas = gestionPlan.obtenerPictogramasPlanificacion(actividad, id_plan)
        return listaPictogramas
    }

    fun actualizarPlanificacion(actividad: Activity?, id_plan: Int, nombre: String?, pictogramas: ArrayList<Pictograma>) {
        gestionPlan.actualizarPlanificacion(actividad, id_plan, nombre, pictogramas)
    }

    //Mostrar la planificacion a seguir
    fun mostrarPlanificacion(actividad: Activity?): ArrayList<*> {
        listaPlanes = ArrayList()


        listaPlanes = gestionPlan.obtenerPictogramas(actividad) as ArrayList<Planificacion>
        return listaPlanes
    }

    //Obtener el titulo de la planificacion a seguir
    fun obtenerTituloPlan(actividad: Activity?): String {
        titulo = gestionPlan.obtenerTituloPlan(actividad)
        return titulo
    }
}
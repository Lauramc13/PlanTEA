package com.example.plantea.dominio

import android.app.Activity
import java.io.Serializable

class Planificacion : Serializable {
    lateinit var titulo: String
    var id = 0
    private var resultado = false
    private lateinit var listaPlanes: ArrayList<Planificacion>
    lateinit var listaPictogramas: ArrayList<Pictograma>
    private var gestionPlan = GestionPlanificaciones()

    constructor()
    constructor(titulo: String, id: Int) {
        this.titulo = titulo
        this.id = id
    }

    fun crearPlanificacion(actividad: Activity?,idUsuario: String, titulo: String?): Int {
        return gestionPlan.insertarPlanificacion(actividad, idUsuario, titulo)
    }

    fun addPictogramasPlan(idPlan: Int?,actividad: Activity?, listaPlanificacion: ArrayList<Pictograma>): Any {
        return gestionPlan.addPictogramasPlan(actividad, idPlan, listaPlanificacion)
    }

    fun mostrarPlanificacionesDisponibles(idUsuario: String, actividad: Activity?): ArrayList<*> {
        listaPlanes = ArrayList()
        listaPlanes = gestionPlan.listarPlanificaciones(idUsuario, actividad) as ArrayList<Planificacion>
        return listaPlanes
    }

    fun eliminarPlanificacion(actividad: Activity?, id_plan: Int) {
        gestionPlan.eliminarPlanificacion(actividad, id_plan)
    }

    fun obtenerPictogramasPlanificacion(actividad: Activity?, id_plan: Int): ArrayList<*> {
        listaPictogramas = ArrayList()
        listaPictogramas = gestionPlan.obtenerPictogramasPlanificacion(actividad, id_plan)
        return listaPictogramas
    }

    fun actualizarPlanificacion(actividad: Activity?, idPlan: Int, nombre: String?, pictogramas: ArrayList<Pictograma>) {
        gestionPlan.actualizarPlanificacion(actividad, idPlan, nombre, pictogramas)
    }

    //Mostrar la planificacion a seguir
    fun mostrarPlanificacion(idUsuario: String, fecha: String, actividad: Activity?): ArrayList<*> {
        listaPlanes = ArrayList()
        listaPlanes = gestionPlan.obtenerPictogramas(idUsuario, fecha, actividad) as ArrayList<Planificacion>
        return listaPlanes
    }

    //Obtener el titulo de la planificacion a seguir
    fun obtenerTituloPlan(idUsuario: String, fecha: String, actividad: Activity?): String {
        titulo = gestionPlan.obtenerTituloPlan(idUsuario, fecha, actividad)
        return titulo
    }


}
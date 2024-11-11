package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import java.io.Serializable

class Planificacion : Serializable {
    private lateinit var titulo: String
    private var id = 0
    lateinit var listaPictogramas: ArrayList<Pictograma>
    private var gestionPlan = GestionPlanificaciones()

    data class PlanificacionItem(val title: String, var date: String)

    constructor()

    constructor(titulo: String, id: Int) {
        this.titulo = titulo
        this.id = id
    }

    // Getters
    fun getTitulo() = titulo
    fun getId() = id

    fun setTitulo(titulo: String) {
        this.titulo = titulo
    }

    fun crearPlanificacion(actividad: Activity?,idUsuario: String, titulo: String?): Int {
        return gestionPlan.insertarPlanificacion(actividad, idUsuario, titulo)
    }

    fun addPictogramasPlan(idPlan: Int?,actividad: Activity?, idUsuario: String?, listaPlanificacion: ArrayList<Pictograma>): Any {
        return gestionPlan.addPictogramasPlan(actividad, idPlan, idUsuario, listaPlanificacion)
    }

    fun addPictogramasPlanTraductor(idPlan: Int?, actividad: Activity?, listaPlanificacion: ArrayList<Pictograma>, idUsuario: String?): Any {
        return gestionPlan.addPictogramasPlanTraductor(actividad, idPlan, listaPlanificacion, idUsuario)
    }

    fun mostrarPlanificacionesDisponibles(idUsuario: String, actividad: Activity?): ArrayList<*> {
       // return gestionPlan.listarPlanificaciones(idUsuario, actividad) as ArrayList<Planificacion>
        return gestionPlan.listarPlanificaciones(idUsuario, actividad)
    }

    fun eliminarPlanificacion(actividad: Activity?, idPlan: Int) {
        gestionPlan.eliminarPlanificacion(actividad, idPlan)
    }

    fun obtenerPictogramasPlanificacion(actividad: Activity?, idPlan: Int, language: String, idUsuario: String): ArrayList<Pictograma> {
        return gestionPlan.obtenerPictogramasPlanificacion(actividad, idPlan, language, idUsuario)
    }

    fun actualizarPlanificacion(actividad: Activity?, idUsuario: String, idPlan: Int, nombre: String?, pictogramas: ArrayList<Pictograma>) {
        gestionPlan.actualizarPlanificacion(actividad, idUsuario, idPlan, nombre, pictogramas)
    }

    //Mostrar la planificacion a seguir
    fun mostrarPlanificacion(idUsuario: String, id: String, context: Context?, language: String): ArrayList<*> {
        return gestionPlan.obtenerPictogramas(idUsuario, id, context, language) as ArrayList<Planificacion>
    }

    fun obtenerTitulosPlanificaciones(idUsuario: String, actividad: Activity?): ArrayList<String> {
        return gestionPlan.obtenerTitulosPlanificaciones(idUsuario, actividad)
    }



}
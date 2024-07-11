package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import java.time.LocalDate

class Evento {
    var id = 0
    var idUsuario: String? = null
    var nombre: String? = null
    var fecha: LocalDate? = null
    var hora: String? = null
    var id_plan = 0
    var visible = 0
    var cambiar_visibilidad = false
    private var gestorEventos = GestionEventos()

    constructor()
    constructor(id: Int, idUsuario: String, nombre: String?, fecha: LocalDate?, hora: String?, idPlan: Int, cambiarVisibilidad: Boolean) {
        this.id = id
        this.idUsuario = idUsuario
        this.nombre = nombre
        this.fecha = fecha
        this.hora = hora
        this.id_plan = idPlan
        this.cambiar_visibilidad = cambiarVisibilidad
    }

    fun obtenerEventos(idUsuario: String, actividad: Activity?, fechaSeleccionada: LocalDate): ArrayList<*> {
        listaEventos = gestorEventos.listarEventos(actividad, idUsuario)
        //obtener eventos para una fecha determinada
        val eventos = ArrayList<Evento>()
        for (evento in listaEventos!!) {
            if (evento.fecha == fechaSeleccionada) {
                eventos.add(evento)
            }
        }
        return eventos
    }

    fun obtenerTodosEventos(idUsuario: String, actividad: Activity?): ArrayList<*> {
        listaEventos = gestorEventos.listarEventos(actividad, idUsuario)
        return listaEventos as ArrayList<Evento>
    }

    fun crearEvento(actividad: Activity?, evento: Evento): Int {
        return gestorEventos.crearEvento(actividad, evento)
    }

    fun eliminarEvento(actividad: Activity?, idEvento: Int) {
        gestorEventos.eliminarEvento(actividad, idEvento)
    }

    fun cambiarVisibilidad(actividad: Context?, valor: Int, idEvento: Int) {
        gestorEventos.cambiarVisibilidad(actividad, valor, idEvento)
    }

    fun invisibiliarEvento(actividad: Context?, idEvento: Int , idUsuario: String?) {
        gestorEventos.invisibiliarEvento(actividad, idEvento, idUsuario!!)
    }

    //Comprobar el numero de eventos visibles
    fun comprobarEventoVisible(userId: String, fecha: String, actividad: Activity?): Int {
        return gestorEventos.comprobarEventoVisible(userId, fecha, actividad)
    }

    fun obtenerEventoPlan(idUsuario: String, fecha: String, context: Context?): Evento {
        return gestorEventos.obtenerTituloEvento(idUsuario, fecha, context)
    }

    fun checkEventosDia(idUsuario: String, fecha: String, context: Context?): Int {
        return gestorEventos.checkEventosDia(idUsuario, fecha, context)
    }

    companion object {
        @JvmField
        var listaEventos: ArrayList<Evento>? = ArrayList()
    }
}
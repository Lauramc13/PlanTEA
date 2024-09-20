package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime

class Evento {
    var id = 0
    var idUsuario: String? = null
    var nombre: String? = null
    var fecha: LocalDate? = null
    var hora: String? = null
    var visible = 0
    var cambiarVisibilidad = false
    var reminder : LocalDateTime? = null
    var idPlan = 0

    private var gestorEventos = GestionEventos()

    constructor()
    constructor(id: Int, idUsuario: String, nombre: String?, fecha: LocalDate?, hora: String?, planId: Int, cambiarVisibilidad: Boolean, reminder : LocalDateTime?) {
        this.id = id
        this.idUsuario = idUsuario
        this.nombre = nombre
        this.fecha = fecha
        this.hora = hora
        this.idPlan = planId
        this.cambiarVisibilidad = cambiarVisibilidad
        this.reminder = reminder
    }

    fun obtenerEventos(idUsuario: String, actividad: Activity?, fechaSeleccionada: LocalDate): ArrayList<Evento> {
       val listaEventos = gestorEventos.listarEventos(actividad, idUsuario)
        //obtener eventos para una fecha determinada
        val eventos = ArrayList<Evento>()
        for (evento in listaEventos) {
            if (evento.fecha == fechaSeleccionada) {
                eventos.add(evento)
            }
        }
        return eventos
    }

    fun obtenerInfoEvento(idEvento: Int, actividad: Activity?): Evento {
        return gestorEventos.obtenerInfoEvento(actividad, idEvento)
    }

    fun obtenerTodosEventos(idUsuario: String, actividad: Activity?): ArrayList<Evento> {
        return gestorEventos.listarEventos(actividad, idUsuario)
    }

    fun crearEvento(actividad: Activity?, evento: Evento): Int {
        return gestorEventos.crearEvento(actividad, evento)
    }

    fun editarEvento(actividad: Activity?, evento: Evento): Int {
        return gestorEventos.editarEvento(actividad, evento)
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

}
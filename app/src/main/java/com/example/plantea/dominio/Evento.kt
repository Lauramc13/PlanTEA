package com.example.plantea.dominio

import android.app.Activity
import java.time.LocalDate

class Evento {
    var id = 0
    var nombre: String? = null
    var fecha: LocalDate? = null
    var hora: String? = null
    var imagen: String? = null
    var id_plan = 0
    var visible = 0
    private var contador = 0
    private var identificador = 0
    var gestorEventos = GestionEventos()

    constructor() {}
    constructor(id: Int, nombre: String?, fecha: LocalDate?, hora: String?, id_plan: Int, imagen: String?) {
        this.id = id
        this.nombre = nombre
        this.fecha = fecha
        this.hora = hora
        this.id_plan = id_plan
        this.imagen = imagen
    }

    fun obtenerEventos(actividad: Activity?, fechaSeleccionada: LocalDate): ArrayList<*> {
        listaEventos = gestorEventos.listarEventos(actividad)
        //obtener eventos para una fecha determinada
        val eventos = ArrayList<Evento>()
        for (evento in listaEventos!!) {
            if (evento.fecha == fechaSeleccionada) {
                eventos.add(evento)
            }
        }
        return eventos
    }

    fun crearEvento(actividad: Activity?, evento: Evento): Int {
        identificador = gestorEventos.crearEvento(actividad, evento)
        return identificador
    }

    fun eliminarEvento(actividad: Activity?, id_evento: Int) {
        gestorEventos.eliminarEvento(actividad, id_evento)
    }

    fun cambiarVisibilidad(actividad: Activity?, valor: Int, id_evento: Int) {
        gestorEventos.cambiarVisibilidad(actividad, valor, id_evento)
    }

    //Comprobar el numero de eventos visibles
    fun comprobarEventosVisible(actividad: Activity?): Int {
        contador = 0
        contador = gestorEventos.comprobarEventosVisible(actividad)
        return contador
    }

    companion object {
        @JvmField
        var listaEventos: ArrayList<Evento>? = ArrayList()
    }
}
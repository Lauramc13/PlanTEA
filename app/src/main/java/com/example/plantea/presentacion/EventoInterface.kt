package com.example.plantea.presentacion

import com.example.plantea.dominio.Evento

interface EventoInterface {
    //EventosFragment
    fun crearEventoFragment()

    //NuevoEventoFragment
    fun nuevoEvento(evento: Evento)
    fun planificar()
    fun cancelarEvento()
    fun cancelarNotificacion(identificador: Int)
}
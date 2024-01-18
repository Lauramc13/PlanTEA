package com.example.plantea.presentacion

import android.content.Context
import com.example.plantea.dominio.Evento

interface EventoInterface {
    //EventosFragment
    fun crearEventoFragment(context: Context)

    //NuevoEventoFragment
    fun nuevoEvento(context: Context, evento: Evento)
    fun planificar(context: Context)
    fun cancelarEvento(context: Context)
    fun cancelarNotificacion(context: Context, identificador: Int)

    fun clickReloj(tiempo: CharSequence?)
}
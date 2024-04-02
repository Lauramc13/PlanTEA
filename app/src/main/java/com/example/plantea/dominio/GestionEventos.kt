package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import com.example.plantea.persistencia.ConectorBD
import java.time.LocalDate

class GestionEventos {
    private var conectorBD: ConectorBD? = null
    private var eventos: ArrayList<Evento>? = null

    fun crearEvento(actividad: Activity?, evento: Evento): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val identificador = conectorBD!!.insertarCita(evento.idUsuario, evento.nombre, evento.fecha.toString(), evento.hora, evento.id_plan)
        conectorBD!!.cerrar()
        return identificador
    }

    fun listarEventos(actividad: Activity?, idUsuario: String): ArrayList<Evento> {
        eventos = ArrayList()
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val c = conectorBD!!.listarEventosPorUsuario(idUsuario)
        if (c.moveToFirst()) {
            do {
                val evento = Evento()
                evento.id = c.getInt(0)
                evento.nombre = c.getString(2)
                evento.fecha = LocalDate.parse(c.getString(3))
                evento.hora = c.getString(4)
                evento.id_plan = c.getInt(5)
                evento.visible = c.getInt(6)
                eventos!!.add(evento)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return eventos!!
    }

    fun eliminarEvento(actividad: Activity?, idEvento: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.eliminarEvento(idEvento)
        conectorBD!!.cerrar()
    }

    fun cambiarVisibilidad(actividad: Activity?, valor: Int, idEvento: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.modificarVisibilidad(valor, idEvento)
        conectorBD!!.cerrar()
    }

    //Comprobar el numero de eventos visibles
    fun comprobarEventoVisible(userId: String, fecha: String, actividad: Activity?): Int {
         var idEvento = 0
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val c = conectorBD!!.contarEventoVisible(userId, fecha)
        if (c.moveToFirst()) {
            idEvento = c.getInt(0)
        }
        conectorBD!!.cerrar()
        return idEvento
    }

    fun obtenerTituloEvento(idUsuario: String, fecha: String, context: Context?): Evento {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        val c = conectorBD!!.listarTituloEvento(idUsuario, fecha)
        val evento = Evento()

        if (c.moveToFirst()) {
            evento.nombre = c.getString(0)
        }
        conectorBD!!.cerrar()
        return evento
    }
}
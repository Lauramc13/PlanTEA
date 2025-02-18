package com.example.plantea.dominio

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.plantea.persistencia.ConectorBD
import java.time.LocalDate
import java.time.LocalDateTime

class GestionEventos {
    private var conectorBD: ConectorBD? = null
    private var eventos: ArrayList<Evento>? = null

    fun crearEvento(actividad: Activity?, evento: Evento): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val identificador = conectorBD!!.insertarEvento(evento.idUsuario, evento.nombre, evento.fecha.toString(), evento.hora, evento.reminder.toString(), evento.cambiarVisibilidad)
        conectorBD!!.insertarCitaEvento(identificador, evento.idPlan)
        conectorBD!!.insertarPictosEvento(identificador, evento.idPlan)
        conectorBD!!.cerrar()
        return identificador
    }

    fun editarEvento(actividad: Activity?, evento: Evento): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.modificarEvento(evento.id, evento.nombre, evento.fecha.toString(), evento.hora, evento.cambiarVisibilidad, evento.reminder.toString(), evento.idPlan)
        conectorBD!!.cerrar()
        return evento.id
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
                evento.idPlan = c.getInt(5)
                evento.visible = c.getInt(6)
                if(c.getString(7) != null && c.getString(7) != "null")
                    evento.reminder = LocalDateTime.parse(c.getString(7))
                evento.cambiarVisibilidad = c.getInt(8) == 1

                eventos!!.add(evento)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return eventos!!
    }

    fun obtenerInfoEvento(actividad: Activity?, idEvento: Int): Evento {
        val evento = Evento()
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val c = conectorBD!!.obtenerInfoEvento(idEvento)
        if (c.moveToFirst()) {
            evento.id = c.getInt(0)
            evento.idUsuario = c.getString(1)
            evento.nombre = c.getString(2)
            evento.fecha = LocalDate.parse(c.getString(3))
            evento.hora = c.getString(4)
            evento.visible = c.getInt(5)
            if(c.getString(6) != null && c.getString(6) != "null")
                evento.reminder = LocalDateTime.parse(c.getString(6))
            evento.cambiarVisibilidad = c.getString(7) == "true"
            evento.idPlan = c.getInt(8)
        }

        conectorBD!!.cerrar()
        return evento
    }

    fun eliminarEvento(actividad: Activity?, idEvento: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.eliminarEvento(idEvento)
        conectorBD!!.cerrar()
    }

    fun cambiarVisibilidad(context: Context?, valor: Int, idEvento: Int) {
        Log.d("AlarmScheduler", "cambiarVisibilidad: $idEvento")
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        conectorBD!!.modificarVisibilidad(valor, idEvento)
        conectorBD!!.cerrar()
    }

    fun invisibiliarEvento(actividad: Context?, idEvento: Int, idUsuario: String) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.invisibiliarEvento(idEvento, idUsuario)
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
            evento.id = c.getInt(0)
            evento.nombre = c.getString(1)
        }
        conectorBD!!.cerrar()
        return evento
    }

    fun checkEventosDia(idUsuario: String, fecha: String, context: Context?): Int {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        val c = conectorBD!!.existsVisibleEventoDia(idUsuario, fecha)
        if (c.moveToFirst()) {
            return c.getInt(0)
        }
        conectorBD!!.cerrar()
        return -1
    }

    fun editPictogramTitle(posicion: Int?, titulo: String?, idEvento: String?, idPictograma: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.modificarTituloPictogramaEvento(posicion, titulo, idEvento, idPictograma)
        conectorBD!!.cerrar()
    }

    fun eliminarPictoEvento(posicion: Int?, idEvento: String?, idPictograma: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.eliminarPictoEvento(posicion, idEvento, idPictograma)
        conectorBD!!.cerrar()
    }

    fun actualizarPosicionPictoEvento(posicionNew: Int?, posicionOld: Int?, idEvento: String?, idPictograma: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.actualizarPosicionPictoEvento(posicionNew, posicionOld, idEvento, idPictograma)
        conectorBD!!.cerrar()
    }
}
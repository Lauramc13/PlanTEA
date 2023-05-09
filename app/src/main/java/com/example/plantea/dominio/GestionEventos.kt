package com.example.plantea.dominio

import android.app.Activity
import android.util.Log
import com.example.plantea.persistencia.ConectorBD
import java.time.LocalDate

class GestionEventos {
    private var conectorBD: ConectorBD? = null
    private var contador = 0
    private var identificador = 0
    private var eventos: ArrayList<Evento>? = null
    fun crearEvento(actividad: Activity?, evento: Evento): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        identificador = conectorBD!!.insertarCita(evento.idUsuario, evento.nombre, evento.fecha.toString(), evento.hora, evento.id_plan, evento.imagen)
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
                evento.imagen = c.getString(6)
                evento.visible = c.getInt(7)
                eventos!!.add(evento)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()
        return eventos!!
    }

    fun eliminarEvento(actividad: Activity?, id_evento: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.eliminarEvento(id_evento)
        conectorBD!!.cerrar()
    }

    fun cambiarVisibilidad(actividad: Activity?, valor: Int, id_evento: Int) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.modificarVisibilidad(valor, id_evento)
        conectorBD!!.cerrar()
    }

    //Comprobar el numero de eventos visibles
    fun comprobarEventosVisible(userId: String, actividad: Activity?): Int {
        contador = 0
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val c = conectorBD!!.contarEventoVisible(userId)
        if (c.moveToFirst()) {
            contador = c.getInt(0)
            Log.d("Tag", contador.toString())
            Log.d("Tag", userId)

        }
        conectorBD!!.cerrar()
        return contador
    }
}
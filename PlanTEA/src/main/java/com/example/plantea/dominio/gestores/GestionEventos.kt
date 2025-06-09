package com.example.plantea.dominio.gestores

import android.app.Activity
import android.content.Context
import com.example.plantea.dominio.objetos.Evento
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.persistencia.ConectorBD
import java.time.LocalDate
import java.time.LocalDateTime

class GestionEventos {
    private var conectorBD: ConectorBD? = null

    fun crearEvento(actividad: Activity?, evento: Evento): Int {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        var id = 0

        val cursor = conectorBD!!.insertarEvento(evento.idUsuario, evento.nombre, evento.fecha.toString(), evento.horaInicio, evento.horaFin, evento.localizacion, evento.notas, evento.recordatorio.toString(), evento.cambiarVisibilidad)
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        cursor.close()

        conectorBD!!.insertarCitaEvento(id, evento.idPlan)
        conectorBD!!.insertarPictosEvento(id, evento.idPlan)
        conectorBD!!.cerrar()
        return id
    }

    fun editarEvento(actividad: Activity?, evento: Evento) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.modificarEvento(evento.id, evento.nombre, evento.fecha.toString(), evento.horaInicio, evento.horaFin, evento.localizacion, evento.notas, evento.cambiarVisibilidad, evento.recordatorio.toString(), evento.idPlan)
        conectorBD!!.cerrar()
    }

    fun obtenerEventos(idUsuario: String?, actividad: Activity?, fechaSeleccionada: LocalDate?): ArrayList<Evento> {
        val eventos = ArrayList<Evento>()
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val c = conectorBD!!.listarEventosPorUsuario(idUsuario)
        if (c.moveToFirst()) {
            do {
                val evento = Evento()
                evento.id = c.getInt(0)
                evento.nombre = c.getString(2)
                evento.fecha = LocalDate.parse(c.getString(3))
                evento.horaInicio = c.getString(4)
                evento.horaFin = c.getString(5)
                evento.localizacion = c.getString(6)
                evento.notas = c.getString(7)
                evento.idPlan = c.getInt(8)
                evento.visible = c.getInt(9)
                if(c.getString(10) != null && c.getString(10) != "null")
                    evento.recordatorio = LocalDateTime.parse(c.getString(10))
                evento.cambiarVisibilidad = c.getInt(11) == 1

                eventos.add(evento)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()

        val listaEventos = ArrayList<Evento>()
        for (evento in eventos) {
            if (evento.fecha == fechaSeleccionada) {
                listaEventos.add(evento)
                //before it was eventos.add(evento)
            }
        }

        return listaEventos
    }

    fun obtenerTodosEventos(idUsuario: String, actividad: Activity?): ArrayList<Evento> {
        val eventos = ArrayList<Evento>()
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val c = conectorBD!!.listarEventosPorUsuario(idUsuario)
        if (c.moveToFirst()) {
            do {
                val evento = Evento()
                evento.id = c.getInt(0)
                evento.nombre = c.getString(2)
                evento.fecha = LocalDate.parse(c.getString(3))
                evento.horaInicio = c.getString(4)
                evento.horaFin = c.getString(5)
                evento.localizacion = c.getString(6)
                evento.notas = c.getString(7)
                evento.idPlan = c.getInt(8)
                evento.visible = c.getInt(9)
                if(c.getString(10) != null && c.getString(10) != "null")
                    evento.recordatorio = LocalDateTime.parse(c.getString(10))
                evento.cambiarVisibilidad = c.getInt(11) == 1

                eventos.add(evento)
            } while (c.moveToNext())
        }
        c.close()
        conectorBD!!.cerrar()

        return eventos
    }

    fun obtenerInfoEvento(idEvento: Int?, actividad: Activity?): Evento {
        val evento = Evento()
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val c = conectorBD!!.obtenerInfoEvento(idEvento)
        if (c.moveToFirst()) {
            evento.id = c.getInt(0)
            evento.idUsuario = c.getString(1)
            evento.nombre = c.getString(2)
            evento.fecha = LocalDate.parse(c.getString(3))
            evento.horaInicio = c.getString(4)
            evento.horaFin = c.getString(5)
            evento.visible = c.getInt(6)
            if(c.getString(7) != null && c.getString(7) != "null")
                evento.recordatorio = LocalDateTime.parse(c.getString(7))
            evento.cambiarVisibilidad = c.getString(8) == "true"
            evento.idPlan = c.getInt(9)
        }

        conectorBD!!.cerrar()
        return evento
    }

    fun eliminarEvento(actividad: Activity?, idEvento: Int?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.eliminarEvento(idEvento)
        conectorBD!!.cerrar()
    }

    fun cambiarVisibilidad(context: Context?, valor: Int, idEvento: Int?) {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        conectorBD!!.modificarVisibilidad(valor, idEvento)
        conectorBD!!.cerrar()
    }

    fun invisibilizarEvento(actividad: Context?, idEvento: Int, idUsuario: String?) {
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

    fun obtenerEventoPlan(idUsuario: String, fecha: String, context: Context?): Evento {
        conectorBD = ConectorBD(context)
        conectorBD!!.abrir()
        val c = conectorBD!!.listarEvento(idUsuario, fecha)
        val evento = Evento()

        if (c.moveToFirst()) {
            evento.id = c.getInt(0)
            evento.nombre = c.getString(1)
            evento.fecha = LocalDate.parse(c.getString(2))
            evento.horaInicio = c.getString(3)
            evento.horaFin = c.getString(4)
            evento.localizacion = c.getString(5)
            evento.notas = c.getString(6)
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

    fun eliminarPictoEvento(posicion: Int?, idEvento: String?, idPictograma: String?, actividad: Activity?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.eliminarPictoEvento(posicion, idEvento, idPictograma)
        conectorBD!!.cerrar()
    }

    fun aniadirImprevisto(actividad: Activity?, picto: Pictograma?, idEvento: String?, posicion: Int?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        val imprevisto = if(picto!!.isImprevisto) 1 else 0
        conectorBD!!.aniadirImprevisto(posicion, picto!!.duracion, picto.historia, imprevisto, picto.pictoEntretenimiento, idEvento, picto.id)
        conectorBD!!.cerrar()
    }

    fun actualizarImprevisto(actividad: Activity?, idPictograma: String?, idEvento: String?, posicion: Int?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.actualizarImprevisto(posicion, idEvento, idPictograma)
        conectorBD!!.cerrar()
    }

    fun borrarTodosImprevistos(actividad: Activity?, idEvento: String?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.borrarTodosImprevistos(idEvento)
        conectorBD!!.cerrar()
    }

    fun borrarImprevisto(actividad: Activity?, idEvento: String?, idPictograma: String?, posicion: Int?) {
        conectorBD = ConectorBD(actividad)
        conectorBD!!.abrir()
        conectorBD!!.borrarImprevisto(idEvento, idPictograma, posicion)
        conectorBD!!.cerrar()
    }

//    fun actualizarPosicionPictoEvento(posicionNew: Int?, posicionOld: Int?, idEvento: String?, idPictograma: String?, actividad: Activity?) {
//        conectorBD = ConectorBD(actividad)
//        conectorBD!!.abrir()
//        conectorBD!!.actualizarPosicionPictoEvento(posicionNew, posicionOld, idEvento, idPictograma)
//        conectorBD!!.cerrar()
//    }
}
package com.example.plantea.dominio;

import android.app.Activity;
import android.database.Cursor;

import com.example.plantea.persistencia.ConectorBD;

import java.time.LocalDate;
import java.util.ArrayList;

public class GestionEventos {
    private ConectorBD conectorBD;
    private int contador, identificador;
    ArrayList<Evento> eventos;

    public int crearEvento(Activity actividad, Evento evento){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        identificador = conectorBD.insertarCita(evento.getNombre(), evento.getFecha().toString(), evento.getHora(), evento.getId_plan(), evento.getImagen());
        conectorBD.cerrar();
        return identificador;
    }

    public ArrayList listarEventos(Activity actividad){
        eventos = new ArrayList<>();
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        Cursor c = conectorBD.listarEventos();
        if (c.moveToFirst())
        {
            do {
                Evento evento = new Evento();
                evento.setId(c.getInt(0));
                evento.setNombre(c.getString(1));
                evento.setFecha(LocalDate.parse(c.getString(2)));
                evento.setHora(c.getString(3));
                evento.setId_plan(c.getInt(4));
                evento.setImagen(c.getString(5));
                evento.setVisible(c.getInt(6));
                eventos.add(evento);
            } while (c.moveToNext());
        }
        c.close();
        conectorBD.cerrar();
        return eventos;
    }

    public void eliminarEvento(Activity actividad, int id_evento){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        conectorBD.eliminarEvento(id_evento);
        conectorBD.cerrar();
    }

     public void cambiarVisibilidad(Activity actividad,int valor, int id_evento){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        conectorBD.modificarVisibilidad(valor, id_evento);
        conectorBD.cerrar();
    }

    //Comprobar el numero de eventos visibles
    public int comprobarEventosVisible(Activity actividad){
        contador = 0;
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        Cursor c = conectorBD.contarEventoVisible();
        if (c.moveToFirst()){
            contador = c.getInt(0);
        }
        conectorBD.cerrar();
        return contador;
    }
}

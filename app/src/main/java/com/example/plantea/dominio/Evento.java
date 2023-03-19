package com.example.plantea.dominio;

import android.app.Activity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Evento {

    public static ArrayList<Evento> listaEventos = new ArrayList<>();
    private int id;
    private String nombre;
    private LocalDate fecha;
    private String hora;
    private String imagen;
    private int id_plan;
    private int visible;
    private int contador, identificador;
    GestionEventos gestorEventos = new GestionEventos();

    public Evento(){

    }

    public Evento(int id, String nombre, LocalDate fecha, String hora, int id_plan, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.id_plan = id_plan;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getId_plan() {
        return id_plan;
    }

    public void setId_plan(int id_plan) {
        this.id_plan = id_plan;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public ArrayList obtenerEventos(Activity actividad, LocalDate fechaSeleccionada){
        listaEventos = gestorEventos.listarEventos(actividad);
        //obtener eventos para una fecha determinada
        ArrayList<Evento> eventos = new ArrayList<>();
        for(Evento evento : listaEventos){
            if(evento.getFecha().equals(fechaSeleccionada)){
                eventos.add(evento);
            }
        }
        return eventos;
    }

    public int crearEvento(Activity actividad, Evento evento){
        identificador = gestorEventos.crearEvento(actividad, evento);
        return identificador;
    }

    public void eliminarEvento(Activity actividad, int id_evento){
        gestorEventos.eliminarEvento(actividad,id_evento);
    }

    public void cambiarVisibilidad(Activity actividad, int valor, int id_evento ) {
        gestorEventos.cambiarVisibilidad(actividad, valor, id_evento);
    }

    //Comprobar el numero de eventos visibles
    public int comprobarEventosVisible(Activity actividad) {
        contador = 0;
        contador = gestorEventos.comprobarEventosVisible(actividad);
        return contador;
    }
}

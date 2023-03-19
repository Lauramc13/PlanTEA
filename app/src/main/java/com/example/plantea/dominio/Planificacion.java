package com.example.plantea.dominio;

import android.app.Activity;

import java.io.Serializable;
import java.util.ArrayList;

public class Planificacion implements Serializable {

    private String titulo;
    private int id;
    private boolean resultado;
    ArrayList<Planificacion> listaPlanes;
    ArrayList<Pictograma> listaPictogramas;
    GestionPlanificaciones gestionPlan = new GestionPlanificaciones();

    public Planificacion(){
    }
    public Planificacion(String titulo, int id) {
        this.titulo = titulo;
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean crearPlanificacion(Activity actividad, ArrayList<Pictograma> pictogramas, String titulo) {
        resultado = gestionPlan.insertarPictogramaPlan(actividad,pictogramas, titulo);
        return resultado;
    }

    public ArrayList mostrarPlanificacionesDisponibles(Activity actividad) {
        listaPlanes = new ArrayList<>();
        listaPlanes = gestionPlan.listarPlanificaciones(actividad);
        return listaPlanes;
    }

    public void eliminarPlanificacion(Activity actividad, int id_plan) {
        gestionPlan.eliminarPlanificacion(actividad, id_plan);
    }

    public ArrayList obtenerPictogramasPlanificacion(Activity actividad, int id_plan) {
        listaPictogramas = new ArrayList<>();
        listaPictogramas = gestionPlan.obtenerPictogramasPlanificacion(actividad, id_plan);
        return listaPictogramas;
    }
    public void actualizarPlanificacion(Activity actividad, int id_plan, String nombre, ArrayList<Pictograma> pictogramas) {
        gestionPlan.actualizarPlanificacion(actividad, id_plan, nombre, pictogramas);
    }

    //Mostrar la planificacion a seguir
    public ArrayList mostrarPlanificacion(Activity actividad) {
        listaPlanes = new ArrayList<>();
        listaPlanes = gestionPlan.obtenerPictogramas(actividad);
        return listaPlanes;
    }

    //Obtener el titulo de la planificacion a seguir
    public String obtenerTituloPlan(Activity actividad) {
        titulo = gestionPlan.obtenerTituloPlan(actividad);
        return titulo;
    }
}

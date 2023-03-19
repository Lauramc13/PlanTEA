package com.example.plantea.dominio;

import android.app.Activity;
import android.database.Cursor;

import com.example.plantea.R;
import com.example.plantea.persistencia.ConectorBD;

import java.io.Serializable;
import java.util.ArrayList;

public class GestionPlanificaciones implements Serializable {

    private ConectorBD conectorBD;
    private String titulo;
    private Boolean resultado;
    ArrayList<Planificacion> listaPlanes;
    ArrayList<Pictograma> listaPictogramas;

    public boolean insertarPictogramaPlan(Activity actividad, ArrayList<Pictograma> pictogramas, String titulo){
        int id_plan;
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        id_plan = conectorBD.insertarPlanificacion(titulo);
        for (int i=0; i<pictogramas.size(); i++){
            resultado = conectorBD.insertarPictogramaPlan(pictogramas.get(i).getTitulo(), pictogramas.get(i).getImagen(),pictogramas.get(i).getCategoria(), id_plan);
        }
        conectorBD.cerrar();
        return resultado;
    }

    public ArrayList listarPlanificaciones(Activity actividad){
        listaPlanes = new ArrayList<>();
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        Cursor c = conectorBD.listarPlanificaciones();
        if (c.moveToFirst())
        {
            do {
                Planificacion plan = new Planificacion();
                plan.setTitulo(c.getString(0));
                plan.setId(c.getInt(1));
                listaPlanes.add(plan);
            } while (c.moveToNext());
        }
        c.close();
        conectorBD.cerrar();
       return listaPlanes;
    }

    public void eliminarPlanificacion(Activity actividad, int id_plan){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        conectorBD.borrarPlanificacion(id_plan);
        conectorBD.cerrar();
    }

    public ArrayList obtenerPictogramasPlanificacion(Activity actividad, int id_plan){
        conectorBD = new ConectorBD(actividad);
        listaPictogramas = new ArrayList<>();
        conectorBD.abrir();
        Cursor c = conectorBD.listarPictogramasPlanificacion(id_plan);
        if (c.moveToFirst())
        {
            do {
                Pictograma pictograma = new Pictograma();
                pictograma.setTitulo(c.getString(0));
                pictograma.setImagen(c.getString(1));
                pictograma.setCategoria(c.getInt(2));
                listaPictogramas.add(pictograma);
            } while (c.moveToNext());
        }
        c.close();
        conectorBD.cerrar();
        return listaPictogramas;
    }

    public void actualizarPlanificacion(Activity actividad, int id_plan, String nombre, ArrayList<Pictograma> pictogramas){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        conectorBD.actualizarPlanificacion(id_plan, nombre);
        for (int i=0; i<pictogramas.size(); i++){
            conectorBD.insertarPictogramaPlan(pictogramas.get(i).getTitulo(), pictogramas.get(i).getImagen(),pictogramas.get(i).getCategoria(), id_plan);
        }
        conectorBD.cerrar();
    }

    public ArrayList obtenerPictogramas(Activity actividad){
        conectorBD = new ConectorBD(actividad);
        listaPictogramas = new ArrayList<>();
        conectorBD.abrir();
        Cursor c = conectorBD.obtenerPlanficacion();
        if (c.moveToFirst())
        {
            do {
                Pictograma pictograma = new Pictograma();
                pictograma.setTitulo(c.getString(0));
                pictograma.setImagen(c.getString(1));
                pictograma.setCategoria(c.getInt(2));
                listaPictogramas.add(pictograma);
            } while (c.moveToNext());
        }
        c.close();
        conectorBD.cerrar();
        return listaPictogramas;
    }

    //Obtener el titulo de la planificacion a seguir
    public String obtenerTituloPlan(Activity actividad){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        Cursor c = conectorBD.listarTituloPlan();
        if (c.moveToFirst()){
            titulo = c.getString(0);
        }
        conectorBD.cerrar();
        return titulo;
    }
}

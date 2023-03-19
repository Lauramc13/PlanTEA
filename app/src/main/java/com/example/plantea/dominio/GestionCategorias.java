package com.example.plantea.dominio;

import android.app.Activity;
import android.database.Cursor;

import com.example.plantea.persistencia.ConectorBD;

import java.util.ArrayList;

public class GestionCategorias {

    ArrayList<String> listaCategorias;
    private ConectorBD conectorBD;
    private int categoria;

    public ArrayList listarCategorias(Activity actividad) {
        conectorBD = new ConectorBD(actividad);
        listaCategorias= new ArrayList<>();
        conectorBD.abrir();
        Cursor c = conectorBD.listarCategorias();
        if (c.moveToFirst())
        {
            do {
                listaCategorias.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        conectorBD.cerrar();
        return listaCategorias;
    }

    public int obtenerIdCategoria(Activity actividad, String nombre) {
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        Cursor c = conectorBD.obtenerIdCategoria(nombre);
        if (c.moveToFirst()){
            categoria = c.getInt(0);
        }
        c.close();
        conectorBD.cerrar();
        return categoria;
    }

    public void insertarSubcategoria (Activity actividad, String nombre) {
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        conectorBD.insertarSubcategoria(nombre);
        conectorBD.cerrar();
    }
}

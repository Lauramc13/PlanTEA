package com.example.plantea.dominio;

import android.app.Activity;

import java.util.ArrayList;

public class Categoria {

    private int categoria;
    ArrayList<String> listaCategorias;
    GestionCategorias gestorCategorias = new GestionCategorias();

    public Categoria(){

    }

    public Categoria(int categoria) {
        this.categoria = categoria;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public ArrayList consultarCategorias(Activity actividad) {
        listaCategorias = new ArrayList<>();
        listaCategorias = gestorCategorias.listarCategorias(actividad);
        return listaCategorias;
    }

    public int obtenerCategoria (Activity actividad, String nombre){
        categoria = gestorCategorias.obtenerIdCategoria(actividad, nombre);
        return categoria;
    }

    public void crearCategoria (Activity actividad, String nombre){
        gestorCategorias.insertarSubcategoria(actividad, nombre);
    }

}

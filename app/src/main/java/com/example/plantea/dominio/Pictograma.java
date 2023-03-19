package com.example.plantea.dominio;

import android.app.Activity;

import java.io.Serializable;
import java.util.ArrayList;

public class Pictograma implements Serializable {

    private String titulo;
    private String imagen;
    private int categoria;
    private int cuaderno;
    ArrayList<Pictograma> listaPictogramas;
    ArrayList<String> listaConsultas;
    GestionPictogramas gestorPictogramas = new GestionPictogramas();

    public Pictograma(){

    }

    public Pictograma(String titulo, String imagen, int categoria, int cuaderno) {
        this.titulo = titulo;
        this.imagen = imagen;
        this.categoria = categoria;
        this.cuaderno= cuaderno;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getCategoria(){
        return categoria;
    }

    public void setCategoria(int categoria){
        this.categoria = categoria;
    }

    public int getCuaderno(){
        return cuaderno;
    }

    public void setCuaderno(int cuaderno){
        this.cuaderno = cuaderno;
    }

    public ArrayList obtenerPictogramas(Activity actividad, int idcategoria ) {
        listaPictogramas = new ArrayList<>();
        listaPictogramas = gestorPictogramas.listarPictogramas(actividad, idcategoria);
        return listaPictogramas;
    }

    public void nuevoPictograma (Activity actividad, String nombre, String imagen, String categoria){
        gestorPictogramas.insertarPictograma(actividad, nombre, imagen, categoria);
    }

    public ArrayList obtenerPictogramasCuaderno(Activity actividad, int identificador ) {
        listaPictogramas = new ArrayList<>();
        listaPictogramas = gestorPictogramas.listarPictogramasCuaderno(actividad, identificador);
        return listaPictogramas;
    }

    public ArrayList obtenerConsultas(Activity actividad, int idcategoria ) {
        listaConsultas = new ArrayList<>();
        listaConsultas = gestorPictogramas.listarConsultas(actividad, idcategoria);
        return listaConsultas;
    }

    public String obtenerImagenEvento(Activity actividad, String consulta, int idCategoria){
        String ruta;
        ruta = gestorPictogramas.obtenerImagenPictograma(actividad, consulta, idCategoria);
        return ruta;
    }
}

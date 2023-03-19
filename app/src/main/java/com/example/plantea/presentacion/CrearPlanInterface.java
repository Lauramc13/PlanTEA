package com.example.plantea.presentacion;

public interface CrearPlanInterface {

    //CategoriasFragment
     void mostrarCategoria(int idCategoria);
     void mostrarsubCategoria(String tituloCategoria);


    //CategoriasPictogramasFragment
    void cerrarFragment();
    void nuevoPictogramaDialogo();
    void pictogramaSeleccionado(String titulo, String imagen, int categoria);


}

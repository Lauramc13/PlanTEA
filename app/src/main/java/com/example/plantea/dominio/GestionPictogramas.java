package com.example.plantea.dominio;

import android.app.Activity;
import android.database.Cursor;

import com.example.plantea.persistencia.ConectorBD;

import java.io.Serializable;
import java.util.ArrayList;

public class GestionPictogramas  implements Serializable {

    ArrayList<Pictograma> listaPictogramas;
    ArrayList<String> listaConsultas;
    private ConectorBD conectorBD;

    public ArrayList listarPictogramas(Activity actividad, int idcategoria) {
        conectorBD = new ConectorBD(actividad);
        listaPictogramas = new ArrayList<>();
        conectorBD.abrir();
        Cursor c = conectorBD.listarPictogramas(idcategoria);
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

    public void insertarPictograma (Activity actividad,String nombre, String imagen, String categoria) {
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        conectorBD.insertarPictograma(nombre, imagen, categoria);
        conectorBD.cerrar();
    }

    public ArrayList listarPictogramasCuaderno(Activity actividad, int identificador) {
        conectorBD = new ConectorBD(actividad);
        listaPictogramas = new ArrayList<>();
        conectorBD.abrir();
        Cursor c = conectorBD.listarPictogramasCuaderno(identificador);
        if (c.moveToFirst())
        {
            do {
                Pictograma pictograma = new Pictograma();
                pictograma.setTitulo(c.getString(0));
                pictograma.setImagen(c.getString(1));
                pictograma.setCuaderno(c.getInt(2));
                listaPictogramas.add(pictograma);
            } while (c.moveToNext());
        }
        c.close();
        conectorBD.cerrar();

        for(int i=0; i<listaPictogramas.size(); i++){
            System.out.print(listaPictogramas.get(i).getTitulo() + listaPictogramas.get(i).getCuaderno() );
        }
        return listaPictogramas;
    }

    public ArrayList listarConsultas(Activity actividad, int idcategoria) {
        conectorBD = new ConectorBD(actividad);
        listaConsultas = new ArrayList<>();
        conectorBD.abrir();
        Cursor c = conectorBD.listarConsulta(idcategoria);
        if (c.moveToFirst())
        {
            do {
                listaConsultas.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        conectorBD.cerrar();
        return listaConsultas;
    }

    public String obtenerImagenPictograma(Activity actividad, String consulta, int idCategoria){
        conectorBD = new ConectorBD(actividad);
        String ruta= null;
        conectorBD.abrir();
        Cursor c = conectorBD.obtenerRutaPictograma(consulta, idCategoria);
        if (c.moveToFirst())
        {
            do {
                ruta = (c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        conectorBD.cerrar();
        return ruta;
    }
}

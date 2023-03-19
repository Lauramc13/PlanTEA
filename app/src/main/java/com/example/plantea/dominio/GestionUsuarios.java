package com.example.plantea.dominio;

import android.app.Activity;

import com.example.plantea.persistencia.ConectorBD;

public class GestionUsuarios {
    private ConectorBD conectorBD;
    private boolean resultado;

    public boolean crearPassword(String password, Activity actividad){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        resultado = conectorBD.insertarPass(password);
        conectorBD.cerrar();
        return resultado;
    }

    public boolean comprobarPassword(String password, Activity actividad){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        resultado = conectorBD.consultarPass(password);
        conectorBD.cerrar();
        return resultado;
    }

    public boolean cambiarPassword(String passwordNueva, String passwordVieja, Activity actividad){
        conectorBD = new ConectorBD(actividad);
        conectorBD.abrir();
        resultado = conectorBD.actualizarPass(passwordNueva,passwordVieja);
        conectorBD.cerrar();
        return resultado;
    }
}

package com.example.plantea.dominio;

import android.app.Activity;

public class Usuario_Planificador {

    private String password;
    GestionUsuarios gestorUsuario = new GestionUsuarios();
    private Boolean resultado;

    public Usuario_Planificador(){

    }

    public Usuario_Planificador(String pass){
        this.password = pass;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean crearPass(String password, Activity actividad){
        resultado = gestorUsuario.crearPassword(password, actividad);
        return resultado;
    }

    public boolean comprobarPass(String password, Activity actividad){
        resultado = gestorUsuario.comprobarPassword(password, actividad);
        return resultado;
    }

    public boolean confirmarPass(String passwordVieja, String passwordNueva, String passwordConfirma, Activity actividad){
        if(passwordNueva.equals(passwordConfirma)){
            resultado = gestorUsuario.cambiarPassword(passwordNueva, passwordVieja, actividad);
        }else{
            resultado = false;
        }
        return resultado;
    }
}

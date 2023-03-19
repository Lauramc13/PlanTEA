package com.example.plantea.presentacion;

import com.example.plantea.dominio.Evento;

public interface EventoInterface {

    //EventosFragment
    void crearEventoFragment();

    //NuevoEventoFragment
    void nuevoEvento(Evento evento);
    void planificar();
    void cancelarEvento();
    void cancelarNotificacion(int identificador);

}

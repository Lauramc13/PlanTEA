package com.example.plantea.presentacion

import com.example.plantea.dominio.Pictograma

interface CuadernoInterface {
    fun mostrarPictogramas(identificador: Int, termometro: Boolean?, tituloCuaderno: String)
    fun mostrarPictogramasBusqueda(query: String)
    fun cerrarFragment()
    fun atrasFragment(listaPictograma:  ArrayList<Pictograma>?)
    fun addPictoFromBusqueda(pictograma: Pictograma)
}

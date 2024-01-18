package com.example.plantea.presentacion

import com.example.plantea.dominio.Pictograma

interface CuadernoInterface {

    fun mostrarPictogramas(identificador: Int, termometro: Boolean?, tituloCuaderno: String)
    fun mostrarPictogramasBusqueda(query: String)
    fun cerrarFragment()
    fun atrasFragment(termometro: Boolean?, tituloCuaderno: String)
    fun addPictoFromBusqueda(pictograma: Pictograma)
    fun removePicto(pictograma: Pictograma, sourceAPI: Boolean, isBusqueda: Boolean)
    fun addPictoPersonalizado(newPictograma: Pictograma)


}

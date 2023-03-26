package com.example.plantea.presentacion

import android.content.Context
import android.widget.SpinnerAdapter

interface CrearPlanInterface {
    //CategoriasFragment
    fun mostrarCategoria(idCategoria: Int)
    fun mostrarsubCategoria(tituloCategoria: String?)

    //CategoriasPictogramasFragment
    fun cerrarFragment()
    fun nuevoPictogramaDialogo()
    fun pictogramaSeleccionado(titulo: String?, imagen: String?, categoria: Int)
}
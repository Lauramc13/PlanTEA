package com.example.plantea.presentacion

interface CrearPlanInterface {
    //CategoriasFragment
    fun mostrarCategoria(idCategoria: Int)
    fun mostrarsubCategoria(tituloCategoria: String?)

    //CategoriasPictogramasFragment
    fun cerrarFragment()
    fun nuevoPictogramaDialogo()
    fun pictogramaSeleccionado(titulo: String?, imagen: String?, categoria: Int)
}
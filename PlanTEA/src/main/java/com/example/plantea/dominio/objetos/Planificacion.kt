package com.example.plantea.dominio.objetos

data class Planificacion (
    var id: String? = null,
    var titulo: String? = null,
    var fecha: String? = null,
    var listaPictogramas: ArrayList<Pictograma> = ArrayList()
)

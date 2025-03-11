package com.example.plantea.dominio.objetos

import android.graphics.Bitmap

data class Categoria(
    var id : Int = 0,
    var nombre: String? = null,
    var imagen: Bitmap? = null,
    var color: String? = null
)

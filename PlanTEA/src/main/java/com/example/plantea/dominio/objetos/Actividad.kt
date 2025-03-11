package com.example.plantea.dominio.objetos

import android.graphics.Bitmap

data class Actividad(
    var id: String? = null,
    var nombre: String? = null,
    var imagen: Bitmap? = null,
    var idCategoria : ArrayList<String>? = null,
    var idUsuario: String? = null
)
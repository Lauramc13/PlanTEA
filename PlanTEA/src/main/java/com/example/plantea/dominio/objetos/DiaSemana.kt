package com.example.plantea.dominio.objetos

import android.graphics.Bitmap

data class DiaSemana(
    var dia: String? = null,
    var imagen: Bitmap? = null,
    var color: String? = null,
    var idEvento: String? = null
)
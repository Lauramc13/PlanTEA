package com.example.plantea.dominio.objetos

import android.graphics.Bitmap
import java.time.LocalDate

data class DiaMes (
    var fecha: LocalDate? = null,
    var nombre: String? = null,
    var color: String? = null,
    var imagen: Bitmap? = null
)
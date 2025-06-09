package com.example.plantea.dominio.objetos

import android.graphics.Bitmap

data class Usuario (
     var id : String? = null,
     var name: String? = null,
     var email: String? = null,
     var imagen: Bitmap? = null,
     var actividades: ArrayList<Actividad>? = ArrayList(),
     var configPictograma : String? = null
)

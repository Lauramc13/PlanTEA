package com.example.plantea.dominio.objetos

import java.time.LocalDate
import java.time.LocalDateTime

data class Evento(
    var id :Int? = null,
    var idUsuario: String? = null,
    var nombre: String? = null,
    var fecha: LocalDate? = null,
    var horaInicio: String? = null,
    var horaFin: String? = null,
    var localizacion: String? = null,
    var notas : String? = null,
    var idPlan : Int? = 0,
    var cambiarVisibilidad : Boolean? = false,
    var visible : Int? = 0
)
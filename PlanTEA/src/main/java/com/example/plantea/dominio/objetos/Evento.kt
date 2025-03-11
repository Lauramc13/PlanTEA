package com.example.plantea.dominio.objetos

import java.time.LocalDate
import java.time.LocalDateTime

data class Evento(
    var id :Int? = null,
    var idUsuario: String? = null,
    var nombre: String? = null,
    var fecha: LocalDate? = null,
    var hora: String? = null,
    var idPlan : Int? = 0,
    var cambiarVisibilidad : Boolean? = false,
    var recordatorio : LocalDateTime? = null,
    var visible : Int? = 0
)
package com.example.plantea.dominio

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

object CalendarioUtilidades {
    lateinit var fechaSeleccionada: LocalDate
    @JvmStatic
    fun formatoHoraAviso(fecha: String?): LocalTime {
        val formato = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(fecha, formato)
    }

  /*  @JvmStatic
    fun formatoMesEvento(fecha: LocalDate): String {
        val formato = DateTimeFormatter.ofPattern("MMMM", Locale("es", "ES"))
        return fecha.format(formato)
    }*/

    @JvmStatic
    fun formatoFechaEvento(fecha: LocalDate): String {
        val formato = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("es", "ES"))
        return fecha.format(formato)
    }

    @JvmStatic
    fun formatoDiaEvento(fecha: LocalDate): String {
        val formato = DateTimeFormatter.ofPattern("EEEE  d", Locale("es", "ES"))
        return fecha.format(formato)
    }

    @JvmStatic
    fun formatoDiaMes(fecha: LocalDate): String {
        val formato = DateTimeFormatter.ofPattern("dd/MM", Locale("es", "ES"))
        return fecha.format(formato)
    }

    @JvmStatic
    fun formatoMesAnio(fecha: LocalDate): String {
        val formato = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
        return fecha.format(formato)
    }

    //En este método se calcula los días para el mes seleccionado
    @JvmStatic
    fun obtenerDiasMes(fecha: LocalDate?): ArrayList<LocalDate?> {
        val diasMes = ArrayList<LocalDate?>()

        //Se obtiene el número de días de un mes
        val mesAnio = YearMonth.from(fecha)
        val maxDias = mesAnio.lengthOfMonth()
        val primeroMes = fechaSeleccionada.withDayOfMonth(1)
        val diaSemana = primeroMes.dayOfWeek.value
        var dia = 1
        for (i in 1..42) {
            if (i < diaSemana || i >= maxDias + diaSemana) {
                diasMes.add(null)
            } else {
                diasMes.add(LocalDate.of(fechaSeleccionada.year, fechaSeleccionada.month, dia++))
            }
        }

        // Elimina la última semana si es nula
        if (diasMes.subList(diasMes.size - 7, diasMes.size).all { it == null }) {
            diasMes.subList(diasMes.size - 7, diasMes.size).clear()
        }

        return diasMes
    }
}
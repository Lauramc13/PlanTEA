package com.example.plantea.dominio

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

/***
 * Clase que contiene métodos para el manejo y formateo de fechas
 */

object CalendarioUtilidades {
    var fechaSeleccionada: LocalDate = LocalDate.now()

    // "10:00" -> LocalTime
    fun formatoHoraAviso(fecha: String?): LocalTime {
        val formato = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(fecha, formato)
    }

    // "2025-02-14" -> "14/02/2025"
    fun formatoFecha(fecha: LocalDate): String {
        return if (Locale.getDefault().language == "es") {
            val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "ES"))
            fecha.format(formato)
        } else {
            val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("en", "US"))
            fecha.format(formato)
        }
    }

    // "2025-02-14" -> "14 de febrero de 2025" o "February 14, 2025"
    fun formatoFechaEvento(fecha: LocalDate): String {
        return if (Locale.getDefault().language == "es") {
            val formato = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
            fecha.format(formato)
        } else {
            val formato = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale("en", "US"))
            fecha.format(formato)
        }
    }

    // "2025-02-14" -> "Viernes 14"
    fun formatoDiaEvento(fecha: LocalDate): String {
        return if (Locale.getDefault().language == "es") {
            val formato = DateTimeFormatter.ofPattern("EEEE  d", Locale("es", "ES"))
            fecha.format(formato)
        } else {
            val formato = DateTimeFormatter.ofPattern("EEEE  d", Locale("en", "US"))
            fecha.format(formato)
        }
    }

    /***
     * Método que obtiene los días de un mes en un ArrayList
     */
    fun obtenerDiasMes(fecha: LocalDate?): ArrayList<LocalDate?> {
        val diasMes = ArrayList<LocalDate?>()
        if (fecha == null) return diasMes
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
        if (diasMes.subList(diasMes.size - 7, diasMes.size).all { it == null }) {
            diasMes.subList(diasMes.size - 7, diasMes.size).clear()
        }
        return diasMes
    }

    // "2025-02-14" -> "febrero 2025"
    fun formatoMesAnio(fecha: LocalDate): String {
        return if (Locale.getDefault().language == "es") {
            val formato = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
            fecha.format(formato)
        } else {
            val formato = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("en", "US"))
            fecha.format(formato)
        }
    }

}
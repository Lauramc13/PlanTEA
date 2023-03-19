package com.example.plantea.dominio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarioUtilidades {

    public static LocalDate fechaSeleccionada;

    public static LocalTime formatoHoraAviso(String fecha){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(fecha, formato);
        return time;
    }

    public static String formatoMesEvento(LocalDate fecha){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("MMMM",  new Locale("es", "ES"));
        return fecha.format(formato);
    }

    public static String formatoFechaEvento(LocalDate fecha){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd MMMM YYYY",  new Locale("es", "ES"));
        return fecha.format(formato);
    }

    public static String formatoDiaEvento(LocalDate fecha){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("EEEE  d",  new Locale("es", "ES"));
        return fecha.format(formato);
    }

    public static String formatoMesAnio(LocalDate fecha){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("MMMM YYYY", new Locale("es", "ES"));
        return fecha.format(formato);
    }

    //En este método se calcula los días para el mes seleccionado
    public static ArrayList<LocalDate> obtenerDiasMes(LocalDate fecha){

        ArrayList<LocalDate> diasMes = new ArrayList<>();

        //Se obtiene el número de días de un mes
        YearMonth mesAnio = YearMonth.from(fecha);
        int maxDias = mesAnio.lengthOfMonth();

        LocalDate primeroMes = fechaSeleccionada.withDayOfMonth(1);
        int diaSemana = primeroMes.getDayOfWeek().getValue();

        int dia = 1;
        for(int i = 1; i <= 42; i++){
            if(i < diaSemana || i >= maxDias + diaSemana){
                diasMes.add(null);
            }
            else{
                diasMes.add(LocalDate.of(fechaSeleccionada.getYear(),fechaSeleccionada.getMonth(),dia++));
            }
        }
        return diasMes;
    }
}

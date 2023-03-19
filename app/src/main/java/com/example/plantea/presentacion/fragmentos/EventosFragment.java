package com.example.plantea.presentacion.fragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantea.R;
import com.example.plantea.dominio.CalendarioUtilidades;
import com.example.plantea.dominio.Evento;
import com.example.plantea.dominio.Pictograma;
import com.example.plantea.dominio.Planificacion;
import com.example.plantea.presentacion.actividades.ninio.PlanActivity;
import com.example.plantea.presentacion.adaptadores.AdaptadorEvento;
import com.example.plantea.presentacion.EventoInterface;

import java.util.ArrayList;

public class EventosFragment extends Fragment implements AdaptadorEvento.OnItemSelectedListener {

    View vista;
    TextView diaEvento, mensaje;
    Button crearEvento;
    RecyclerView listaEventos;
    Activity actividad;
    EventoInterface eventoInterface;
    ArrayList<Evento> eventos;
    ArrayList<Pictograma> pictogramas;
    AdaptadorEvento adaptadorEvento;
    int contador=0;

    Evento evento = new Evento();
    Planificacion plan = new Planificacion();

    public EventosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_eventos, container, false);

        diaEvento = vista.findViewById(R.id.txt_dia_eventos);
        crearEvento = vista.findViewById(R.id.btn_nuevo_evento);
        mensaje = vista.findViewById(R.id.lbl_mensaje_evento);
        listaEventos = vista.findViewById(R.id.recycler_eventos);

        iniciarAdaptadorEvento();

         crearEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventoInterface.crearEventoFragment();
            }
        });
        return vista;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            actividad = (Activity) context;
            eventoInterface = (EventoInterface) actividad;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void iniciarAdaptadorEvento(){
        diaEvento.setText(CalendarioUtilidades.formatoDiaEvento(CalendarioUtilidades.fechaSeleccionada).toUpperCase());
        eventos = evento.obtenerEventos(actividad,CalendarioUtilidades.fechaSeleccionada);
        listaEventos.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorEvento = new AdaptadorEvento(eventos, this);
        if(eventos.isEmpty()){
            listaEventos.setVisibility(View.GONE);
            mensaje.setVisibility(View.VISIBLE);
        } else {
            listaEventos.setVisibility(View.VISIBLE);
            mensaje.setVisibility(View.GONE);
        }
        listaEventos.setAdapter(adaptadorEvento);
    }

    @Override
    public void deleteClick(int posicion) {
        AlertDialog.Builder dialogoEliminar = new AlertDialog.Builder(getContext());
        dialogoEliminar.setTitle("Eliminar Evento");
        dialogoEliminar.setMessage("¿Seguro que deseas eliminar el evento seleccionado?");
        dialogoEliminar.setCancelable(false);
        dialogoEliminar.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoEliminar, int id) {
                Toast.makeText(getContext(),"Evento eliminado", Toast.LENGTH_SHORT).show();
                eventoInterface.cancelarNotificacion(eventos.get(posicion).getId());
                evento.eliminarEvento(actividad, eventos.get(posicion).getId());
                eventos.remove(posicion);
                adaptadorEvento.notifyDataSetChanged();
                //Mostramos un mensaje informando si la lista está vacía
                if(eventos.isEmpty()){
                    listaEventos.setVisibility(View.GONE);
                    mensaje.setVisibility(View.VISIBLE);
                } else {
                    listaEventos.setVisibility(View.VISIBLE);
                    mensaje.setVisibility(View.GONE);
                }
            }
        });
        dialogoEliminar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoEliminar, int id) {
                dialogoEliminar.dismiss();
            }
        });
        dialogoEliminar.show();
    }

    @Override
    public void viewClick(int posicion) {
        contador = evento.comprobarEventosVisible(actividad);
        System.out.println(contador);
        if (eventos.get(posicion).getVisible() == 1){
            evento.cambiarVisibilidad(actividad,0, eventos.get(posicion).getId());
        }else{
            if (contador == 0){
                evento.cambiarVisibilidad(actividad,1, eventos.get(posicion).getId());
            }else {
                Toast.makeText(getContext(),"Solo un evento puede ser visible", Toast.LENGTH_SHORT).show();
            }
        }
        iniciarAdaptadorEvento();
    }

    @Override
    public void viewEventClick(int posicion) {
        pictogramas = new ArrayList<>();
        pictogramas=plan.obtenerPictogramasPlanificacion(actividad, eventos.get(posicion).getId_plan());
        Intent intent = new Intent(actividad, PlanActivity.class);
        intent.putExtra("titulo", eventos.get(posicion).getNombre());
        intent.putExtra("pictogramas", pictogramas);
        startActivity(intent);
    }
}
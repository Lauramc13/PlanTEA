package com.example.plantea.presentacion.fragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.plantea.R;
import com.example.plantea.dominio.CalendarioUtilidades;
import com.example.plantea.dominio.Evento;
import com.example.plantea.dominio.Pictograma;
import com.example.plantea.dominio.Planificacion;
import com.example.plantea.presentacion.EventoInterface;
import com.example.plantea.presentacion.actividades.planificador.CrearPlanActivity;
import com.example.plantea.presentacion.adaptadores.AdaptadorListaPlanes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NuevoEventoFragment extends Fragment implements AdaptadorListaPlanes.OnItemSelectedListener{

    Activity actividad;
    EventoInterface eventoInterface;
    View vista;
    Button btn_hora, btn_guardar, btn_planificar, btn_desplegarPlanes;
    TextView horaEvento, fechaEvento, mensajePlanes;
    ImageView cancelarEvento;
    RecyclerView listaPlanificaciones;
    Spinner spinner_consultas;
    AdaptadorListaPlanes adaptador;
    ArrayList<Planificacion> planes;
    ConstraintLayout layout_planificaciones;
    private ArrayList<Pictograma> pictogramas;
    private ArrayList<String> consultas;
    public int hora, minuto, planSeleccionado, posAnterior;
    public String nombreEvento;

    Planificacion plan = new Planificacion();
    Pictograma pictograma = new Pictograma();

    public NuevoEventoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_nuevo_evento, container, false);

        cancelarEvento = vista.findViewById(R.id.img_cancelarEvento);
        btn_hora = vista.findViewById(R.id.btn_horaEvento);
        btn_guardar = vista.findViewById(R.id.btn_guardarEvento);
        btn_planificar = vista.findViewById(R.id.btn_planificar);
        btn_desplegarPlanes = vista.findViewById(R.id.button);
        horaEvento = vista.findViewById(R.id.lbl_horaEvento);
        fechaEvento = vista.findViewById(R.id.lbl_fechaEvento);
        mensajePlanes = vista.findViewById(R.id.lbl_mensajePlanes);
        listaPlanificaciones = vista.findViewById(R.id.recycler_planificaciones);
        spinner_consultas = vista.findViewById(R.id.spinner_consultas);
        layout_planificaciones = vista.findViewById(R.id.layout);

        //Componentes deshabilitados al principio
        spinner_consultas.setEnabled(false);
        btn_desplegarPlanes.setEnabled(false);
        layout_planificaciones.setVisibility(View.GONE);


        fechaEvento.setText(CalendarioUtilidades.formatoFechaEvento(CalendarioUtilidades.fechaSeleccionada));

        consultas = pictograma.obtenerConsultas(actividad,1);
        spinner_consultas.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, consultas));

        btn_desplegarPlanes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_planificaciones.setVisibility(View.VISIBLE);
                iniciarListaPlanificaciones();
            }
        });

        btn_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarReloj(horaEvento);
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rutaImagen = obtenerImagenEvento();
                Evento evento = new Evento(0,nombreEvento, CalendarioUtilidades.fechaSeleccionada, horaEvento.getText().toString(), planSeleccionado,rutaImagen);
                eventoInterface.nuevoEvento(evento);
                Toast.makeText(getContext(),"Evento creado", Toast.LENGTH_SHORT).show();
            }
        });

        btn_planificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventoInterface.planificar();
            }
        });

        cancelarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventoInterface.cancelarEvento();
            }
        });

        return vista;
    }

    public String obtenerImagenEvento(){
       String ruta = pictograma.obtenerImagenEvento(actividad,spinner_consultas.getSelectedItem().toString(),1);
       return ruta;
    }

    @Override
    public void onResume() {
        super.onResume();
        iniciarListaPlanificaciones();
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

    private void iniciarListaPlanificaciones(){
        listaPlanificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        planes = plan.mostrarPlanificacionesDisponibles(actividad);
        adaptador = new AdaptadorListaPlanes(planes,this);
        listaPlanificaciones.setAdapter(adaptador);
        //Mostramos un mensaje informando si la lista está vacía
        if(planes.isEmpty()){
            listaPlanificaciones.setVisibility(View.GONE);
            mensajePlanes.setVisibility(View.VISIBLE);
        } else {
            listaPlanificaciones.setVisibility(View.VISIBLE);
            mensajePlanes.setVisibility(View.GONE);
        }
    }

    private void mostrarReloj(TextView tiempo){
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int horaSeleccionada, int minutoSeleccionado) {
                hora = horaSeleccionada;
                minuto = minutoSeleccionado;
                tiempo.setText(String.format(Locale.getDefault(), "%02d:%02d", hora, minuto));
                //Habilitamos el resto de componentes
                spinner_consultas.setEnabled(true);
                btn_desplegarPlanes.setEnabled(true);
            }
        };
        int style = android.R.style.Theme_Holo_Light_Dialog;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),style, onTimeSetListener, currentHour, currentMinute, true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.setTitle("Selecciona una hora");
        timePickerDialog.show();
    }

    @Override
    public void deleteClick(int posicion) {
        AlertDialog.Builder dialogoEliminar = new AlertDialog.Builder(getContext());
        dialogoEliminar.setTitle("Eliminar Planificación");
        dialogoEliminar.setMessage("¿Seguro que deseas eliminar la planificación seleccionada?");
        dialogoEliminar.setCancelable(false);
        dialogoEliminar.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoEliminar, int id) {
                Toast.makeText(getContext(),"Planificación eliminada", Toast.LENGTH_SHORT).show();
                plan.eliminarPlanificacion(actividad, planes.get(posicion).getId());
                planes.remove(posicion);
                adaptador.notifyDataSetChanged();
                //Mostramos un mensaje informando si la lista está vacía
                if(planes.isEmpty()){
                    listaPlanificaciones.setVisibility(View.GONE);
                    mensajePlanes.setVisibility(View.VISIBLE);
                } else {
                    listaPlanificaciones.setVisibility(View.VISIBLE);
                    mensajePlanes.setVisibility(View.GONE);
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
    public void editClick(int posicion) {
        pictogramas = new ArrayList<>();
        pictogramas=plan.obtenerPictogramasPlanificacion(actividad, planes.get(posicion).getId());
        Intent intent = new Intent(actividad, CrearPlanActivity.class);
        intent.putExtra("identificador", planes.get(posicion).getId());
        intent.putExtra("titulo", planes.get(posicion).getTitulo());
        intent.putExtra("pictogramas", pictogramas);
        startActivity(intent);
    }

    @Override
    public void duplicateClick(int posicion) {
        pictogramas = new ArrayList<>();
        pictogramas=plan.obtenerPictogramasPlanificacion(actividad, planes.get(posicion).getId());
        boolean creada = plan.crearPlanificacion(actividad, pictogramas, planes.get(posicion).getTitulo());
        if(creada){
            Toast.makeText(getContext(), "Planificación duplicada" , Toast.LENGTH_LONG).show();
            iniciarListaPlanificaciones();
        }else{
            Toast.makeText(getContext(), "Error al duplicar planificación", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void planSeleccionado(int posicion) {
        AdaptadorListaPlanes.ViewHolder viewHolder = (AdaptadorListaPlanes.ViewHolder) listaPlanificaciones.findViewHolderForAdapterPosition(posAnterior);
        CardView card = (CardView) viewHolder.itemView.findViewById(R.id.card_plan);
        if(posicion != posAnterior){
           card.setCardBackgroundColor(Color.WHITE);
        }else{
            card.setCardBackgroundColor(Color.rgb(224,224,224));
        }
        posAnterior = posicion;
        planSeleccionado=0;
        nombreEvento = null;
        planSeleccionado = planes.get(posicion).getId();
        nombreEvento = planes.get(posicion).getTitulo();
        btn_guardar.setEnabled(true);
    }
}


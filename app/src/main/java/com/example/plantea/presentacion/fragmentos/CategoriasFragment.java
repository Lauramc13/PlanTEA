package com.example.plantea.presentacion.fragmentos;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantea.R;
import com.example.plantea.presentacion.CrearPlanInterface;


public class CategoriasFragment extends Fragment {

    View vista;
    Activity actividad;
    CardView cardConsultas;
    CardView cardProfesionales;
    CardView cardLugares;
    CardView cardDesplazamiento;
    CardView cardAccion;
    CardView cardRecompensa;
    CardView cardEntretenimiento;

    CrearPlanInterface interfaceCategorias;

    public CategoriasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int orientation = getResources().getConfiguration().orientation;
        int layoutId = orientation == Configuration.ORIENTATION_LANDSCAPE ? R.layout.fragment_categorias : R.layout.fragment_categorias_portrait;
        vista = inflater.inflate(layoutId, container, false);

        cardConsultas = vista.findViewById(R.id.categoria_consultas);
        cardProfesionales = vista.findViewById(R.id.categoria_profesionales);
        cardAccion = vista.findViewById(R.id.categoria_accion);
        cardDesplazamiento = vista.findViewById(R.id.categoria_desplazamiento);
        cardEntretenimiento = vista.findViewById(R.id.categoria_entretenimiento);
        cardLugares = vista.findViewById(R.id.categoria_lugares);
        cardRecompensa = vista.findViewById(R.id.categoria_recompensa);

        cardConsultas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.mostrarCategoria(1);
            }
        });

        cardProfesionales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.mostrarCategoria(2);
            }
        });

        cardLugares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.mostrarCategoria(3);
            }
        });

        cardDesplazamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.mostrarCategoria(4);
            }
        });

        cardAccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.mostrarCategoria(5);
            }
        });

        cardEntretenimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.mostrarCategoria(6);
            }
        });

        cardRecompensa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.mostrarCategoria(7);
            }
        });

        return vista;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            actividad = (Activity) context;
            interfaceCategorias = (CrearPlanInterface) actividad;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
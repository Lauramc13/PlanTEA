package com.example.plantea.presentacion.fragmentos.cuaderno;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantea.R;
import com.example.plantea.presentacion.CuadernoInterface;


public class PrincipalFragment extends Fragment {

    View vista;
    Activity actividad;
    CardView cardSintomas, cardDolores;
    CuadernoInterface interfaceCuaderno;

    public PrincipalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_cuaderno_principal, container, false);

        cardSintomas = vista.findViewById(R.id.card_sintomas);
        cardDolores = vista.findViewById(R.id.card_dolores);

        cardSintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCuaderno.mostrarPictogramas(2);
            }
        });

        cardDolores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCuaderno.mostrarPictogramas(3);
            }
        });

        return vista;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            actividad = (Activity) context;
            interfaceCuaderno = (CuadernoInterface) actividad;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
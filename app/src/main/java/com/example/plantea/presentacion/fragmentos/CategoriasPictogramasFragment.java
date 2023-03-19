package com.example.plantea.presentacion.fragmentos;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plantea.R;
import com.example.plantea.dominio.Pictograma;
import com.example.plantea.presentacion.CrearPlanInterface;
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramas;

import java.util.ArrayList;


public class CategoriasPictogramasFragment extends Fragment implements AdaptadorPictogramas.OnItemSelectedListener {

    RecyclerView recyclerPictogramas;
    ArrayList<Pictograma> listaPictogramas;
    CrearPlanInterface interfaceCategorias;
    Activity actividad;
    View vista;

    private ImageView image_Cerrar;
    private ImageView image_add;

    public CategoriasPictogramasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_categorias_pictogramas, container, false);

        Bundle bundle = this.getArguments();
        listaPictogramas = (ArrayList<Pictograma>) bundle.get("key");

        recyclerPictogramas =  vista.findViewById(R.id.recycler_Pictogramas);
        int orientation = getResources().getConfiguration().orientation;
        int gridValueManager;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridValueManager = 3; // set the number of columns to 2 for portrait mode
        } else {
            gridValueManager = 5; // set the number of columns to 3 for landscape mode
        }
        recyclerPictogramas.setLayoutManager(new GridLayoutManager(getContext(),gridValueManager ));
        image_Cerrar = vista.findViewById(R.id.image_Cerrar);
        image_add = vista.findViewById(R.id.image_add);

        AdaptadorPictogramas adaptador = new AdaptadorPictogramas(listaPictogramas,this);
        recyclerPictogramas.setAdapter(adaptador);

        //Este método se ejecutará al pinchar sobre la imagen de cerrar
        image_Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.cerrarFragment();
            }
        });

        //Este método se ejecutará al seleccionar añadir nuevo pictograma.
        image_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCategorias.nuevoPictogramaDialogo();
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

    //Este método se ejecutará al seleccionar un pictograma para añadirlo en la planificacion
    @Override
    public void onItemSeleccionado(int posicion) {
        if (listaPictogramas.get(posicion).getCategoria() == 1){
            interfaceCategorias.mostrarsubCategoria(listaPictogramas.get(posicion).getTitulo());
        } else {
            interfaceCategorias.pictogramaSeleccionado(listaPictogramas.get(posicion).getTitulo(), listaPictogramas.get(posicion).getImagen(),listaPictogramas.get(posicion).getCategoria());
        }
    }
}
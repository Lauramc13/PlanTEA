package com.example.plantea.presentacion.fragmentos.cuaderno;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantea.R;
import com.example.plantea.dominio.Pictograma;
import com.example.plantea.presentacion.CuadernoInterface;
import com.example.plantea.presentacion.adaptadores.AdaptadorPictogramasCuaderno;


import java.util.ArrayList;

public class CuadernoPictogramasFragment extends Fragment implements AdaptadorPictogramasCuaderno.OnItemSelectedListener{

    View vista;
    Activity actividad;
    CuadernoInterface interfaceCuaderno;
    ArrayList<Pictograma> listaPictogramas;
    RecyclerView lst_Pictogramas;
    ImageView image_Cerrar;
    SeekBar seekbar;

    public CuadernoPictogramasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_cuaderno_pictogramas, container, false);

        Bundle bundle = this.getArguments();
        listaPictogramas = (ArrayList<Pictograma>) bundle.get("key");

        lst_Pictogramas =  vista.findViewById(R.id.lst_cuaderno_pictogramas);
        int orientation = getResources().getConfiguration().orientation;
        int gridValueManager;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridValueManager = 3; // set the number of columns to 2 for portrait mode
        } else {
            gridValueManager = 5; // set the number of columns to 3 for landscape mode
        }

        lst_Pictogramas.setLayoutManager(new GridLayoutManager(getContext(),gridValueManager ));

        AdaptadorPictogramasCuaderno adaptador = new AdaptadorPictogramasCuaderno(listaPictogramas,this);
        lst_Pictogramas.setAdapter(adaptador);

        image_Cerrar = vista.findViewById(R.id.icono_cuaderno_fragment);

        image_Cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfaceCuaderno.cerrarFragment();
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


    @Override
    public void pictogramaCuaderno(int posicion) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_termometro);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView pictograma = dialog.findViewById(R.id.img_pictograma);
        TextView tituloPictograma = dialog.findViewById(R.id.lbl_pictograma);
        seekbar = dialog.findViewById(R.id.seekBar_termometro);

        pictograma.setImageURI(Uri.parse(listaPictogramas.get(posicion).getImagen()));
        tituloPictograma.setText(listaPictogramas.get(posicion).getTitulo());

        //Funcionalidad termómetro: cambio de color según el progreso
        if (seekbar != null) {
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Write code to perform some action when progress is changed.
                    if(progress < 30){
                        seekBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(118,255,3)));

                    }else if(progress < 60){
                        seekBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(255, 165, 0)));

                    }else if(progress < 100){
                        seekBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Write code to perform some action when touch is started.

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Write code to perform some action when touch is stopped.
                    // Toast.makeText(getContext(), "Progress is " + seekBar.getProgress() + "%", Toast.LENGTH_SHORT).show();
                }
            });
        }
        dialog.show();
    }
}
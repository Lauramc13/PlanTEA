package com.example.plantea.presentacion.adaptadores;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantea.R;
import com.example.plantea.dominio.CalendarioUtilidades;
import com.example.plantea.dominio.Evento;

import java.time.LocalDate;
import java.util.ArrayList;

public class AdaptadorCalendario extends RecyclerView.Adapter<AdaptadorCalendario.ViewHolderCalendario>{

    private final ArrayList<LocalDate> diasMes;
    private AdaptadorCalendario.OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void diaSeleccionado(LocalDate fecha);
    }

    public AdaptadorCalendario(ArrayList<LocalDate> diasMes, AdaptadorCalendario.OnItemSelectedListener listener)
    {
        this.diasMes = diasMes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderCalendario onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendario_celda, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new ViewHolderCalendario(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCalendario holder, int position) {
        LocalDate fecha = diasMes.get(position);
        if(fecha == null){
            holder.diaMes.setText("");
            holder.vistaPrincipal.setBackgroundColor(Color.rgb(224,224,224));
        }else{
            holder.diaMes.setText(String.valueOf(fecha.getDayOfMonth()));
            if(fecha.equals(CalendarioUtilidades.fechaSeleccionada)){
                holder.vistaPrincipal.setBackgroundColor(Color.rgb(255,238,88));
            }
        }

        //Mostrar imagen del evento en el calendario
        for (int i=0; i< Evento.listaEventos.size(); i++){
            if(Evento.listaEventos.get(i).getFecha().equals(fecha)){
                holder.imagenEvento.setImageURI(Uri.parse(Evento.listaEventos.get(i).getImagen()));
                holder.imagenEvento.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return diasMes.size();
    }



    public class ViewHolderCalendario extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView diaMes;
        View vistaPrincipal;
        ImageView imagenEvento;

        public ViewHolderCalendario(@NonNull View itemView) {
            super(itemView);
            diaMes = itemView.findViewById(R.id.lbl_celda_dia);
            vistaPrincipal = itemView.findViewById(R.id.vistaPrincipal);
            imagenEvento = itemView.findViewById(R.id.img_evento);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            LocalDate fecha = diasMes.get(getAdapterPosition());
            if (listener != null) {
                listener.diaSeleccionado(fecha);
            }
        }
    }
}

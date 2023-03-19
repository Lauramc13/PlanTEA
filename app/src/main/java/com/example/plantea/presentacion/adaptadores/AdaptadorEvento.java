package com.example.plantea.presentacion.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantea.R;
import com.example.plantea.dominio.Evento;

import java.util.ArrayList;

public class AdaptadorEvento extends RecyclerView.Adapter<AdaptadorEvento.ViewHolderEvento> {

    private ArrayList<Evento> eventos;
    private AdaptadorEvento.OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void deleteClick(int posicion);
        void viewClick(int posicion);
        void viewEventClick(int posicion);
    }

    public AdaptadorEvento(ArrayList<Evento> eventos, AdaptadorEvento.OnItemSelectedListener listener) {
        this.eventos = eventos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderEvento onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evento,null,false);
        return new ViewHolderEvento(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderEvento holder, int position) {
        holder.nombre.setText(eventos.get(position).getNombre());
        holder.hora.setText(String.valueOf(eventos.get(position).getHora()));
        switch (eventos.get(position).getVisible()) {
            case 0: //El plan no esta visible
                holder.visibilidad.setImageResource(R.drawable.ic_baseline_visibility_off_40);
                break;
            case 1: //El plan esta visible
                holder.visibilidad.setImageResource(R.drawable.ic_baseline_visibility_40);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public class ViewHolderEvento extends RecyclerView.ViewHolder{

        TextView nombre;
        TextView hora;
        ImageView eliminarEvento, visibilidad, verPlan;


        public ViewHolderEvento(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txt_evento);
            hora = itemView.findViewById(R.id.txt_hora);
            eliminarEvento = itemView.findViewById(R.id.img_borrarEvento);
            visibilidad = itemView.findViewById(R.id.img_eventoVisible);
            verPlan = itemView.findViewById(R.id.img_verEvento);

            verPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        listener.viewEventClick(position);
                    }
                }
            });

            eliminarEvento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        listener.deleteClick(position);
                    }
                }
            });

            visibilidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        if (eventos.get(position).getVisible() == 1){
                            visibilidad.setImageResource(R.drawable.ic_baseline_visibility_off_40);
                        }else{
                            visibilidad.setImageResource(R.drawable.ic_baseline_visibility_40);
                        }
                        listener.viewClick(position);
                    }
                }
            });
        }
    }
}

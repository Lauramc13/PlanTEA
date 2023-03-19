package com.example.plantea.presentacion.adaptadores;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantea.R;
import com.example.plantea.dominio.Pictograma;

import java.util.ArrayList;
import java.util.Stack;

public class AdaptadorPresentacion extends RecyclerView.Adapter<AdaptadorPresentacion.ViewHolderPictogramas> {

    ArrayList<Pictograma> listaPictogramas;

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSeleccionado(int posicion);
    }

    public AdaptadorPresentacion(ArrayList<Pictograma> listaPictogramas, AdaptadorPresentacion.OnItemSelectedListener listener) {
        this.listener = listener;
        this.listaPictogramas = listaPictogramas;
    }

    @NonNull
    @Override
    public ViewHolderPictogramas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictogramas,null,false);
        return new AdaptadorPresentacion.ViewHolderPictogramas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPictogramas holder, int position) {
        holder.titulo.setText(listaPictogramas.get(position).getTitulo());
        holder.imagen.setImageURI(Uri.parse(listaPictogramas.get(position).getImagen()));
        if(listaPictogramas.get(position).getCategoria() == 7){
            holder.premio.setVisibility(View.INVISIBLE);
            holder.card.setBackgroundResource(R.drawable.card_premio);
        } else if(listaPictogramas.get(position).getCategoria() == 6){
            holder.premio.setVisibility(View.INVISIBLE);
            holder.card.setBackgroundResource(R.drawable.card_espera);
        } else{
            holder.premio.setVisibility(View.INVISIBLE);
            holder.card.setBackgroundResource(R.drawable.card_personalizado);
        }
    }

    @Override
    public int getItemCount() {
            return listaPictogramas.size();
    }

    public class ViewHolderPictogramas extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titulo;
        ImageView imagen;
        ImageView premio;
        View card;

        public ViewHolderPictogramas(@NonNull View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.id_Texto);
            imagen = (ImageView) itemView.findViewById(R.id.id_Imagen);
            premio = (ImageView) itemView.findViewById(R.id.id_recompensa);
            card = (View) itemView.findViewById(R.id.id_card);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int posicion = getAdapterPosition();
            if (listener != null) {
                listener.onItemSeleccionado(posicion);
            }
            //Diseño item deshabilitado
            card.setBackgroundResource(R.drawable.card_disabled);
            imagen.setAlpha(0.7f);
            titulo.setAlpha(0.7f);
        }
    }
}

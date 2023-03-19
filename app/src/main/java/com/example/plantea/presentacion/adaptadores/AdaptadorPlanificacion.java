package com.example.plantea.presentacion.adaptadores;


import android.net.Uri;
import android.text.Layout;
import android.util.Log;
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


public class AdaptadorPlanificacion extends RecyclerView.Adapter<AdaptadorPlanificacion.ViewHolderPlanificacion>{

    ArrayList<Pictograma> listaPlanificacion;

    private AdaptadorPlanificacion.OnItemSelectedListener listener;

    public interface OnItemSelectedListener {

    }

    public AdaptadorPlanificacion(ArrayList<Pictograma> listaPlanificacion, AdaptadorPlanificacion.OnItemSelectedListener listener) {
        this.listener = listener;
        this.listaPlanificacion = listaPlanificacion;
    }

    @NonNull
    @Override
    public AdaptadorPlanificacion.ViewHolderPlanificacion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictogramas,null,false);
        return new AdaptadorPlanificacion.ViewHolderPlanificacion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPlanificacion.ViewHolderPlanificacion holder, int position) {
        holder.titulo.setText(listaPlanificacion.get(position).getTitulo());
        holder.imagen.setImageURI(Uri.parse(listaPlanificacion.get(position).getImagen()));

        //Normal
        holder.premio.setVisibility(View.INVISIBLE);
        holder.premio.setImageResource(R.drawable.categoria_recompensa);
        holder.card.setBackgroundResource(R.drawable.card_personalizado);

        if(listaPlanificacion.get(position).getCategoria() == 7){ //Premio
            holder.premio.setVisibility(View.VISIBLE);
            holder.card.setBackgroundResource(R.drawable.card_premio);
        }else if(listaPlanificacion.get(position).getCategoria() == 6){ //Espera
            holder.premio.setVisibility(View.VISIBLE);
            holder.premio.setImageResource(R.drawable.reloj);
            holder.card.setBackgroundResource(R.drawable.card_espera);
        }
    }

    @Override
    public int getItemCount() {
        return listaPlanificacion.size();
    }

    public class ViewHolderPlanificacion extends RecyclerView.ViewHolder{

        TextView titulo;
        ImageView imagen;
        ImageView premio;
        View card;

        public ViewHolderPlanificacion(@NonNull View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.id_Texto);
            imagen = (ImageView) itemView.findViewById(R.id.id_Imagen);
            premio = (ImageView) itemView.findViewById(R.id.id_recompensa);
            card = (View) itemView.findViewById(R.id.id_card);
        }
    }
}

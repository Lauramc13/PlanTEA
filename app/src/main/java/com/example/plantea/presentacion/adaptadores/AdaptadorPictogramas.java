package com.example.plantea.presentacion.adaptadores;

import android.content.ClipData;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantea.R;
import com.example.plantea.dominio.Pictograma;


import java.util.ArrayList;

public class AdaptadorPictogramas extends RecyclerView.Adapter<AdaptadorPictogramas.ViewHolderPictogramas> {

    ArrayList<Pictograma> listaPictogramas;

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSeleccionado(int posicion);
    }

    public AdaptadorPictogramas(ArrayList<Pictograma> listaPictogramas, OnItemSelectedListener listener) {
        this.listener = listener;
        this.listaPictogramas = listaPictogramas;
    }

    @NonNull
    @Override
    public ViewHolderPictogramas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictogramas,null,false);
        return new ViewHolderPictogramas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPictogramas holder, int position) {
        holder.titulo.setText(listaPictogramas.get(position).getTitulo());
        holder.imagen.setImageURI(Uri.parse(listaPictogramas.get(position).getImagen()));

    }

    @Override
    public int getItemCount() {
        return listaPictogramas.size();
    }

    public class ViewHolderPictogramas extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView titulo;
        ImageView imagen;

        public ViewHolderPictogramas(@NonNull View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.id_Texto);
            imagen = (ImageView) itemView.findViewById(R.id.id_Imagen);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            int posicion = getAdapterPosition();
            if (listener != null) {
                listener.onItemSeleccionado(posicion);
            }
            if (listaPictogramas.get(posicion).getCategoria() == 1){
                Log.d("tag", "CONSULTAS");
            } else {
                ClipData data = ClipData.newPlainText("","");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                Log.d("tag", "Selecciona pictograma");
            }
            return false;
        }
    }
}

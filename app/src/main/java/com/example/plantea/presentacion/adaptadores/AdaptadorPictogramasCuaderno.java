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

public class AdaptadorPictogramasCuaderno extends RecyclerView.Adapter<AdaptadorPictogramasCuaderno.ViewHolderPictogramas>  {

    ArrayList<Pictograma> listaPictogramas;

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void pictogramaCuaderno(int posicion);
    }

    public AdaptadorPictogramasCuaderno(ArrayList<Pictograma> listaPictogramas, AdaptadorPictogramasCuaderno.OnItemSelectedListener listener) {
        this.listener = listener;
        this.listaPictogramas = listaPictogramas;
    }

    @NonNull
    @Override
    public AdaptadorPictogramasCuaderno.ViewHolderPictogramas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictogramas,null,false);
        return new ViewHolderPictogramas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPictogramasCuaderno.ViewHolderPictogramas holder, int position) {
        holder.titulo.setText(listaPictogramas.get(position).getTitulo());
        holder.imagen.setImageURI(Uri.parse(listaPictogramas.get(position).getImagen()));
    }

    @Override
    public int getItemCount() {
        return listaPictogramas.size();
    }

    public class ViewHolderPictogramas extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titulo;
        ImageView imagen;

        public ViewHolderPictogramas(@NonNull View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.id_Texto);
            imagen = (ImageView) itemView.findViewById(R.id.id_Imagen);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int posicion = getAdapterPosition();
            if (listener != null) {
                listener.pictogramaCuaderno(posicion);
            }
        }
    }
}

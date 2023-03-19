package com.example.plantea.presentacion.adaptadores;

import android.net.Uri;
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

public class AdaptadorCuadernoActivity extends RecyclerView.Adapter<AdaptadorCuadernoActivity.ViewHolderPictogramas>  {

    ArrayList<Pictograma> listaPictogramas;

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void pictogramaCuaderno(int posicion);
    }

    public AdaptadorCuadernoActivity(ArrayList<Pictograma> listaPictogramas, AdaptadorCuadernoActivity.OnItemSelectedListener listener) {
        this.listener = listener;
        this.listaPictogramas = listaPictogramas;
    }

    @NonNull
    @Override
    public AdaptadorCuadernoActivity.ViewHolderPictogramas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuaderno,null,false);
        return new ViewHolderPictogramas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorCuadernoActivity.ViewHolderPictogramas holder, int position) {
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
            titulo = (TextView) itemView.findViewById(R.id.id_texto_item);
            imagen = (ImageView) itemView.findViewById(R.id.id_imagen_item);
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

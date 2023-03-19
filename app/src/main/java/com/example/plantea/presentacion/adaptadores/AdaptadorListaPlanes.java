package com.example.plantea.presentacion.adaptadores;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantea.R;
import com.example.plantea.dominio.Planificacion;

import java.util.ArrayList;

public class AdaptadorListaPlanes extends RecyclerView.Adapter<AdaptadorListaPlanes.ViewHolder> {

    ArrayList<Planificacion> planes;

    private AdaptadorListaPlanes.OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void deleteClick(int posicion);
        void editClick(int posicion);
        void duplicateClick(int posicion);
        void planSeleccionado(int posicion);
    }

    public AdaptadorListaPlanes(ArrayList<Planificacion> planes, AdaptadorListaPlanes.OnItemSelectedListener listener) {
        this.planes = planes;
        this.listener = listener;
    }


    @NonNull
    @Override
    public AdaptadorListaPlanes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_planificacion,null,false);
        return new AdaptadorListaPlanes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaPlanes.ViewHolder holder, int position) {
        holder.titulo.setText(planes.get(position).getTitulo());
    }

    @Override
    public int getItemCount() {
        return planes.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        TextView titulo;
        ImageView eliminar,editar,duplicar;
        CardView card;

        public ViewHolder(@NonNull View itemView)  {
            super(itemView);
            titulo = itemView.findViewById(R.id.lbl_Planificacion);
            eliminar = itemView.findViewById(R.id.icon_delete);
            editar = itemView.findViewById(R.id.icon_edit);
            duplicar = itemView.findViewById(R.id.icon_copy);
            card = itemView.findViewById(R.id.card_plan);
            itemView.setOnLongClickListener(this);

            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        listener.editClick(position);
                    }
                }
            });
            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        listener.deleteClick(position);
                    }
                }
            });
            duplicar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        listener.duplicateClick(position);
                    }
                }
            });

        }

        @Override
        public boolean onLongClick(View view) {
            int posicion = getAdapterPosition();
            if (listener != null) {
                listener.planSeleccionado(posicion);
            }
            card.setCardBackgroundColor(Color.rgb(224,224,224));
            return false;
        }
    }
}

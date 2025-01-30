package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion.ViewHolderPlanificacion
import kotlin.collections.ArrayList


class AdaptadorPlanificacion(var listaPlanificacion: ArrayList<Pictograma>) : RecyclerView.Adapter<ViewHolderPlanificacion>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPlanificacion {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPlanificacion(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPlanificacion, position: Int) {
        holder.titulo.text = listaPlanificacion[position].titulo
        holder.imagen.setImageBitmap(listaPlanificacion[position].imagen)

        holder.card.setBackgroundResource(R.drawable.card_personalizado)
        holder.borrar.visibility = View.VISIBLE

//        if (listaPlanificacion[position].categoria == 4) {
//            holder.card.setBackgroundResource(R.drawable.card_espera)
//        }

        configPicto(holder)
    }

    override fun getItemCount(): Int {
        return listaPlanificacion.size
    }

    private fun configPicto(holder: AdaptadorPlanificacion.ViewHolderPlanificacion){
        val sharedPreferences = holder.itemView.context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        when (sharedPreferences.getString("configPictogramas", "default")) {
            "default" -> {
                holder.imagen.visibility = View.VISIBLE
                holder.titulo.visibility = View.VISIBLE
            }
            "imagen" -> {
                holder.imagen.visibility = View.VISIBLE
                holder.titulo.visibility = View.GONE
            }
            "texto" -> {
                holder.imagen.visibility = View.GONE
                holder.titulo.visibility = View.VISIBLE
            }
        }
    }


    fun updateListaPlan(newList : ArrayList<Pictograma>){
        listaPlanificacion = newList
        notifyDataSetChanged()
    }

    inner class ViewHolderPlanificacion(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView = itemView.findViewById(R.id.id_Texto)
        var imagen: ImageView = itemView.findViewById(R.id.id_Imagen)
        var borrar: ImageView = itemView.findViewById(R.id.btn_borrarPicto)
        var card: ConstraintLayout = itemView.findViewById(R.id.id_card)

        init {
            borrar.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listaPlanificacion.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

        }


    }
}
package com.example.plantea.presentacion.adaptadores

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion.ViewHolderPlanificacion

class AdaptadorPlanificacion(var listaPlanificacion: ArrayList<Pictograma>, private val listener: OnItemSelectedListener) : RecyclerView.Adapter<ViewHolderPlanificacion>() {
    interface OnItemSelectedListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPlanificacion {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPlanificacion(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPlanificacion, position: Int) {
        holder.titulo.text = listaPlanificacion!![position]!!.titulo
        holder.imagen.setImageURI(Uri.parse(listaPlanificacion!![position]!!.imagen))

        //Normal
        holder.premio.visibility = View.INVISIBLE
        holder.premio.setImageResource(R.drawable.categoria_recompensa)
        holder.card.setBackgroundResource(R.drawable.card_personalizado)
        if (listaPlanificacion!![position]!!.categoria == 7) { //Premio
            holder.premio.visibility = View.VISIBLE
            holder.card.setBackgroundResource(R.drawable.card_premio)
        } else if (listaPlanificacion!![position]!!.categoria == 6) { //Espera
            holder.premio.visibility = View.VISIBLE
            holder.premio.setImageResource(R.drawable.reloj)
            holder.card.setBackgroundResource(R.drawable.card_espera)
        }
    }

    override fun getItemCount(): Int {
        return listaPlanificacion!!.size
    }

    inner class ViewHolderPlanificacion(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView
        var imagen: ImageView
        var premio: ImageView
        var card: View

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            premio = itemView.findViewById<View>(R.id.id_recompensa) as ImageView
            card = itemView.findViewById(R.id.id_card) as View
        }
    }
}
package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Pictograma
import com.google.android.material.card.MaterialCardView

class AdaptadorPictogramaEntretenimiento(var listaPictogramas: ArrayList<Pictograma>?, val idPicto: Int, private val listener: AdaptadorPictogramaEntretenimiento.OnItemSelectedListener?): RecyclerView.Adapter<AdaptadorPictogramaEntretenimiento.ViewHolderPictogramas>() {

    interface OnItemSelectedListener {
        fun onItemSeleccionadoEntre(idPicto: Int)
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorPictogramaEntretenimiento.ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_picto_default, parent, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: AdaptadorPictogramaEntretenimiento.ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context
        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageBitmap(listaPictogramas!![position].imagen)

        if(idPicto == listaPictogramas!![position].id!!.toInt()) {
            holder.card.setBackgroundResource(R.drawable.card_personalizado_categoria)
        }else{
            holder.card.setBackgroundResource(R.drawable.card_personalizado)
        }
    }


    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView){
        var titulo: TextView = itemView.findViewById(R.id.id_Texto)
        var imagen: ImageView = itemView.findViewById(R.id.id_Imagen)
        var card: MaterialCardView = itemView.findViewById(R.id.id_card)

        init {
            card.setOnClickListener {
                val idPictograma = listaPictogramas!![bindingAdapterPosition].id!!.toInt()
                card.setBackgroundResource(R.drawable.card_personalizado_categoria)
                listener?.onItemSeleccionadoEntre(idPictograma)
            }
        }
    }
}
package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma

class AdaptadorNuevoPicto( var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?): RecyclerView.Adapter<AdaptadorNuevoPicto.ViewHolderPictogramas>() {

    var currentPosition = RecyclerView.NO_POSITION

    interface OnItemSelectedListener {
        fun onNuevoPicto(picto: Pictograma?)
    }


    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_picto_entretenimiento, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context
        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageBitmap(listaPictogramas!![position].imagen)

        if (currentPosition == position) {
            holder.card.setBackgroundResource(R.drawable.card_personalizado_categoria)
        } else {
            holder.card.setBackgroundResource(R.drawable.card_personalizado)
        }
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView){
        var titulo: TextView
        var imagen: ImageView
        var card: View

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            card = itemView.findViewById(R.id.id_card) as View

            card.setOnClickListener {
                card.setBackgroundResource(R.drawable.card_personalizado_categoria)
                listener?.onNuevoPicto(listaPictogramas!![bindingAdapterPosition])

                 val previousPosition = currentPosition
                 currentPosition = bindingAdapterPosition
                 notifyItemChanged(previousPosition)
                 notifyItemChanged(currentPosition)
            }
        }
    }

}
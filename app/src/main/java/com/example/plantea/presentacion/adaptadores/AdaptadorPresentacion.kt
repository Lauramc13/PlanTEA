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

class AdaptadorPresentacion(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPresentacion.ViewHolderPictogramas>() {
    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))
        if (listaPictogramas!![position].categoria == 7) {
            holder.premio.visibility = View.INVISIBLE
            holder.card.setBackgroundResource(R.drawable.card_premio)
        } else if (listaPictogramas!![position].categoria == 6) {
            holder.premio.visibility = View.INVISIBLE
            holder.card.setBackgroundResource(R.drawable.card_espera)
        } else {
            holder.premio.visibility = View.INVISIBLE
            holder.card.setBackgroundResource(R.drawable.card_personalizado)
        }
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var titulo: TextView
        var imagen: ImageView
        var premio: ImageView
        var card: View

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            premio = itemView.findViewById<View>(R.id.id_recompensa) as ImageView
            card = itemView.findViewById(R.id.id_card) as View
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val posicion = bindingAdapterPosition
            listener?.onItemSeleccionado(posicion)
            //Diseño item deshabilitado
            card.setBackgroundResource(R.drawable.card_disabled)
            imagen.alpha = 0.7f
            titulo.alpha = 0.7f
        }
    }
}
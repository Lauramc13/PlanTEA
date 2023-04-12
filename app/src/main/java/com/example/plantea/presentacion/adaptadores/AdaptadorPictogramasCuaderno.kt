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

class AdaptadorPictogramasCuaderno(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPictogramasCuaderno.ViewHolderPictogramas>() {
    interface OnItemSelectedListener {
        fun pictogramaCuaderno(posicion: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var titulo: TextView
        var imagen: ImageView

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val posicion = bindingAdapterPosition
            listener?.pictogramaCuaderno(posicion)
        }
    }
}
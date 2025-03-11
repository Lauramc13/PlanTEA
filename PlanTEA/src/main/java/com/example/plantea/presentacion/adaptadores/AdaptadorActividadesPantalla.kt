package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Actividad
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView

class AdaptadorActividadesPantalla(private val actividades: ArrayList<Actividad>?, private val listener: OnItemSelectedListenerActividad?) : RecyclerView.Adapter<AdaptadorActividadesPantalla.ViewHolderAcitividades>() {

    interface OnItemSelectedListenerActividad {
        fun onClick(context: Context, position: Int)
        fun onClickNuevaActividad(context: Context, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAcitividades {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_actividades, parent, false)
        return ViewHolderAcitividades(view)
    }

    override fun onBindViewHolder(holder: ViewHolderAcitividades, position: Int) {
        if(actividades!![position].id == "-1"){
            holder.imagen.setImageResource(R.drawable.svg_add_20)
            holder.nombre.text = actividades[position].nombre
        } else {
            holder.imagen.setImageBitmap(actividades[position].imagen)
            holder.nombre.text = actividades[position].nombre
        }
    }

    override fun getItemCount(): Int {
        return actividades!!.size
    }

    inner class ViewHolderAcitividades(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagen : ShapeableImageView = itemView.findViewById(R.id.imagen)
        var nombre: TextView = itemView.findViewById(R.id.nombre)
        private var cardImagen : MaterialCardView = itemView.findViewById(R.id.newUser)

        init {
            cardImagen.setOnClickListener {
                if(actividades!![bindingAdapterPosition].id == "-1"){
                    listener?.onClickNuevaActividad(itemView.context, bindingAdapterPosition)
                } else {
                    listener?.onClick(itemView.context, bindingAdapterPosition)
                }
            }
        }
    }
}
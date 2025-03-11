package com.example.plantea.presentacion.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Actividad
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView

class ActividadAdapter(private val positionUser: Int, private val actividades: ArrayList<Actividad>?, private val listener: OnItemSelectedListenerActividad?) : RecyclerView.Adapter<ActividadAdapter.ViewHolderAcitividades>() {

    interface OnItemSelectedListenerActividad {
        fun onClick(isEdit: Boolean, positionUser: Int, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAcitividades {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_actividad, parent, false)
        return ViewHolderAcitividades(view)
    }

    override fun onBindViewHolder(holder: ViewHolderAcitividades, position: Int) {
        if(actividades!![position].imagen != null){
            holder.imagen.setImageBitmap(actividades[position].imagen)
        }
    }

    override fun getItemCount(): Int {
        return actividades!!.size
    }

    inner class ViewHolderAcitividades(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagen : ShapeableImageView = itemView.findViewById(R.id.imagen)
        private var cardImagen : MaterialCardView = itemView.findViewById(R.id.newUser)

        init {
            cardImagen.setOnClickListener {
                if(bindingAdapterPosition == actividades!!.size - 1){
                    listener?.onClick(false, positionUser, bindingAdapterPosition)
                }else{
                    listener?.onClick(true, positionUser, bindingAdapterPosition)
                }
            }
        }
    }
}
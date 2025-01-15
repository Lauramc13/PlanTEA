package com.example.plantea.presentacion.adaptadores

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Actividad
import com.example.plantea.dominio.Usuario
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout

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
        var imagen : ShapeableImageView
        var cardImagen : MaterialCardView

        init {
            imagen = itemView.findViewById(R.id.imagen)
            cardImagen = itemView.findViewById(R.id.newUser)

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
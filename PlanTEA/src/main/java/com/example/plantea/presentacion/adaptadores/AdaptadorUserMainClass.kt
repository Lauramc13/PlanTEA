package com.example.plantea.presentacion.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Usuario
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView

class AdaptadorUserMainClass(private val users: ArrayList<Usuario>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorUserMainClass.ViewHolderUserTEA>() {

    interface OnItemSelectedListener {
        fun onClickUser(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUserTEA {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_tea_main, null, false)
        return ViewHolderUserTEA(view)
    }

    override fun onBindViewHolder(holder: ViewHolderUserTEA, position: Int) {
       holder.nombre.text = users!![position].name
        if(users[position].imagen != null){
            holder.imagen.setImageBitmap(users[position].imagen)
        }

    }

    override fun getItemCount(): Int {
        return users!!.size
    }

    inner class ViewHolderUserTEA(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: MaterialCardView = itemView.findViewById(R.id.cardView)
        var imagen : ShapeableImageView = itemView.findViewById(R.id.imagen)
        var nombre: TextView = itemView.findViewById(R.id.nombre)

        init {
            card.setOnClickListener {
                listener?.onClickUser(bindingAdapterPosition)
            }
        }
    }

}
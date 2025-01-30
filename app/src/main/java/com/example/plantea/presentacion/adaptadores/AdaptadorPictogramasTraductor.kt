package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma


class AdaptadorPictogramasTraductor(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPictogramasTraductor.ViewHolderPictogramas>() {

    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int, context: Context)
        fun onLongItemSeleccionado(posicion: Int, context: Context)
        fun onItemEliminado(posicion: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageBitmap(listaPictogramas!![position].imagen)
        holder.removePicto.visibility = View.VISIBLE

        configPicto(holder)
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    private fun configPicto(holder: AdaptadorPictogramasTraductor.ViewHolderPictogramas){
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

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView = itemView.findViewById(R.id.id_Texto)
        var imagen: ImageView = itemView.findViewById(R.id.id_Imagen)
        val removePicto : ImageView = itemView.findViewById(R.id.btn_borrarPicto)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                listener?.onItemSeleccionado(position, itemView.context)
            }
            itemView.setOnLongClickListener{
                val position = bindingAdapterPosition
                listener?.onLongItemSeleccionado(position, itemView.context)
                return@setOnLongClickListener true
            }

            removePicto.setOnClickListener {
                listener?.onItemEliminado(bindingAdapterPosition)
            }
        }
    }
}

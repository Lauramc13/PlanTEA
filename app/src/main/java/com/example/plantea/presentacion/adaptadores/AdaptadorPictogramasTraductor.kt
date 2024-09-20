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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas_traductor, null, false)
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

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        var titulo: TextView
        var imagen: ImageView
        val removePicto : ImageView

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            removePicto = itemView.findViewById<View>(R.id.btn_removePicto) as ImageView
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)

            removePicto.setOnClickListener {
                listener?.onItemEliminado(bindingAdapterPosition)
            }
        }

        override fun onClick(view: View) {
            val position = bindingAdapterPosition
            listener?.onItemSeleccionado(position, view.context)
        }

        override fun onLongClick(view: View): Boolean {
            val position = bindingAdapterPosition
            listener?.onLongItemSeleccionado(position, view.context)
            return true
        }


    }
}

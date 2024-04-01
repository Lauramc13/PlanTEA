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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas_traductor, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))

        holder.removePicto.visibility = View.VISIBLE
        holder.removePicto.setOnClickListener {
            listaPictogramas!!.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
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

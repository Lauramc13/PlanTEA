package com.example.plantea.presentacion.adaptadores

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma

class AdaptadorPictogramasCuaderno(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPictogramasCuaderno.ViewHolderPictogramas>() {

    var isBusqueda: Boolean = false

    interface OnItemSelectedListener {
        fun pictogramaCuaderno(posicion: Int)
        fun addPicto(pictograma: Pictograma)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictograma_cuaderno, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))

        if(isBusqueda){
            holder.btnAniadir.visibility = View.VISIBLE
        }else{
            holder.btnAniadir.visibility = View.INVISIBLE
            holder.btnNoAniair.visibility = View.INVISIBLE
        }

        holder.btnNoAniair.setOnClickListener {
            //segun la posicion eliminar el pictograma en el cuaderno
            holder.btnNoAniair.visibility = View.INVISIBLE
            holder.btnAniadir.visibility = View.VISIBLE
           // listener?.addPicto(listaPictogramas!![position])
        }

        holder.btnAniadir.setOnClickListener {
            //segun la posicion guardar el pictograma en el cuaderno
            holder.btnNoAniair.visibility = View.VISIBLE
            holder.btnAniadir.visibility = View.INVISIBLE
            listener?.addPicto(listaPictogramas!![position])
        }
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    fun updateData(newPictogramasList: ArrayList<Pictograma>? ) {
        listaPictogramas = newPictogramasList
        notifyDataSetChanged()
    }

    fun updateDataBusqueda(newPictogramasList: ArrayList<Pictograma>?){
        listaPictogramas = newPictogramasList
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var titulo: TextView
        var imagen: ImageView
        var btnAniadir: Button
        var btnNoAniair: Button

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            btnAniadir = itemView.findViewById<View>(R.id.btn_sin_aniadir) as Button
            btnNoAniair = itemView.findViewById<View>(R.id.btn_aniadido) as Button
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val posicion = bindingAdapterPosition
            listener?.pictogramaCuaderno(posicion)
        }
    }
}
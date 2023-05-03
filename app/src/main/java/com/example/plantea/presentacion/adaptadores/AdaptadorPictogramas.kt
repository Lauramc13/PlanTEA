package com.example.plantea.presentacion.adaptadores

import android.content.ClipData
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.planificador.CrearPlanActivity


class AdaptadorPictogramas(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPictogramas.ViewHolderPictogramas>() {

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
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {
        var titulo: TextView
        var imagen: ImageView

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            itemView.setOnTouchListener(this)
        }

        // override fun onClick(view: View) {
        //     val posicion = bindingAdapterPosition
        //     listener?.onItemSeleccionado(posicion)
        //     if (listaPictogramas!![posicion].categoria == 1) {
        //         Log.d("tag", "CONSULTAS")
        //     } else {
        //         val data = ClipData.newPlainText("", "")
        //         val shadowBuilder = View.DragShadowBuilder(view)
        //         val pictograma = listaPictogramas!![posicion]
        //         var planActivity = CrearPlanActivity()
        //         planActivity.aniadirPicto()
        //         //.add(pictograma)
        //         //adaptador.notifyDataSetChanged()
        //         Log.d("tag", "Selecciona pictograma")
        //     }
        // }

        override fun onTouch(view: View?, p1: MotionEvent?): Boolean {
            val posicion = bindingAdapterPosition
            listener?.onItemSeleccionado(posicion)
            if (listaPictogramas!![posicion].categoria == 1) {
                Log.d("tag", "CONSULTAS")
            } else {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                if (view != null) {
                    view. startDragAndDrop(data, shadowBuilder, view, 0)
                }
                Log.d("tag", "Selecciona pictograma")
            }
            return false
        }
    }
}
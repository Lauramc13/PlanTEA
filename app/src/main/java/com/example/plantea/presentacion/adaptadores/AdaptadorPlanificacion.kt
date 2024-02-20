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
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion.ViewHolderPlanificacion
import kotlin.collections.ArrayList


class AdaptadorPlanificacion(var listaPlanificacion: ArrayList<Pictograma>, private val listener: OnItemSelectedListener) : RecyclerView.Adapter<ViewHolderPlanificacion>() {
    interface OnItemSelectedListener{
        fun onHistoriaClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPlanificacion {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPlanificacion(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPlanificacion, position: Int) {
        holder.titulo.text = listaPlanificacion[position].titulo
        holder.imagen.setImageURI(Uri.parse(listaPlanificacion[position].imagen))

        //Normal
        //holder.premio.visibility = View.INVISIBLE
        //holder.premio.setImageResource(R.drawable.categoria_recompensa)
        holder.card.setBackgroundResource(R.drawable.card_personalizado)
        holder.borrar.visibility = View.VISIBLE
        holder.historia.visibility = View.VISIBLE

        if (listaPlanificacion[position].categoria == 9) { //Premio
            //holder.premio.visibility = View.VISIBLE
            holder.card.setBackgroundResource(R.drawable.card_premio)
        } else if (listaPlanificacion[position].categoria == 8) { //Espera
            //holder.premio.visibility = View.VISIBLE
            //holder.premio.setImageResource(R.drawable.reloj)
            holder.card.setBackgroundResource(R.drawable.card_espera)
        }

        if(listaPlanificacion[position].historia.toString() == "null"){
            holder.historia.setImageResource(R.drawable.bocadillo_historia_off)
        }else{
            holder.historia.setImageResource(R.drawable.bocadillo_historia_on)
        }
    }

    override fun getItemCount(): Int {
        return listaPlanificacion.size
    }

    inner class ViewHolderPlanificacion(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView
        var imagen: ImageView
        //var premio: ImageView
        var borrar: ImageView
        var historia: ImageView
        var card: View

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            //premio = itemView.findViewById<View>(R.id.id_recompensa) as ImageView
            borrar = itemView.findViewById<View>(R.id.btn_borrarPicto) as ImageView
            historia = itemView.findViewById<View>(R.id.btn_historiaPicto) as ImageView
            card = itemView.findViewById(R.id.id_card) as View


            borrar.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listaPlanificacion.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            //obtenemos la posicion del pictograma y guardamos en el objeto Pictograma la historia, si el pictograma tenia historia de antes tambien la mostramos
            historia.setOnClickListener {
                listener.onHistoriaClick(bindingAdapterPosition)
            }
        }


    }
}
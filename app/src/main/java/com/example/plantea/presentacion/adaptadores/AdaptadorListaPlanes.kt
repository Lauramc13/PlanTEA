package com.example.plantea.presentacion.adaptadores

import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Planificacion

class AdaptadorListaPlanes(private var planes: ArrayList<Planificacion>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorListaPlanes.ViewHolder>() {
    interface OnItemSelectedListener {
        fun deleteClick(posicion: Int)
        fun editClick(posicion: Int)
        fun duplicateClick(posicion: Int)
        fun planSeleccionado(posicion: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_planificacion, null, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titulo.text = planes!![position].titulo
    }

    override fun getItemCount(): Int {
        return planes!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView
        private var eliminar: ImageView
        private var editar: ImageView
        private var duplicar: ImageView
        var card: CardView

        init {
            titulo = itemView.findViewById(R.id.lbl_Planificacion)
            eliminar = itemView.findViewById(R.id.icon_delete)
            editar = itemView.findViewById(R.id.icon_edit)
            duplicar = itemView.findViewById(R.id.icon_copy)
            card = itemView.findViewById(R.id.card_plan)
            itemView.setOnClickListener{
                val posicion = bindingAdapterPosition
                listener?.planSeleccionado(posicion)
                val currentNightMode = itemView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

                if (isDarkMode) {
                    card.setCardBackgroundColor(Color.rgb(127, 144, 159)) // Set the desired background color for dark mode
                } else {
                    card.setCardBackgroundColor(Color.rgb(193, 235, 235)) // Set the desired background color for light mode
                }

            }

            editar.setOnClickListener {
                if (listener != null) {
                    val position = bindingAdapterPosition
                    listener.editClick(position)
                }
            }
            eliminar.setOnClickListener {
                if (listener != null) {
                    val position = bindingAdapterPosition
                    listener.deleteClick(position)
                }
            }
            duplicar.setOnClickListener {
                if (listener != null) {
                    val position = bindingAdapterPosition
                    listener.duplicateClick(position)
                }
            }
        }


    }
}
package com.example.plantea.presentacion.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Evento
import com.example.plantea.presentacion.adaptadores.AdaptadorEvento.ViewHolderEvento

class AdaptadorEvento(private val eventos: ArrayList<Evento>, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<ViewHolderEvento>() {
    interface OnItemSelectedListener {
        fun deleteClick(posicion: Int)
        fun viewClick(posicion: Int)
        fun viewExportClick(posicion: Int)
        fun viewEventClick(posicion: Int)
        fun editEvento(posicion: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEvento {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento, null, false)
        return ViewHolderEvento(view)
    }

    override fun onBindViewHolder(holder: ViewHolderEvento, position: Int) {
        holder.nombre.text = eventos[position].nombre
        holder.hora.text = eventos[position].horaInicio.toString()
        when (eventos[position].visible) {
            0 -> holder.visibilidad.setImageResource(R.drawable.svg_eye_off)
            1 -> holder.visibilidad.setImageResource(R.drawable.svg_eye_on)
        }
    }

    override fun getItemCount(): Int {
        return eventos.size
    }

    inner class ViewHolderEvento(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombre: TextView = itemView.findViewById(R.id.txt_evento)
        var hora: TextView = itemView.findViewById(R.id.txt_hora)
        private var eliminarEvento: ImageView = itemView.findViewById(R.id.img_borrarEvento)
        private var export : ImageView = itemView.findViewById(R.id.img_exportCalendar)
        var visibilidad: ImageView = itemView.findViewById(R.id.img_eventoVisible)
        private var edit: ImageView = itemView.findViewById(R.id.img_editarEvento)
        private var verPlan: CardView = itemView.findViewById(R.id.card_Evento)

        init {
            verPlan.setOnClickListener {
               if (listener != null) {
                   val position = bindingAdapterPosition
                   listener.viewEventClick(position)
               }
            }

            eliminarEvento.setOnClickListener {
                if (listener != null) {
                    val position = bindingAdapterPosition
                    listener.deleteClick(position)
                }
            }

            visibilidad.setOnClickListener {
                if (listener != null) {
                    val position = bindingAdapterPosition
                    if (eventos[position].visible == 1) {
                        visibilidad.setImageResource(R.drawable.ic_baseline_visibility_off_40)
                    } else {
                        visibilidad.setImageResource(R.drawable.ic_baseline_visibility_40)
                    }
                    listener.viewClick(position)
                }
            }

            edit.setOnClickListener {
                if (listener != null) {
                    val position = bindingAdapterPosition
                    listener.editEvento(position)
                }
            }

            export.setOnClickListener {
                if (listener != null) {
                    val position = bindingAdapterPosition
                    listener.viewExportClick(position)
                }
            }
        }
    }
}
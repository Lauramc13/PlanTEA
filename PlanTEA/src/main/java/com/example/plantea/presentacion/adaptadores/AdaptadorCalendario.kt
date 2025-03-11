package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.objetos.Evento
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario.ViewHolderCalendario
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate


class AdaptadorCalendario(private val diasMes: ArrayList<LocalDate?>, days: Array<String>, private val listaEventos: ArrayList<Evento>, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<ViewHolderCalendario>() {

    private val daysOfWeek = days
    var fecha: LocalDate? = LocalDate.now()
    var selectedDay = -1
    private val VIEW_TYPE_DAY_OF_MONTH = 1
    private val VIEW_TYPE_DAY_OF_WEEK = 2

    interface OnItemSelectedListener {
        fun diaSeleccionado(context: Context?, fecha: LocalDate, position: Int, selectedDay: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalendario {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendario_celda, parent, false)

        if (viewType == VIEW_TYPE_DAY_OF_MONTH) {
            view.setBackgroundResource(R.drawable.round_bg)
        }

        return ViewHolderCalendario(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCalendario, position: Int) {
        if (position < daysOfWeek.size) {
            holder.diaMes.text = daysOfWeek[position]
            holder.vistaPrincipal.isClickable = false

        } else {
            fecha = diasMes[position-daysOfWeek.size]

            if(fecha != null){
                holder.diaMes.text = fecha!!.dayOfMonth.toString()
                if (fecha == CalendarioUtilidades.fechaSeleccionada) {
                    holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg_selected)
                    selectedDay = position
                }else{
                    holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg)
                }
            }else{
                holder.diaMes.text = ""
                holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg_none)
            }

            for (i in listaEventos.indices) {
                if (listaEventos[i].fecha == fecha && fecha != CalendarioUtilidades.fechaSeleccionada) {
                    holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg_evento)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < daysOfWeek.size) {
            // View type for days of the week
            VIEW_TYPE_DAY_OF_WEEK
        } else {
            // View type for days of the month
            VIEW_TYPE_DAY_OF_MONTH
        }
    }

    override fun getItemCount(): Int {
        return diasMes.size + daysOfWeek.size
    }

    inner class ViewHolderCalendario(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var diaMes: TextView = itemView.findViewById(R.id.lbl_celda_dia)
        var vistaPrincipal: MaterialCardView = itemView.findViewById(R.id.vistaPrincipal)

        init {
            vistaPrincipal.setOnClickListener{
                val fecha = diasMes[bindingAdapterPosition-daysOfWeek.size]
                if (fecha != null) {
                    listener?.diaSeleccionado(itemView.context, fecha, bindingAdapterPosition, selectedDay)
                }
            }
        }
    }
}
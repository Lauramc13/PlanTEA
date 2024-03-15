package com.example.plantea.presentacion.adaptadores


import android.R.attr.height
import android.R.attr.width
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario.ViewHolderCalendario
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate


class AdaptadorCalendario(private val diasMes: ArrayList<LocalDate?>, days: Array<String>, private val listaEventos: ArrayList<Evento>, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<ViewHolderCalendario>() {

    private val daysOfWeek = days
    private val VIEW_TYPE_DAY_OF_MONTH = 1
    private val VIEW_TYPE_DAY_OF_WEEK = 2

    interface OnItemSelectedListener {
        fun diaSeleccionado(context: Context?, fecha: LocalDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalendario {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendario_celda, parent, false)

        val layoutParams = view.layoutParams

        if (viewType == VIEW_TYPE_DAY_OF_MONTH) {
            view.setBackgroundResource(R.drawable.round_bg)
            //set item height to the same width
           // layoutParams.height = layoutParams.width
        }

        return ViewHolderCalendario(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCalendario, position: Int) {
        if (position < daysOfWeek.size) {
            // Bind the days of the week to the corresponding positions
            holder.diaMes.text = daysOfWeek[position]
            holder.vistaPrincipal.isClickable = false

        } else {
            val fecha = diasMes[position-daysOfWeek.size]
            //holder.diaMes.setTextColor(holder.itemView.context.resources.getColor(R.color.md_theme_dark_surface))

            //Mostrar imagen del evento en el calendario
            for (i in listaEventos.indices) {
                if (listaEventos[i].fecha == fecha) {
                    holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg_evento)
                }
            }

            if (fecha == null) {
                holder.diaMes.text = ""
                holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg_none)
            } else {
                holder.diaMes.text = fecha.dayOfMonth.toString()
                if (fecha == CalendarioUtilidades.fechaSeleccionada) {
                    holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg_selected)
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

    inner class ViewHolderCalendario(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var diaMes: TextView
        var vistaPrincipal: MaterialCardView

        init {
            diaMes = itemView.findViewById(R.id.lbl_celda_dia)
            vistaPrincipal = itemView.findViewById(R.id.vistaPrincipal)
            vistaPrincipal.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val fecha = diasMes[bindingAdapterPosition-daysOfWeek.size]
            if (fecha != null) {
                listener?.diaSeleccionado(view.context, fecha)
            }
        }
    }
}
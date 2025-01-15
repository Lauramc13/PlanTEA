package com.example.plantea.presentacion.adaptadores

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.DiaMes
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate


class AdaptadorCalendarioMensualFechas(var fechas: ArrayList<DiaMes>, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorCalendarioMensualFechas.ViewHolderCalendarioMensual>() {

    var fecha: LocalDate? = LocalDate.now()
    interface OnItemSelectedListener {
        fun diaSeleccionadoFecha(context: Context?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalendarioMensual {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_calendario_mensual_fecha, parent, false)

        return ViewHolderCalendarioMensual(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderCalendarioMensual, position: Int) {
        val color = fechas[position].color ?: "gray"
        val diaTex = getString(holder.itemView.context, R.string.dia2)
        holder.fecha.text = "$diaTex ${fechas[position].fecha?.dayOfMonth}"
        holder.titulo.text = fechas[position].titulo

        if(Configuration.UI_MODE_NIGHT_YES == (holder.itemView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK))
            holder.vistaPrincipal.setCardBackgroundColor(CommonUtils.getColorDark(holder.itemView.context, color))
        else
            holder.vistaPrincipal.setCardBackgroundColor(CommonUtils.getColor(holder.itemView.context, color))
    }

    override fun getItemCount(): Int {
        return fechas.size
    }

    inner class ViewHolderCalendarioMensual(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vistaPrincipal: MaterialCardView = itemView.findViewById(R.id.vistaPrincipal)
        var fecha :TextView = itemView.findViewById(R.id.fecha)
        var titulo :TextView = itemView.findViewById(R.id.titulo)

        init {
            vistaPrincipal.setOnClickListener{
                val fecha = fechas[bindingAdapterPosition].fecha
                if (fecha != null) {
                    listener?.diaSeleccionadoFecha(itemView.context, bindingAdapterPosition)
                }
            }
        }

    }
}
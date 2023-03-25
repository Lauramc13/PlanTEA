package com.example.plantea.presentacion.adaptadores

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.presentacion.adaptadores.AdaptadorCalendario.ViewHolderCalendario
import java.time.LocalDate

class AdaptadorCalendario(private val diasMes: ArrayList<LocalDate?>, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<ViewHolderCalendario>() {
    interface OnItemSelectedListener {
        fun diaSeleccionado(fecha: LocalDate?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalendario {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendario_celda, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return ViewHolderCalendario(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCalendario, position: Int) {
        val fecha = diasMes[position]
        if (fecha == null) {
            holder.diaMes.text = ""
            holder.vistaPrincipal.setBackgroundColor(Color.rgb(224, 224, 224))
        } else {
            holder.diaMes.text = fecha.dayOfMonth.toString()
            if (fecha == CalendarioUtilidades.fechaSeleccionada) {
                holder.vistaPrincipal.setBackgroundColor(Color.rgb(255, 238, 88))
            }
        }

        //Mostrar imagen del evento en el calendario
        for (i in Evento.listaEventos!!.indices) {
            if (Evento.listaEventos!![i].fecha == fecha) {
                holder.imagenEvento.setImageURI(Uri.parse(Evento.listaEventos!![i].imagen))
                holder.imagenEvento.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return diasMes.size
    }

    inner class ViewHolderCalendario(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var diaMes: TextView
        var vistaPrincipal: View
        var imagenEvento: ImageView

        init {
            diaMes = itemView.findViewById(R.id.lbl_celda_dia)
            vistaPrincipal = itemView.findViewById(R.id.vistaPrincipal)
            imagenEvento = itemView.findViewById(R.id.img_evento)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val fecha = diasMes[adapterPosition]
            listener?.diaSeleccionado(fecha)
        }
    }
}
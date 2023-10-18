package com.example.plantea.presentacion.adaptadores


import android.util.TypedValue
import android.view.Gravity
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
        fun diaSeleccionado(fecha: LocalDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalendario {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendario_celda, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.14).toInt()
        layoutParams.width = (parent.height * 0.14).toInt()
        val layoutParams2 = view.layoutParams as ViewGroup.MarginLayoutParams
        view.setBackgroundResource(R.drawable.round_bg)
        view.foregroundGravity = Gravity.CENTER
        layoutParams2.setMargins(18, 10, 0, 7)
        return ViewHolderCalendario(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCalendario, position: Int) {
        val fecha = diasMes[position]


        //Mostrar imagen del evento en el calendario
        for (i in Evento.listaEventos!!.indices) {
            if (Evento.listaEventos!![i].fecha == fecha) {
                holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg_evento)
                //holder.imagenEvento.setImageURI(Uri.parse(Evento.listaEventos!![i].imagen))
                //holder.imagenEvento.visibility = View.VISIBLE
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
        holder.vistaPrincipal.foregroundGravity = Gravity.CENTER

    }

    override fun getItemCount(): Int {
        return diasMes.size
    }

    inner class ViewHolderCalendario(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var diaMes: TextView
        var vistaPrincipal: View
        private var imagenEvento: ImageView

        init {
            diaMes = itemView.findViewById(R.id.lbl_celda_dia)
            diaMes.gravity = Gravity.CENTER
            diaMes.textAlignment = View.TEXT_ALIGNMENT_CENTER
            diaMes.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            diaMes.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            diaMes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            vistaPrincipal = itemView.findViewById(R.id.vistaPrincipal)
            imagenEvento = itemView.findViewById(R.id.img_evento)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val fecha = diasMes[bindingAdapterPosition]
            if (fecha != null) {
                listener?.diaSeleccionado(fecha)
            }
        }
    }
}
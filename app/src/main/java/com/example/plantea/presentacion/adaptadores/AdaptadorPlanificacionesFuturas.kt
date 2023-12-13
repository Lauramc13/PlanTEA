package com.example.plantea.presentacion.adaptadores


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.PlanificacionItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdaptadorPlanificacionesFuturas(var list : ArrayList<PlanificacionItem>, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPlanificacionesFuturas.ViewHolderPlanFuturos>() {

    interface OnItemSelectedListener {
        fun diaSeleccionado(fecha:LocalDate)

    }

    inner class ViewHolderPlanFuturos(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title = itemView.findViewById<TextView>(R.id.titleNotification)
        val date = itemView.findViewById<TextView>(R.id.dateNotification)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view:View){
            val position = bindingAdapterPosition
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val fecha = LocalDate.parse(list[position].date, formatter)
            listener?.diaSeleccionado(fecha)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPlanFuturos {
        val items = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list_planificaciones, parent, false)
        return ViewHolderPlanFuturos(items)
    }

    override fun onBindViewHolder(holder: ViewHolderPlanFuturos, position: Int) {
        holder.title.text = list[position].title
        holder.date.text = list[position].date
    }

    override fun getItemCount(): Int {
        return list.size
    }
}



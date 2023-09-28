package com.example.plantea.presentacion.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.PlanificacionItem

class AdaptadorPlanificacionesFuturas(var list : ArrayList<PlanificacionItem>) :
    RecyclerView.Adapter<AdaptadorPlanificacionesFuturas.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val title = itemView.findViewById<TextView>(R.id.titleNotification)
        val date = itemView.findViewById<TextView>(R.id.dateNotification)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorPlanificacionesFuturas.ViewHolder {
        val items = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list_planificaciones, parent, false)
        return ViewHolder(items)
    }

    override fun onBindViewHolder(holder: AdaptadorPlanificacionesFuturas.ViewHolder, position: Int) {
        holder.title.text = list[position].title
        holder.date.text = list[position].date.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }


}



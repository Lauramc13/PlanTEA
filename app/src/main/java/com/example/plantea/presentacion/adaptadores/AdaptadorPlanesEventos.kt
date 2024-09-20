package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Planificacion
import com.google.android.material.card.MaterialCardView

class AdaptadorPlanesEventos(private var planes: ArrayList<Planificacion>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPlanesEventos.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    interface OnItemSelectedListener {
        fun planSeleccionado(posicion: Int, context: Context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_planificacion, null, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titulo.text = planes!![position].getTitulo()
        val currentNightMode = holder.itemView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        if (position == selectedPosition) {
            holder.card.setCardBackgroundColor(
                if (isDarkMode) {
                    Color.rgb(0, 102, 102)
                } else {
                    Color.rgb(193, 235, 235)
                }
            )
        } else {
            holder.card.setCardBackgroundColor(
                if (isDarkMode) {
                    Color.rgb(37, 47, 56)
                } else {
                    Color.WHITE
                }
            )
        }

    }

    override fun getItemCount(): Int {
        return planes!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView
        var card: MaterialCardView

        init {
            titulo = itemView.findViewById(R.id.lbl_Planificacion)
            card = itemView.findViewById(R.id.card_plan)

            itemView.setOnClickListener{
                val posicion = bindingAdapterPosition
                listener?.planSeleccionado(posicion , itemView.context)
                setItemSelected(posicion)
            }
        }
    }

    fun setItemSelected(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(position)
    }
}
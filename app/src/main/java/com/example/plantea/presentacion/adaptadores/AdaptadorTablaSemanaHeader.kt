package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class AdaptadorTablaSemanaHeader(var listaDiaSemana: Array<String>, private var configuracion: Int) : RecyclerView.Adapter<AdaptadorTablaSemanaHeader.ViewHolderItemSemanaHeader>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItemSemanaHeader {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table_recycler_header, null, false)
        return ViewHolderItemSemanaHeader(view)
    }

    override fun onBindViewHolder(holder: ViewHolderItemSemanaHeader, position: Int) {
        context = holder.itemView.context

        if(configuracion == 3){
            holder.imagen.visibility = View.VISIBLE
            holder.text.visibility = View.GONE
            holder.imagen.setImageDrawable(getImagesWeek(position))
        }else{
            holder.imagen.visibility = View.GONE
            holder.text.visibility = View.VISIBLE
            holder.text.text = listaDiaSemana[position]
        }

       if(position == LocalDate.now().dayOfWeek.value-1){
            holder.itemView.setBackgroundResource(R.drawable.border_week_today_header)
       }else{
            holder.itemView.setBackgroundResource(R.drawable.border_week)
       }

    }


    fun updateList(newList: Array<String>, newConfig: Int) {
        listaDiaSemana = newList
        configuracion = newConfig
        notifyDataSetChanged()
    }

    fun getImagesWeek(position: Int): Drawable {
        //for each day of the week, get the image semana_lunes, semana_martes, etc

        val drawableId = when (position) {
            0 -> R.drawable.semana_lunes
            1 -> R.drawable.semana_martes
            2 -> R.drawable.semana_miercoles
            3 -> R.drawable.semana_jueves
            4 -> R.drawable.semana_viernes
            5 -> R.drawable.semana_sabado
            6 -> R.drawable.semana_domingo
            else -> throw IllegalArgumentException("Invalid position: $position")
        }

        // Retrieve and return the drawable from resources
        return ContextCompat.getDrawable(context, drawableId) ?: throw IllegalArgumentException("Drawable not found")
    }


    override fun getItemCount(): Int {
        return listaDiaSemana.size
    }

    inner class ViewHolderItemSemanaHeader(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imagen: ImageView
        var text : TextView

        init {
            imagen = itemView.findViewById<View>(R.id.TextImage) as ImageView
            text = itemView.findViewById<View>(R.id.Text) as TextView
        }

    }
}
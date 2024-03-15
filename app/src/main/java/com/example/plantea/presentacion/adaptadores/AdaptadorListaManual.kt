package com.example.plantea.presentacion.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Evento
import java.time.LocalDate

class AdaptadorListaManual(private val lista: List<String>) : RecyclerView.Adapter<AdaptadorListaManual.ViewHolderManual>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderManual {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list_manual, parent, false)
        return ViewHolderManual(view)
    }

    override fun onBindViewHolder(holder: ViewHolderManual, position: Int) {
        holder.numero.text = (position + 1).toString() + "."
        holder.titulo.text = lista[position]
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun getTotalHeightRecyclerView(recyclerView: RecyclerView): Int {
        val adapter = recyclerView.adapter
        val itemCount = adapter!!.itemCount
        val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(0))
        var height = 0
        for (i in 0 until itemCount) {
            adapter.onBindViewHolder(holder, i)
            holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            height += holder.itemView.measuredHeight
        }
        return height
    }

    class ViewHolderManual(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numero: TextView = itemView.findViewById(R.id.item_number)
        val titulo: TextView = itemView.findViewById(R.id.content)
    }

}
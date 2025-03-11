package com.example.plantea.presentacion.adaptadores

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R

class AdaptadorListaManual(private val lista: List<String>) : RecyclerView.Adapter<AdaptadorListaManual.ViewHolderManual>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderManual {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list_manual, parent, false)
        return ViewHolderManual(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderManual, position: Int) {
        holder.numero.text = (position + 1).toString() + "."
        holder.titulo.text = Html.fromHtml(lista[position], Html.FROM_HTML_MODE_COMPACT)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    class ViewHolderManual(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numero: TextView = itemView.findViewById(R.id.item_number)
        val titulo: TextView = itemView.findViewById(R.id.content)
    }
}
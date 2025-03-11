package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionPictogramas
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import java.util.Locale


class AdaptadorPictogramasPlan(var listaPictogramas: ArrayList<Pictograma>?) : RecyclerView.Adapter<AdaptadorPictogramasPlan.ViewHolderPictogramas>() {

    lateinit var context: Context
    private var listaPictos = listaPictogramas

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas_presentacion, parent, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context

        holder.titulo.setText(listaPictos!![position].titulo)
        holder.imagen.setImageBitmap(listaPictos!![position].imagen)

        configPicto(holder)
    }

    override fun getItemCount(): Int {
        return listaPictos!!.size
    }

    private fun configPicto(holder: AdaptadorPictogramasPlan.ViewHolderPictogramas){
        val sharedPreferences = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        when (sharedPreferences.getString("configPictogramas", "default")) {
            "default" -> {
                holder.imagen.visibility = View.VISIBLE
                holder.titulo.visibility = View.VISIBLE
            }
            "imagen" -> {
                holder.imagen.visibility = View.VISIBLE
                holder.titulo.visibility = View.GONE
            }
            "texto" -> {
                holder.imagen.visibility = View.GONE
                holder.titulo.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: EditText = itemView.findViewById<View>(R.id.id_Texto) as EditText
        var imagen: ImageView = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
        var card: View = itemView.findViewById(R.id.id_card)!!
    }
}
package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment


class AdaptadorPictogramas(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?, private val favourites: CategoriasPictogramasFragment) : RecyclerView.Adapter<AdaptadorPictogramas.ViewHolderPictogramas>() {

    lateinit var context: Context
    var picto = Pictograma()
    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context

        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))

        if(listaPictogramas!![position].categoria in 1..4){
            holder.card.setBackgroundResource(R.drawable.card_personalizado_categoria)
            holder.imagen.setBackgroundColor(Color.rgb(100, 100, 50))
        }else{
            holder.heart!!.visibility = View.VISIBLE
            if(listaPictogramas!![position].favorito){
                holder.heart!!.setImageResource(R.drawable.svg_heart_filled)
            }
        }

        holder.heart!!.setOnClickListener {
            if(listaPictogramas!![position].favorito){
                holder.heart!!.setImageResource(R.drawable.svg_heart)
                listaPictogramas!![position].favorito = false
                favourites.removeFavorite(listaPictogramas!![position], position)
                //favourites.nuevoPictoFavorito(listaPictogramas!![position])
            }else{
                holder.heart!!.setImageResource(R.drawable.svg_heart_filled)
                listaPictogramas!![position].favorito = true
                favourites.markAsFavorite(listaPictogramas!![position])

                Log.d("asf", listaPictogramas!![position].toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
        var titulo: TextView
        var imagen: ImageView
        var card: View
        var heart: ImageView? = null
        var historia: ImageView

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            card = itemView.findViewById(R.id.id_card) as View
            heart = itemView.findViewById(R.id.btn_favoritosOff) as ImageView
            historia = itemView.findViewById(R.id.btn_historiaPictoOn)
            itemView.setOnClickListener(this)


        }

        override fun onClick(view: View?) {
            val posicion = bindingAdapterPosition
            listener?.onItemSeleccionado(posicion)
        }
    }
}
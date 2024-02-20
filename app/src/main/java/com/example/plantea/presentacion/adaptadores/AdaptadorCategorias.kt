package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Categoria
import com.example.plantea.dominio.Pictograma

class AdaptadorCategorias(var listaCategorias: ArrayList<Categoria>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorCategorias.ViewHolderCategorias>() {
    lateinit var context: Context

    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int, context: Context)
        fun addCategoria(view: View?)

        fun borrarCategoria(posicion: Int, categoria: Int, view: View?)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCategorias {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categorias, null, false)
        return ViewHolderCategorias(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCategorias, position: Int) {
        context = holder.itemView.context
        holder.titulo.text = listaCategorias!![position].titulo

        if(position == listaCategorias!!.size-1){
            holder.imagen.setImageResource(R.drawable.svg_add)
            val drawable = ContextCompat.getDrawable(context, R.drawable.card_dotted_categorias)
            holder.card.background = drawable
            holder.borrar.visibility = View.GONE
        }else{
            holder.imagen.setImageURI(Uri.parse(listaCategorias!![position].imagen))
        }
        if(position in 0..9){
            holder.borrar.visibility = View.GONE
        }

        if(listaCategorias!![position].color == "default") {
            // if its dark mode
            if(context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES){
                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_dark_background2))
            }else{
                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_surfaceVariant))
            }
        }else{
            val color = getColor(listaCategorias!![position].color!!)
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, color))
            holder.titulo.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onSurface))

        }
    }

    private fun getColor(color: String): Int {
        return when(color) {
            "blue" -> R.color.blueCategoria
            "green" -> R.color.greenCategoria
            "purple" -> R.color.purpleCategoria
            "pink" -> R.color.pinkCategoria
            "yellow" -> R.color.yellowCategoria
            else -> R.color.md_theme_light_surfaceVariant
        }
    }

    override fun getItemCount(): Int {
        return listaCategorias!!.size
    }

    inner class ViewHolderCategorias(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var titulo: TextView
        var imagen: ImageView
        var card: CardView
        var borrar: ImageView

        init {
            titulo = itemView.findViewById<View>(R.id.lbl_titulo) as TextView
            imagen = itemView.findViewById<View>(R.id.img_categoria) as ImageView
            card = itemView.findViewById<View>(R.id.categoria) as CardView
            borrar = itemView.findViewById<View>(R.id.btn_borrarPicto) as ImageView

            itemView.setOnClickListener(this)

            borrar.setOnClickListener{
                val posicion = bindingAdapterPosition
                listener?.borrarCategoria(posicion, listaCategorias!![posicion].categoria, itemView)
            }


        }

        override fun onClick(view: View?) {
            val posicion = bindingAdapterPosition

            if(posicion == listaCategorias!!.size-1){
                listener?.addCategoria(view)
            } else {
                listener?.onItemSeleccionado(listaCategorias!![posicion].categoria, view!!.context)
            }
        }
    }

}
package com.example.plantea.presentacion.adaptadores

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
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
import com.example.plantea.presentacion.actividades.CommonUtils

class AdaptadorCategorias(var listaCategorias: ArrayList<Categoria>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorCategorias.ViewHolderCategorias>() {
    lateinit var context: Context

    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int, context: Context)
        fun addCategoria(view: View?, activity: Activity)
        fun borrarCategoria(posicion: Int, categoria: Int, view: View?)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCategorias {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categorias, null, false)
        return ViewHolderCategorias(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCategorias, position: Int) {
        context = holder.itemView.context
        holder.titulo.text = listaCategorias!![position].getTitulo()

        if(position == listaCategorias!!.size-1){
            holder.imagen.setImageResource(R.drawable.svg_add_20)
            val drawable = ContextCompat.getDrawable(context, R.drawable.card_dotted_categorias)
            holder.card.background = drawable
            holder.borrar.visibility = View.GONE
        }else{
           holder.imagen.setImageBitmap(listaCategorias!![position].getImagen())
        }

        if(listaCategorias!![position].getColor() == "default") {
            // if its dark mode
            if(context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES){
                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_light_onSurfaceVariant))
            }else{
                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_dark_onSurfaceVariant))
            }
        }else{
            val darkColor = getColorDark(listaCategorias!![position].getColor()!!)

            holder.borrar.setColorFilter(ContextCompat.getColor(context, darkColor), PorterDuff.Mode.SRC_IN)
            holder.card.setCardBackgroundColor(CommonUtils.getColor(context, listaCategorias!![position].getColor()!!))
            holder.titulo.setTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onSurface))
        }
    }

    private fun getColorDark(color: String): Int {
        return when(color) {
            "blue" -> R.color.blueCategoriaDark
            "green" -> R.color.greenCategoriaDark
            "purple" -> R.color.purpleCategoriaDark
            "pink" -> R.color.pinkCategoriaDark
            "yellow" -> R.color.yellowCategoriaDark
            else -> R.color.md_theme_light_onSurfaceVariant
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
                listener?.borrarCategoria(posicion, listaCategorias!![posicion].getCategoria(), itemView)
            }
        }

        override fun onClick(view: View?) {
            val posicion = bindingAdapterPosition

            if(posicion == listaCategorias!!.size-1){
                //pass the activity
                listener?.addCategoria(view, view!!.context as Activity)
            } else {
                listener?.onItemSeleccionado(listaCategorias!![posicion].getCategoria(), view!!.context)
            }
        }
    }

}
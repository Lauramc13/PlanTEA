package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Cuaderno
import com.example.plantea.presentacion.fragmentos.cuaderno.PrincipalFragment


class AdaptadorCategoriasCuaderno(var listaPictogramas: ArrayList<Cuaderno>?, private val isPlan: Boolean, private val listener: OnItemSelectedListener?, private val context: Context, private val fragment: PrincipalFragment) : RecyclerView.Adapter<AdaptadorCategoriasCuaderno.ViewHolderPictogramas>() {
    interface OnItemSelectedListener {
        fun pictogramaCuaderno(posicion: Int, idCuaderno: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cuaderno_picto, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        holder.titulo.text = listaPictogramas!![position].titulo
        if(isPlan && (position == listaPictogramas!!.size - 1) ){
            holder.imagen.setImageResource(R.drawable.svg_add)
            val drawable = ContextCompat.getDrawable(context, R.drawable.card_personalizado_cuaderno_dotted)
            holder.borde.background = drawable
            holder.borrar.visibility = View.INVISIBLE
            holder.editar.visibility = View.INVISIBLE
        }else{
            holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))
            val drawable = ContextCompat.getDrawable(context, R.drawable.card_personalizado_cuaderno)
            holder.borde.background = drawable
            holder.borrar.visibility = View.VISIBLE
            holder.editar.visibility = View.VISIBLE
        }

        if ((listaPictogramas!![position].id == 2 || listaPictogramas!![position].id == 3 || listaPictogramas!![position].id == 4) || !isPlan) {
            holder.borrar.visibility = View.INVISIBLE
            holder.editar.visibility = View.INVISIBLE
        }

        holder.borrar.setOnClickListener {
            fragment.eliminarCuaderno(listaPictogramas!![position])
        }

        holder.editar.setOnClickListener {
            fragment.editarCuaderno(listaPictogramas!![position])
        }
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var titulo: TextView
        var imagen: ImageView
        var borde : RelativeLayout
        var borrar: ImageView
        var editar: ImageView

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            borde = itemView.findViewById<View>(R.id.card_categoria) as RelativeLayout
            borrar = itemView.findViewById<View>(R.id.btn_borrarCategoria) as ImageView
            editar = itemView.findViewById<View>(R.id.btn_editCategoria) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val posicion = bindingAdapterPosition
            val idCuaderno = listaPictogramas?.get(posicion)?.id
            if (idCuaderno != null) {
                listener?.pictogramaCuaderno(posicion, idCuaderno)
            }
        }
    }
}
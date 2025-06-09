package com.example.plantea.presentacion.adaptadores

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.presentacion.fragmentos.CategoriasPictogramasFragment


class AdaptadorPictogramas(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?, private val favourites: CategoriasPictogramasFragment) : RecyclerView.Adapter<AdaptadorPictogramas.ViewHolderPictogramas>() {
    lateinit var context: Context

    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int)
        fun onItemBorrar(pictograma: Pictograma)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, parent, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context

        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageBitmap(listaPictogramas!![position].imagen)

        holder.borrar.visibility = View.VISIBLE
        holder.favorito.visibility = View.VISIBLE
        if(listaPictogramas!![position].favorito){
            holder.favorito.setImageResource(R.drawable.svg_star_filled)
        }

        holder.favorito.setOnClickListener {
            if(listaPictogramas!![position].favorito){
                holder.favorito.setImageResource(R.drawable.svg_star)
                listaPictogramas!![position].favorito = false
                favourites.removeFavorite(listaPictogramas!![position])
                if(listaPictogramas!![position].categoria == 5){
                    listaPictogramas!!.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, listaPictogramas!!.size)
                }
            }else{
                holder.favorito.setImageResource(R.drawable.svg_star_filled)
                listaPictogramas!![position].favorito = true
                favourites.markAsFavorite(listaPictogramas!![position])
            }
        }

        configPicto(holder)
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    private fun configPicto(holder: ViewHolderPictogramas){
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
        var titulo: TextView = itemView.findViewById(R.id.id_Texto)
        var imagen: ImageView = itemView.findViewById(R.id.id_Imagen)
        var card: ConstraintLayout = itemView.findViewById(R.id.id_card)!!
        var favorito: ImageView = itemView.findViewById(R.id.btn_favoritos)
        var borrar : ImageView = itemView.findViewById(R.id.btn_borrarPicto)

        init {
            itemView.setOnClickListener {
                val posicion = bindingAdapterPosition
                listener?.onItemSeleccionado(posicion)
            }

            borrar.setOnClickListener {
                //TODO: create dialog to make sure the user wants to delete the pictogram
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.dialogo_eliminar_evento)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialog.findViewById<TextView>(R.id.lbl_title).text = context.getString(R.string.dialog_borrar_pictograma)
                dialog.findViewById<TextView>(R.id.lbl_subtitle).text = context.getString(R.string.dialog_borrar_pictograma_subtitulo, listaPictogramas!![bindingAdapterPosition].titulo)

                val btnCancelar = dialog.findViewById<Button>(R.id.btn_cancelar)
                val btnEliminar = dialog.findViewById<Button>(R.id.btn_eliminar)
                val cerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)

                btnCancelar.setOnClickListener {
                    dialog.dismiss()
                }

                cerrar.setOnClickListener {
                    dialog.dismiss()
                }

                btnEliminar.setOnClickListener {
                    val posicion = bindingAdapterPosition
                    listener?.onItemBorrar(listaPictogramas!![posicion])
                    listaPictogramas!!.removeAt(posicion)
                    notifyItemRemoved(posicion)
                    notifyItemRangeChanged(posicion, listaPictogramas!!.size)
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }
}
package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.CategoriaActividad
import com.google.android.material.button.MaterialButton

class AdaptadorListaCategoriasActividad(var listaCategorias: ArrayList<CategoriaActividad>?, private val listener: OnItemSelectedListener? ) : RecyclerView.Adapter<AdaptadorListaCategoriasActividad.ViewHolderCategoriasActividad>() {
    lateinit var context: Context
    var isEditable = false

    interface OnItemSelectedListener {
        fun borrarCategoria(idCategoria: String?, position: Int)
        fun editarCategoria(idCategoria: String?, nombre: String?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCategoriasActividad {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categorias_actividad, null, false)
        return ViewHolderCategoriasActividad(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCategoriasActividad, position: Int) {
        context = holder.itemView.context
        if(listaCategorias!![position].id == "0"){
            holder.titulo.visibility = View.GONE
            holder.borrar.visibility = View.GONE
            holder.editar.visibility = View.GONE

        }else{
            holder.titulo.setText(listaCategorias!![position].nombre)
        }
    }

    override fun getItemCount(): Int {
        return listaCategorias!!.size
    }

    inner class ViewHolderCategoriasActividad(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo = itemView.findViewById<View>(R.id.textView) as EditText
        var borrar = itemView.findViewById<View>(R.id.btn_delete) as MaterialButton
        var editar = itemView.findViewById<View>(R.id.btn_edit) as MaterialButton

        init {
            borrar.setOnClickListener {
                val posicion = bindingAdapterPosition
                listener?.borrarCategoria(listaCategorias!![posicion].id, posicion)
            }

            editar.setOnClickListener {
                if(!isEditable) {
                    isEditable = true
                    titulo.isEnabled = true
                    titulo.background = ResourcesCompat.getDrawable(context.resources, R.drawable.edittext_underline, null)
                    editar.icon = ResourcesCompat.getDrawable(context.resources, R.drawable.svg_save, null)
                }else{
                    val posicion = bindingAdapterPosition
                    listener?.editarCategoria(listaCategorias!![posicion].id, titulo.text.toString(), posicion)
                    titulo.background = null
                    editar.setIconResource(R.drawable.svg_edit_outline)
                    isEditable = false
                    titulo.isEnabled = false
                }
            }
        }
    }
}
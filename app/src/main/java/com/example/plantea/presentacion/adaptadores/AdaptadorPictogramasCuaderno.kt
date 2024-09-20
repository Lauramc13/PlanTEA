package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma

class AdaptadorPictogramasCuaderno(var listaPictogramas: ArrayList<Pictograma>?, private val isPlan: Boolean, private val listener: OnItemSelectedListener?, private val context: Context) : RecyclerView.Adapter<AdaptadorPictogramasCuaderno.ViewHolderPictogramas>() {

    var isBusqueda: Boolean = false
    var listaPictosAgregados = ArrayList<String>()

    interface OnItemSelectedListener {
        fun pictogramaCuaderno(posicion: Int)
        fun addPicto(pictograma: Pictograma)
        fun removePicto(pictograma: Pictograma, sourceAPI: Boolean, isBusqueda: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictograma_cuaderno, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        holder.titulo.text = listaPictogramas!![position].titulo

        if(isPlan){
            if(isBusqueda){
                mostrarPicto(holder, position)
                holder.borrar.visibility = View.INVISIBLE
                if(listaPictogramas!![position].id in listaPictosAgregados){
                    holder.btnNoAniair.visibility = View.VISIBLE
                    holder.btnAniadir.visibility = View.INVISIBLE
                }else{
                    holder.btnAniadir.visibility = View.VISIBLE
                    holder.btnNoAniair.visibility = View.INVISIBLE
                }
            }else{
                holder.btnAniadir.visibility = View.INVISIBLE
                holder.btnNoAniair.visibility = View.INVISIBLE
                if(position == listaPictogramas!!.size - 1) {
                    mostrarAniadir(holder)
                    holder.borrar.visibility = View.INVISIBLE
                }else{
                    mostrarPicto(holder, position)
                    holder.borrar.visibility = View.VISIBLE
                }
            }
        }else{
            mostrarPicto(holder, position)
            holder.borrar.visibility = View.INVISIBLE
        }

        holder.btnNoAniair.setOnClickListener {
            holder.btnNoAniair.visibility = View.INVISIBLE
            holder.btnAniadir.visibility = View.VISIBLE
            listener?.removePicto(listaPictogramas!![position], true, isBusqueda)
        }

        holder.btnAniadir.setOnClickListener {
            holder.btnNoAniair.visibility = View.VISIBLE
            holder.btnAniadir.visibility = View.INVISIBLE
            listener?.addPicto(listaPictogramas!![position])
        }
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var titulo: TextView
        var imagen: ImageView
        var borde : RelativeLayout
        var btnAniadir: Button
        var btnNoAniair: Button
        var borrar: ImageView


        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            borde = itemView.findViewById<View>(R.id.card_pictograma) as RelativeLayout
            btnAniadir = itemView.findViewById<View>(R.id.btn_sin_aniadir) as Button
            btnNoAniair = itemView.findViewById<View>(R.id.btn_aniadido) as Button
            borrar = itemView.findViewById<View>(R.id.btn_borrarPicto) as ImageView
            itemView.setOnClickListener(this)

            borrar.setOnClickListener {
                val posicion = bindingAdapterPosition
                //val sourceAPI = listaPictogramas!![posicion].sourceAPI
              //  listener?.removePicto(listaPictogramas!![posicion], sourceAPI, false)

                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listaPictogramas!!.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

        }

        override fun onClick(view: View) {
            val posicion = bindingAdapterPosition
            listener?.pictogramaCuaderno(posicion)
        }
    }

    private fun mostrarAniadir(holder : ViewHolderPictogramas){
        holder.imagen.setImageResource(R.drawable.svg_add)
        val drawable = ContextCompat.getDrawable(context, R.drawable.card_personalizado_cuaderno_dotted)
        holder.borde.background = drawable
    }

    private fun mostrarPicto(holder : ViewHolderPictogramas, position: Int){
       /* val identifier = context.resources.getIdentifier(listaPictogramas!![position].imagen, "drawable", context.packageName)
        if(identifier == 0) {
            holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))
        }else{
            holder.imagen.setImageResource(identifier)
        }*/
        //holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))
        val drawable = ContextCompat.getDrawable(context, R.drawable.card_personalizado_cuaderno)
        holder.borde.background = drawable
    }
}
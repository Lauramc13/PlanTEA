package com.example.plantea.presentacion.adaptadores

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.DiaSemana
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate

class AdaptadorTablaSemana(var listaDiaSemana: ArrayList<DiaSemana>?, var isEdit: Boolean, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorTablaSemana.ViewHolderItemSemana>() {

    lateinit var context: Context
    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int, activity: Activity?)
        fun onBorrarItemSeleccionado(posicion: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItemSemana {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table_recycler_view, parent, false)
        return ViewHolderItemSemana(view)
    }

    override fun onBindViewHolder(holder: ViewHolderItemSemana, position: Int) {
        context = holder.itemView.context
        if(listaDiaSemana != null){
            if(listaDiaSemana?.get(position)?.imagen != null){

                holder.imagen.setImageBitmap(listaDiaSemana!![position].imagen)
            }else{
                holder.imagen.setImageBitmap(null)
            }
        }

        if(position == LocalDate.now().dayOfWeek.value-1){
            holder.itemView.setBackgroundResource(R.drawable.border_week_today)
        }else{
            holder.itemView.setBackgroundResource(R.drawable.border_week)
        }

        changeImageClick(holder, position)
    }

    override fun getItemCount(): Int {
        return 7
    }

    private fun changeImageClick(holder: ViewHolderItemSemana, position: Int){
        val imagen = listaDiaSemana?.get(position)?.imagen

        holder.borrar.visibility = if (isEdit && imagen != null) View.VISIBLE else View.GONE

        if (imagen == null) {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            if(isEdit) {
                holder.imagen.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.svg_add_image))
            }
        }
    }

    fun changeImage(posicion: Int, bitmap: Bitmap?){
        listaDiaSemana?.get(posicion)?.imagen = bitmap
        notifyItemChanged(posicion)
    }

    inner class ViewHolderItemSemana(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagen: ImageView
        var borrar: MaterialButton
        var card: MaterialCardView

        init {
            imagen = itemView.findViewById(R.id.image)
            borrar = itemView.findViewById(R.id.btnTrash)
            card = itemView.findViewById(R.id.card_imagen)

            borrar.setOnClickListener {
                listener?.onBorrarItemSeleccionado(bindingAdapterPosition)
            }

            imagen.setOnClickListener {
                if (isEdit) {
                    val posicion = bindingAdapterPosition
                    val activity = context as Activity
                    listener?.onItemSeleccionado(posicion, activity)
                }
            }
        }

    }
}
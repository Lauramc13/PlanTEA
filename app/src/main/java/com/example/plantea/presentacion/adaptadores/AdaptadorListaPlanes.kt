package com.example.plantea.presentacion.adaptadores

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils

class AdaptadorListaPlanes(private var planes: ArrayList<Planificacion>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorListaPlanes.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    private var previousSelectedPosition = RecyclerView.NO_POSITION
    private var cardOpened = -1

    interface OnItemSelectedListener {
        fun deleteClick(posicion: Int, context: Context)
        fun editClick(posicion: Int, context: Context)
        fun duplicateClick(posicion: Int, context: Context)
        fun downloadPDFClick(posicion: Int, context: Context)
        fun planSeleccionado(posicion: Int, recyclerView: RecyclerView, context: Context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_planificaciones, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titulo.text = planes!![position].getTitulo()
        changeHeight(holder, position)
        if (selectedPosition == position) {
            holder.recyclerView.visibility = View.VISIBLE
            listener?.planSeleccionado(position, holder.recyclerView, holder.itemView.context)
        } else {
            holder.recyclerView.visibility = View.GONE
        }
    }

    private fun changeHeight(holder: ViewHolder, position: Int) {
        val layoutParams = holder.card.layoutParams
        val newHeight = calculateHeight(position, holder.itemView.context)
        layoutParams.height = newHeight
        holder.card.layoutParams = layoutParams
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val resources = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    private fun calculateHeight(posicion: Int, context: Context): Int {
        return when (posicion) {
            selectedPosition -> {
                if (CommonUtils.isMobile(context)) dpToPx(180, context) else dpToPx(230, context)
            }
            else -> {
                if (CommonUtils.isMobile(context)) dpToPx(45, context) else dpToPx(50, context)
            }
        }
    }

    override fun getItemCount(): Int {
        return planes!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView = itemView.findViewById(R.id.lbl_Planificacion)
        private var eliminar: ImageView = itemView.findViewById(R.id.icon_delete)
        private var editar: ImageView = itemView.findViewById(R.id.icon_edit)
        private var duplicar: ImageView = itemView.findViewById(R.id.icon_copy)
        private var downloadPDF: ImageView = itemView.findViewById(R.id.icon_downloadPDF)
        var card: CardView = itemView.findViewById(R.id.card_plan)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.items_planificacion)

        init {
            editar.setOnClickListener {
                listener?.editClick(bindingAdapterPosition, itemView.context)
                notifyItemChanged(bindingAdapterPosition)
            }
            eliminar.setOnClickListener {
                listener?.deleteClick(bindingAdapterPosition, itemView.context)
            }
            duplicar.setOnClickListener {
                listener?.duplicateClick(bindingAdapterPosition, itemView.context)
            }
            downloadPDF.setOnClickListener {
                listener?.downloadPDFClick(bindingAdapterPosition, itemView.context)
            }

            card.setOnClickListener{
                if(cardOpened == bindingAdapterPosition){
                    cardOpened = -1
                    notifyItemChanged(selectedPosition)
                    selectedPosition = RecyclerView.NO_POSITION
                    previousSelectedPosition = RecyclerView.NO_POSITION
                } else {
                    cardOpened = bindingAdapterPosition
                    previousSelectedPosition = selectedPosition
                    selectedPosition = bindingAdapterPosition
                }

                changeHeight(this, previousSelectedPosition)
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(bindingAdapterPosition)
            }
        }
    }

}
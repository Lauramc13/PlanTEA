package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.objetos.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.button.MaterialButton

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
        holder.titulo.text = planes!![position].titulo
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
        val layoutParamsItem = holder.recyclerView.layoutParams
        val newHeight = calculateHeight(layoutParamsItem.height, position, holder.itemView.context)
        layoutParams.height = newHeight
        holder.card.layoutParams = layoutParams
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val resources = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    private fun calculateHeight(itemHeight: Int, posicion: Int, context: Context): Int {
        return when (posicion) {
            selectedPosition -> {
                if (CommonUtils.isMobile(context)) dpToPx(itemHeight, context) else dpToPx(itemHeight, context)
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
        private var eliminar: MaterialButton? = itemView.findViewById(R.id.icon_delete)
        private var editar: MaterialButton? = itemView.findViewById(R.id.icon_edit)
        private var duplicar: MaterialButton? = itemView.findViewById(R.id.icon_copy)
        private var downloadPDF: MaterialButton? = itemView.findViewById(R.id.icon_downloadPDF)
        private var options: MaterialButton? = itemView.findViewById(R.id.icon_options)
        var card: CardView = itemView.findViewById(R.id.card_plan)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.items_planificacion)

        init {
            editar?.setOnClickListener {
                listener?.editClick(bindingAdapterPosition, itemView.context)
                notifyItemChanged(bindingAdapterPosition)
            }
            eliminar?.setOnClickListener {
                listener?.deleteClick(bindingAdapterPosition, itemView.context)
            }
            duplicar?.setOnClickListener {
                listener?.duplicateClick(bindingAdapterPosition, itemView.context)
            }
            downloadPDF?.setOnClickListener {
                listener?.downloadPDFClick(bindingAdapterPosition, itemView.context)
            }

            options?.setOnClickListener {
                // show menu with export and visibility options
                val inflater = itemView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_menu_planificacion, null)
                val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
                popupWindow.showAsDropDown(options, -20, 0)

                downloadPDF = popupView.findViewById(R.id.icon_downloadPDF)
                duplicar = popupView.findViewById(R.id.icon_copy)
                editar = popupView.findViewById(R.id.icon_edit)
                eliminar = popupView.findViewById(R.id.icon_delete)

                editar?.setOnClickListener {
                    listener?.editClick(bindingAdapterPosition, itemView.context)
                    notifyItemChanged(bindingAdapterPosition)
                    popupWindow.dismiss()
                }
                eliminar?.setOnClickListener {
                    listener?.deleteClick(bindingAdapterPosition, itemView.context)
                    popupWindow.dismiss()
                }
                duplicar?.setOnClickListener {
                    listener?.duplicateClick(bindingAdapterPosition, itemView.context)
                    popupWindow.dismiss()
                }
                downloadPDF?.setOnClickListener {
                    listener?.downloadPDFClick(bindingAdapterPosition, itemView.context)
                    popupWindow.dismiss()
                }
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
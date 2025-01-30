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
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.CalendarioUtilidades
import com.example.plantea.dominio.Evento
import com.example.plantea.dominio.Planificacion
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.button.MaterialButton
import java.time.LocalDate

class AdaptadorListaEventos(private var planes: ArrayList<Evento>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorListaEventos.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    private var previousSelectedPosition = RecyclerView.NO_POSITION
    private var cardOpened = -1

    interface OnItemSelectedListener {
        fun eventoSeleccionado(posicion: Int, recyclerView: RecyclerView, context: Context)
        fun eventoEditado(posicion: Int, context: Context)
        fun verEvento(posicion: Int, context: Context)
        fun cambiarVisibilidadEvento(posicion: Int, context: Context)
        fun exportarEvento(posicion: Int, context: Context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_eventos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titulo.text = planes!![position].nombre
        holder.fecha.text = CalendarioUtilidades.formatoFechaEvento(planes!![position].fecha!!)

        holder.fecha.visibility = View.VISIBLE
        holder.separator.visibility = View.VISIBLE

        if(planes!![position].visible == 1){
            holder.visibility?.setIconResource(R.drawable.svg_eye_on)
        } else {
            holder.visibility?.setIconResource(R.drawable.svg_eye_off)
            holder.visibility?.iconTint = holder.itemView.context.getColorStateList(R.color.red)
        }

        changeHeight(holder, position)
        if (selectedPosition == position) {
            holder.recyclerView.visibility = View.VISIBLE
            listener?.eventoSeleccionado(position, holder.recyclerView, holder.itemView.context)
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
                if (CommonUtils.isMobile(context)) dpToPx(170, context) else dpToPx(220, context)
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
        val fecha: TextView = itemView.findViewById(R.id.lbl_Fecha)
        val separator : TextView = itemView.findViewById(R.id.separator)
        var card: CardView = itemView.findViewById(R.id.card_plan)
        var edit : MaterialButton? = itemView.findViewById(R.id.icon_edit)
        var ver : MaterialButton? = itemView.findViewById(R.id.icon_eye)
        var visibility : MaterialButton? = itemView.findViewById(R.id.icon_visibility)
        var export: MaterialButton? = itemView.findViewById(R.id.icon_export)
        var options : MaterialButton? = itemView.findViewById(R.id.icon_options)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.items_planificacion)

        init {
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

            options?.setOnClickListener {
                // show menu with export and visibility options
                val inflater = itemView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_menu_evento, null)
                val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
                popupWindow.showAsDropDown(options, -20, 0)

                edit = popupView.findViewById(R.id.item_editar)
                ver = popupView.findViewById(R.id.item_ver)
                visibility =  popupView.findViewById(R.id.item_visibilidad)
                export = popupView.findViewById(R.id.item_export)

                //if evento is visible
                if (planes!![bindingAdapterPosition].visible == 1){
                    visibility?.setIconResource(R.drawable.svg_eye_filled)

                } else{
                    visibility?.setIconResource(R.drawable.svg_eye_off)
                    visibility?.iconTint = itemView.context.getColorStateList(R.color.red)
                }

                edit?.setOnClickListener {
                    listener?.eventoEditado(bindingAdapterPosition, itemView.context)
                    popupWindow.dismiss()
                }

                ver?.setOnClickListener {
                    listener?.verEvento(bindingAdapterPosition, itemView.context)
                    popupWindow.dismiss()
                }

                visibility?.setOnClickListener {
                    listener?.cambiarVisibilidadEvento(bindingAdapterPosition, itemView.context)
                    popupWindow.dismiss()
                }

                export?.setOnClickListener {
                    listener?.exportarEvento(bindingAdapterPosition, itemView.context)
                    popupWindow.dismiss()
                }
            }

            edit?.setOnClickListener {
                listener?.eventoEditado(bindingAdapterPosition, itemView.context)
            }

            ver?.setOnClickListener {
                listener?.verEvento(bindingAdapterPosition, itemView.context)
            }

            visibility?.setOnClickListener {
                listener?.cambiarVisibilidadEvento(bindingAdapterPosition, itemView.context)
            }

            export?.setOnClickListener {
                listener?.exportarEvento(bindingAdapterPosition, itemView.context)
            }
        }
    }

}
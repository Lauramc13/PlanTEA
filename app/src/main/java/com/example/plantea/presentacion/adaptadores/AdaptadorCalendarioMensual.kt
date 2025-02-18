package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.DiaMes
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import java.time.LocalDate

class AdaptadorCalendarioMensual(private val diasMes: ArrayList<LocalDate?>, days: Array<String>, var fechas: ArrayList<DiaMes>, private val listener: OnItemSelectedListener?, private val forPDF: Boolean) : RecyclerView.Adapter<AdaptadorCalendarioMensual.ViewHolderCalendarioMensual>() {

    private val daysOfWeek = days
    var fecha: LocalDate? = LocalDate.now()
    private val VIEW_TYPE_DAY_OF_MONTH = 1
    private val VIEW_TYPE_DAY_OF_WEEK = 2

    interface OnItemSelectedListener {
        fun diaSeleccionado(context: Context?,position: Int)
        fun nuevaFecha(fecha: LocalDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalendarioMensual {
        val inflater = LayoutInflater.from(parent.context)
        val view = if(forPDF){
             inflater.inflate(R.layout.calendario_mensual_celda_pdf, parent, false)
        }else{
            inflater.inflate(R.layout.calendario_mensual_celda, parent, false)
        }

        return ViewHolderCalendarioMensual(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCalendarioMensual, position: Int) {
        holder.setIsRecyclable(false)
        val context = holder.itemView.context

        if(forPDF){
            isPDF(holder, position, context)
        }else{
            isApp(holder, position, context)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderCalendarioMensual, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        val context = holder.itemView.context
        if(payloads.contains("update")){
            isApp(holder, position, context)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < daysOfWeek.size) {
            // View type for days of the week
            VIEW_TYPE_DAY_OF_WEEK
        } else {
            // View type for days of the month
            VIEW_TYPE_DAY_OF_MONTH
        }
    }

    override fun getItemCount(): Int {
        return diasMes.size + daysOfWeek.size
    }

    private fun isPDF(holder: ViewHolderCalendarioMensual, position: Int, context: Context){
        if (position < daysOfWeek.size) {
            holder.diaMes.text = daysOfWeek[position]
            holder.vistaPrincipal.background = null
            holder.vistaPrincipal.strokeWidth = 0
        } else {
            holder.vistaPrincipal.shapeAppearanceModel = cornerRadius(holder, position)

            val fecha = diasMes[position - daysOfWeek.size]
            if (fecha != null) {
                holder.diaMes.text = fecha.dayOfMonth.toString()
                holder.vistaPrincipal.setCardBackgroundColor(Color.parseColor("#F4F6F7"))

                // Check if the date matches one in `fechas`
                val matchingFecha = fechas.firstOrNull { it.fecha == fecha }

                matchingFecha?.let { match ->
                    holder.vistaPrincipal.setCardBackgroundColor(CommonUtils.getColor(context, match.color))
                    if (match.imagen != null) {
                        holder.imagenDia.visibility = View.VISIBLE
                        holder.imagenDia.setImageBitmap(match.imagen)
                        holder.diaMes.text = ""
                    } else {
                        holder.imagenDia.visibility = View.GONE
                    }
                }
            } else {
                if(forPDF){
                    val color = Color.parseColor("#DDE3E5")
                    holder.vistaPrincipal.setCardBackgroundColor(color)
                }
            }
        }
    }

    private fun cornerRadius(holder: ViewHolderCalendarioMensual, position: Int): ShapeAppearanceModel {
        val shapeAppearanceModel = when (position) {
            daysOfWeek.size -> {
                holder.vistaPrincipal.shapeAppearanceModel
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, 30f)
                    .build()
            }
            diasMes.size + daysOfWeek.size - 1 -> {
                holder.vistaPrincipal.shapeAppearanceModel
                    .toBuilder()
                    .setBottomRightCorner(CornerFamily.ROUNDED, 30f)
                    .build()
            }
            daysOfWeek.size *2-1 -> {
                holder.vistaPrincipal.shapeAppearanceModel
                    .toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, 30f)
                    .build()
            }
            diasMes.size  -> {
                holder.vistaPrincipal.shapeAppearanceModel
                    .toBuilder()
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 30f)
                    .build()
            }
            else -> {
                holder.vistaPrincipal.shapeAppearanceModel
            }
        }
        return shapeAppearanceModel
    }

    private fun isApp(holder: ViewHolderCalendarioMensual, position: Int, context: Context){
        val isNight = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        if (position < daysOfWeek.size) {
            holder.diaMes.text = daysOfWeek[position]
            holder.vistaPrincipal.isClickable = false
        } else {
            val fecha = diasMes[position - daysOfWeek.size]
            if (fecha != null) {
                holder.diaMes.text = fecha.dayOfMonth.toString()
                holder.imagenDia.visibility = View.GONE

                // Default background color
                val defaultColor = if (isNight) {
                    CommonUtils.getColorDark(context, "gray")
                } else {
                    CommonUtils.getColor(context, "gray")
                }
                holder.vistaPrincipal.setCardBackgroundColor(defaultColor)

                // Check if the date matches one in `fechas`
                val matchingFecha = fechas.firstOrNull { it.fecha == fecha }

                matchingFecha?.let { match ->
                    val color = if (isNight) {
                        CommonUtils.getColorDark(context, match.color)
                    } else {
                        CommonUtils.getColor(context, match.color)
                    }
                    holder.vistaPrincipal.setCardBackgroundColor(color)
                    if (match.imagen != null) {
                        holder.imagenDia.visibility = View.VISIBLE
                        holder.imagenDia.setImageBitmap(match.imagen)
                        holder.diaMes.text = ""
                    } else {
                        holder.imagenDia.visibility = View.GONE
                    }
                }
            } else {
                holder.vistaPrincipal.setBackgroundResource(R.drawable.round_bg_none2)
            }
        }
    }

    inner class ViewHolderCalendarioMensual(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var diaMes: TextView = itemView.findViewById(R.id.lbl_celda_dia)
        var imagenDia: ImageView = itemView.findViewById(R.id.img_celda_dia)
        var vistaPrincipal: MaterialCardView = itemView.findViewById(R.id.vistaPrincipal)

        init {
            vistaPrincipal.setOnClickListener {
                val fecha = diasMes[bindingAdapterPosition - daysOfWeek.size]
                fecha?.let { selectedFecha ->
                    val position = fechas.indexOfFirst { it.fecha == selectedFecha }
                    if (position != -1) {
                        listener?.diaSeleccionado(itemView.context, position)
                    }else{
                        listener?.nuevaFecha(fecha)

                    }
                }
            }
        }
    }
}
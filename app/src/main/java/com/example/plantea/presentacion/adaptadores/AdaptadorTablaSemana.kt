package com.example.plantea.presentacion.adaptadores

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.DiaSemana
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

class AdaptadorTablaSemana(var listaDiaSemana: ArrayList<DiaSemana>?, var isEdit: Boolean, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorTablaSemana.ViewHolderItemSemana>() {

    lateinit var context: Context
    var currentPopupWindow: PopupWindow? = null
    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int, activity: Activity?)
        fun onBorrarItemSeleccionado(posicion: Int)
        fun onColorSelected(posicion: Int, color: String?, activity: Activity)
        fun onDiaClicked(posicion: Int, activity: Activity)

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
                holder.imagen.setBackgroundColor(Color.WHITE)
            }else{
                holder.imagen.setImageBitmap(null)
                holder.imagen.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        if(position == LocalDate.now().dayOfWeek.value-1){
            holder.itemView.setBackgroundResource(R.drawable.border_week_today)
        }else{
            holder.itemView.setBackgroundResource(R.drawable.border_week)
        }

        val color = if(context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
            getColorDark(context, listaDiaSemana?.get(position)?.color)
        }else{
             getColor(context, listaDiaSemana?.get(position)?.color)
        }

        val drawable = holder.itemView.background as LayerDrawable
        val shape = drawable.findDrawableByLayerId(R.id.background) as GradientDrawable
        shape.setColor(color)

        changeImageClick(holder, position)
    }

    override fun getItemCount(): Int {
        return 7
    }

    private fun changeImageClick(holder: ViewHolderItemSemana, position: Int){
        val imagen = listaDiaSemana?.get(position)?.imagen

        holder.borrar.visibility = if (isEdit && imagen != null) View.VISIBLE else View.GONE
        holder.colors.visibility = if (isEdit) View.VISIBLE else View.GONE

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

    fun changeColor(posicion: Int, color: String?){
        listaDiaSemana?.get(posicion)?.color = color
        notifyItemChanged(posicion)
    }

    private fun getColor(context: Context, color: String?): Int {
        return when(color) {
            "red" -> ContextCompat.getColor(context, R.color.redCategoria)
            "orange" -> ContextCompat.getColor(context, R.color.orangeCategoria)
            "yellow" -> ContextCompat.getColor(context, R.color.yellowCategoria)
            "green" -> ContextCompat.getColor(context, R.color.greenCategoria)
            "blue" -> ContextCompat.getColor(context, R.color.blueCategoria)
            "purple" -> ContextCompat.getColor(context, R.color.purpleCategoria)
            "pink" -> ContextCompat.getColor(context, R.color.pinkCategoria)
            else ->  ContextCompat.getColor(context, R.color.white)
        }
    }

    private fun getColorDark(context: Context, color: String?): Int {
        return when(color) {
            "red" -> ContextCompat.getColor(context, R.color.DarkredCategoria)
            "orange" -> ContextCompat.getColor(context, R.color.DarkorangeCategoria)
            "yellow" -> ContextCompat.getColor(context, R.color.DarkyellowCategoria)
            "green" -> ContextCompat.getColor(context, R.color.DarkgreenCategoria)
            "blue" -> ContextCompat.getColor(context, R.color.DarkblueCategoria)
            "purple" -> ContextCompat.getColor(context, R.color.DarkpurpleCategoria)
            "pink" -> ContextCompat.getColor(context, R.color.DarkpinkCategoria)
            else ->  ContextCompat.getColor(context, R.color.md_theme_dark_background2)
        }
    }

    inner class ViewHolderItemSemana(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagen: ImageView
        var borrar: MaterialButton
        var card: MaterialCardView
        var colors: MaterialButton

        init {
            imagen = itemView.findViewById(R.id.image)
            borrar = itemView.findViewById(R.id.btnTrash)
            card = itemView.findViewById(R.id.card_imagen)
            colors = itemView.findViewById(R.id.btnColors)

            borrar.setOnClickListener {
                listener?.onBorrarItemSeleccionado(bindingAdapterPosition)
            }

            imagen.setOnClickListener {
                if (isEdit) {
                    val posicion = bindingAdapterPosition
                    val activity = context as Activity
                    listener?.onItemSeleccionado(posicion, activity)
                }else{
                    if(listaDiaSemana?.get(bindingAdapterPosition)?.imagen != null)
                        listener?.onDiaClicked(bindingAdapterPosition, context as Activity)
                }
            }

            colors.setOnClickListener {
                currentPopupWindow?.dismiss()
                val popupWindow = PopupWindow(context)
                val layout = LayoutInflater.from(context).inflate(R.layout.menu_horizontal_colors, null)

                layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val popupWidth = layout.measuredWidth

                popupWindow.contentView = layout
                popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                popupWindow.showAsDropDown(itemView, (itemView.width - popupWidth) / 2, 0)
                currentPopupWindow = popupWindow

                layout.findViewById<FloatingActionButton>(R.id.fab1).setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, "red", context as Activity)
                    popupWindow.dismiss()
                }

                layout.findViewById<FloatingActionButton>(R.id.fab2).setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, "orange", context as Activity)
                    popupWindow.dismiss()
                }

                layout.findViewById<FloatingActionButton>(R.id.fab3).setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, "yellow", context as Activity)
                    popupWindow.dismiss()
                }

                layout.findViewById<FloatingActionButton>(R.id.fab4).setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, "green", context as Activity)
                    popupWindow.dismiss()
                }

                layout.findViewById<FloatingActionButton>(R.id.fab5).setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, "blue", context as Activity)
                    popupWindow.dismiss()
                }

                layout.findViewById<FloatingActionButton>(R.id.fab6).setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, "pink", context as Activity)
                    popupWindow.dismiss()
                }

                layout.findViewById<FloatingActionButton>(R.id.fab7).setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, "purple", context as Activity)
                    popupWindow.dismiss()
                }


                layout.findViewById<FloatingActionButton>(R.id.fab8).setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, null, context as Activity)
                    popupWindow.dismiss()
                }

            }

        }

    }


}
package com.example.plantea.presentacion.adaptadores

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
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
    private lateinit var popupWindowMenu: PopupWindow


    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int, activity: Activity?)
        fun onBorrarItemSeleccionado(posicion: Int)
        fun onColorSelected(posicion: Int, color: String?, activity: Activity)
        fun onDiaClicked(posicion: Int, activity: Activity)
        fun onAsociarEvento(posicion: Int, activity: Activity)
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
            CommonUtils.getColorDark(context, listaDiaSemana?.get(position)?.color)
        }else{
            CommonUtils.getColor(context, listaDiaSemana?.get(position)?.color)
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

        holder.colors.visibility = if (isEdit) View.VISIBLE else View.GONE
        holder.menu.visibility = if (isEdit && imagen != null) View.VISIBLE else View.GONE

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



    inner class ViewHolderItemSemana(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagen: ImageView
        var menu: MaterialButton
        var card: MaterialCardView
        var colors: MaterialButton

        init {
            imagen = itemView.findViewById(R.id.image)
            card = itemView.findViewById(R.id.card_imagen)
            colors = itemView.findViewById(R.id.btnColors)
            menu = itemView.findViewById(R.id.btnMenu)

            menu.setOnClickListener {
                //create popup
                ObjectAnimator.ofFloat(menu, "rotation", menu.rotation + 45f).apply {
                    duration = 300 // Animation duration in milliseconds
                    start()
                }
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_menu_semana, null)

                popupWindowMenu = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
                popupWindowMenu.showAsDropDown(menu, -5, 0)

                popupWindowMenu.setOnDismissListener {
                    // Rotate FAB back to 0 degrees when the popup is dismissed
                    ObjectAnimator.ofFloat(menu, "rotation", 0f).apply {
                        duration = 300
                        start()
                    }
                }

                val btnLink = popupView.findViewById<MaterialButton>(R.id.item_link)
                val btnBorrar =  popupView.findViewById<MaterialButton>(R.id.item_borrar)

                if(listaDiaSemana?.get(bindingAdapterPosition)?.idEvento != null && listaDiaSemana?.get(bindingAdapterPosition)?.idEvento != ""){
                    btnLink.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer))
                    btnLink.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_dark_onPrimary))
                }

                btnLink.setOnClickListener {
                    listener?.onAsociarEvento(bindingAdapterPosition, context as Activity)
                    popupWindowMenu.dismiss()
                }

                btnBorrar.setOnClickListener {
                    listener?.onBorrarItemSeleccionado(bindingAdapterPosition)
                    popupWindowMenu.dismiss()
                }
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

                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inflater.inflate(R.layout.menu_horizontal_colors, null)
                val popupWindow = PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
                //show at the center of colors button
                layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val popupWidth = layout.measuredWidth-colors.width
                popupWindow.showAsDropDown(colors, -(popupWidth/2), 0)

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
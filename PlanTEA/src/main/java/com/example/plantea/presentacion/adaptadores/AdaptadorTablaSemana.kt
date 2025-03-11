package com.example.plantea.presentacion.adaptadores

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
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
import com.example.plantea.dominio.objetos.DiaSemana
import com.example.plantea.presentacion.actividades.CommonUtils
import com.example.plantea.presentacion.actividades.SemanaActivity
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
        fun onColorSelected(posicion: Int, colorHeader: String?, colorBody: String?, activity: Activity)
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
                holder.imagen.clearColorFilter()
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
            holder.imagen.clearColorFilter()

            if(isEdit) {
                holder.imagen.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.svg_add_image))
                holder.imagen.setColorFilter(ContextCompat.getColor(context, R.color.md_theme_light_outline))
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
        var imagen: ImageView = itemView.findViewById(R.id.image)
        var menu: MaterialButton = itemView.findViewById(R.id.btnMenu)
        var card: MaterialCardView = itemView.findViewById(R.id.card_imagen)
        var colors: MaterialButton = itemView.findViewById(R.id.btnColors)

        init {
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
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.menu_horizontal_colors)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

                val cardHeader = dialog.findViewById<MaterialCardView>(R.id.card_header)
                val cardBody = dialog.findViewById<MaterialCardView>(R.id.card_body)
                val btnCancelar = dialog.findViewById<MaterialButton>(R.id.btn_cancelar)
                val btnGuardar = dialog.findViewById<MaterialButton>(R.id.btn_guardar)
                val cerrar = dialog.findViewById<ImageView>(R.id.icono_CerrarDialogo)

                val linearLayoutHeader = dialog.findViewById<LinearLayout>(R.id.layout_colors_header)
                val linearLayoutBody = dialog.findViewById<LinearLayout>(R.id.layout_colors_body)

                val colors = arrayOf("red", "orange", "yellow", "green", "blue", "pink", "purple", "")

                var colorHeader = (context as SemanaActivity).viewModel.colorsHeader?.get(bindingAdapterPosition) ?: ""
                var colorBody = listaDiaSemana?.get(bindingAdapterPosition)?.color ?: ""

                cardHeader.setCardBackgroundColor(CommonUtils.getColor(context, colorHeader))
                cardBody.setCardBackgroundColor(CommonUtils.getColor(context, colorBody))

                for (i in 0 until linearLayoutHeader.childCount) {
                    linearLayoutHeader.getChildAt(i).setOnClickListener {
                        colorHeader = colors[i]
                        cardHeader.setCardBackgroundColor(CommonUtils.getColor(context, colors[i]))
                    }
                }

                for (i in 0 until linearLayoutBody.childCount) {
                    linearLayoutBody.getChildAt(i).setOnClickListener {
                        colorBody = colors[i]
                        cardBody.setCardBackgroundColor(CommonUtils.getColor(context, colors[i]))
                    }
                }

                btnGuardar.setOnClickListener {
                    listener?.onColorSelected(bindingAdapterPosition, colorHeader, colorBody, context as Activity)
                    dialog.dismiss()
                }

                btnCancelar.setOnClickListener { dialog.dismiss() }
                cerrar.setOnClickListener { dialog.dismiss() }
            }
        }
    }
}
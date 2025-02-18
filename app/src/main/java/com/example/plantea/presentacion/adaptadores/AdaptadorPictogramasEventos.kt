package com.example.plantea.presentacion.adaptadores

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.button.MaterialButton
import java.util.Locale


class AdaptadorPictogramasEventos(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?, private var isEdit: Boolean) : RecyclerView.Adapter<AdaptadorPictogramasEventos.ViewHolderPictogramas>() {

    lateinit var context: Context
    //private var listaPictos = listaPictogramas?.map { it.copy() } as ArrayList<Pictograma>
    private lateinit var popupWindow: PopupWindow

    interface OnItemSelectedListener {
        fun duracionSeleecionado(posicion: Int, context: Context)
        fun historiaSeleccionado(posicion: Int, context: Context)
        fun entretenimientoSeleccionado(posicion: Int, context: Context)
        fun borrarPicto(posicion: Int, idPictograma: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas_presentacion, parent, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context

        holder.titulo.setText(listaPictogramas!![position].titulo)
        holder.imagen.setImageBitmap(listaPictogramas!![position].imagen)

        if(isEdit){
            holder.trash.visibility = View.VISIBLE
//            holder.titulo.background = ResourcesCompat.getDrawable(context.resources, R.drawable.edittext_underline, null)
//            holder.titulo.isEnabled = true
        }else {
            holder.trash.visibility = View.GONE
        }

        configPicto(holder)
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

//    fun getTitleAt(position: Int): String? {
//        return listaPictos[position].titulo
//    }

    fun setEditMode(editMode: Boolean) {
        isEdit = editMode
        notifyDataSetChanged()
    }


    private fun configPicto(holder: AdaptadorPictogramasEventos.ViewHolderPictogramas){
        val sharedPreferences = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        when (sharedPreferences.getString("configPictogramas", "default")) {
            "default" -> {
                holder.imagen.visibility = View.VISIBLE
                holder.titulo.visibility = View.VISIBLE
            }
            "imagen" -> {
                holder.imagen.visibility = View.VISIBLE
                holder.titulo.visibility = View.GONE
            }
            "texto" -> {
                holder.imagen.visibility = View.GONE
                holder.titulo.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: EditText = itemView.findViewById(R.id.id_Texto)
        var imagen: ImageView = itemView.findViewById(R.id.id_Imagen)
        var trash = itemView.findViewById<ImageView>(R.id.btn_borrarPicto)
        var card: View = itemView.findViewById(R.id.id_card)
        private val fab: MaterialButton = itemView.findViewById(R.id.btn_addInfo)

        private lateinit var historia: MaterialButton
        private lateinit var duracion: MaterialButton
        private lateinit var entretenimiento: MaterialButton

        init {
            fab.visibility = View.VISIBLE
            fab.setOnClickListener {

                //create popup
                ObjectAnimator.ofFloat(fab, "rotation", fab.rotation + 45f).apply {
                    duration = 300 // Animation duration in milliseconds
                    start()
                }

                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val popupView = inflater.inflate(R.layout.popup_menu_crear_plan, null)

                popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
                popupWindow.showAsDropDown(fab, 0, 0)

                popupWindow.setOnDismissListener {
                    // Rotate FAB back to 0 degrees when the popup is dismissed
                    ObjectAnimator.ofFloat(fab, "rotation", 0f).apply {
                        duration = 300
                        start()
                    }
                }

                historia = popupView.findViewById(R.id.item_historia)
                duracion =  popupView.findViewById(R.id.item_duracion)
                entretenimiento = popupView.findViewById(R.id.item_entretenimiento)

                if(listaPictogramas!![bindingAdapterPosition].historia.toString() != "null"){
                    historia.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer))
                    historia.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_dark_onPrimary))
                }

                if(listaPictogramas!![bindingAdapterPosition].duracion.toString() != "null"){
                    duracion.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer))
                    duracion.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_dark_onPrimary))
                }

                if( listaPictogramas!![bindingAdapterPosition].pictoEntretenimiento != 0){
                    entretenimiento.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer))
                    entretenimiento.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_dark_onPrimary))
                }

                historia.setOnClickListener {
                    listener?.historiaSeleccionado(bindingAdapterPosition, context)
                    popupWindow.dismiss()
                }

                duracion.setOnClickListener {
                    listener?.duracionSeleecionado(bindingAdapterPosition, context)
                    popupWindow.dismiss()
                }

                entretenimiento.setOnClickListener {
                    listener?.entretenimientoSeleccionado(bindingAdapterPosition, context)
                    popupWindow.dismiss()
                }
            }

            trash.setOnClickListener {
                listener?.borrarPicto(listaPictogramas!![bindingAdapterPosition].posicion!!, listaPictogramas!![bindingAdapterPosition].id!!)
                listaPictogramas!!.removeAt(bindingAdapterPosition)
                notifyItemRemoved(bindingAdapterPosition)
                notifyItemRangeChanged(bindingAdapterPosition, listaPictogramas!!.size)
            }
        }
    }
}
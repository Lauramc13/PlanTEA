package com.example.plantea.presentacion.adaptadores

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.button.MaterialButton
import java.util.Locale


class AdaptadorPictogramasEventos(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPictogramasEventos.ViewHolderPictogramas>() {

    lateinit var context: Context
    private var listaPictos = listaPictogramas
    private lateinit var popupWindow: PopupWindow

    interface OnItemSelectedListener {
        fun duracionSeleecionado(posicion: Int, context: Context)
        fun historiaSeleccionado(posicion: Int, context: Context)
        fun entretenimientoSeleccionado(posicion: Int, context: Context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas_presentacion, parent, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context

        holder.titulo.text = listaPictos!![position].titulo
        holder.imagen.setImageBitmap(listaPictos!![position].imagen)

       /* if(listaPictos!![position].duracion.toString() != "null"){
        }

        val idEntretenimiento = listaPictogramas!![position].pictoEntretenimiento
        if(idEntretenimiento == 0) {
            holder.entretenimiento.visibility = View.GONE
        }else{
            if(idEntretenimiento == -1){
                val prefs = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
                val imagen = prefs.getString("imagenObjeto", null)
                holder.entretenimiento.setImageURI(Uri.parse(imagen))
            }else{
                val picto = Pictograma()
                val image = picto.obtenerPicto(context,  idEntretenimiento.toString(), Locale.getDefault().language).imagen
                holder.entretenimiento.setImageBitmap(image)
            }
            holder.entretenimiento.visibility = View.VISIBLE
        }

        holder.historia.visibility = if(listaPictos!![position].historia.toString() == "null") View.INVISIBLE else View.VISIBLE
*/
        configPicto(holder)
    }

    override fun getItemCount(): Int {
        return listaPictos!!.size
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
        var titulo: TextView
        var imagen: ImageView
        var card: View
        val fab: MaterialButton
        lateinit var historia: MaterialButton
        lateinit var duracion: MaterialButton
        lateinit var entretenimiento: MaterialButton

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            card = itemView.findViewById(R.id.id_card)!!
            fab = itemView.findViewById(R.id.btn_addInfo)

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

                if(listaPictos!![bindingAdapterPosition].historia.toString() != "null"){
                    historia.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_light_primaryContainer))
                    historia.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.md_theme_dark_onPrimary))
                }

                if(listaPictos!![bindingAdapterPosition].duracion.toString() != "null"){
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
        }
    }
}
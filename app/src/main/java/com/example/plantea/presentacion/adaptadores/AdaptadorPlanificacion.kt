package com.example.plantea.presentacion.adaptadores

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion.ViewHolderPlanificacion
import com.google.android.material.button.MaterialButton

class AdaptadorPlanificacion(var listaPlanificacion: ArrayList<Pictograma>) : RecyclerView.Adapter<ViewHolderPlanificacion>() {
    interface OnItemSelectedListener
    lateinit var btn_logout : MaterialButton
    lateinit var icono_cerrar_login : ImageView


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPlanificacion {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPlanificacion(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPlanificacion, position: Int) {
        holder.titulo.text = listaPlanificacion[position].titulo
        holder.imagen.setImageURI(Uri.parse(listaPlanificacion[position].imagen))

        //Normal
        holder.premio.visibility = View.INVISIBLE
        holder.premio.setImageResource(R.drawable.categoria_recompensa)
        holder.card.setBackgroundResource(R.drawable.card_personalizado)
        holder.borrar.visibility = View.VISIBLE
        holder.historia.visibility = View.VISIBLE
        if (listaPlanificacion[position].categoria == 7) { //Premio
            //holder.premio.visibility = View.VISIBLE
            holder.card.setBackgroundResource(R.drawable.card_premio)
        } else if (listaPlanificacion[position].categoria == 6) { //Espera
            //holder.premio.visibility = View.VISIBLE
            //holder.premio.setImageResource(R.drawable.reloj)
            holder.card.setBackgroundResource(R.drawable.card_espera)
        }

    }

    override fun getItemCount(): Int {
        return listaPlanificacion.size
    }

    inner class ViewHolderPlanificacion(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView
        var imagen: ImageView
        var premio: ImageView
        var borrar: ImageView
        var historia: ImageView
        var card: View

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            premio = itemView.findViewById<View>(R.id.id_recompensa) as ImageView
            borrar = itemView.findViewById<View>(R.id.btn_borrarPicto) as ImageView
            historia = itemView.findViewById<View>(R.id.btn_historiaPicto) as ImageView
            card = itemView.findViewById(R.id.id_card) as View

            borrar.setOnClickListener {
                val position = bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    listaPlanificacion.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            historia.setOnClickListener {
                val position = bindingAdapterPosition
                val context = itemView.context
                val dialogLogout = Dialog(context)
                dialogLogout.setContentView(R.layout.dialogo_historiasocial)
                dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                btn_logout = dialogLogout.findViewById(R.id.btn_logout)
                icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogo)
                icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                dialogLogout.show()

            }
        }


    }
}
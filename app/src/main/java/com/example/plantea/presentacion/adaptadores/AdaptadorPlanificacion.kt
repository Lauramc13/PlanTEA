package com.example.plantea.presentacion.adaptadores

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.adaptadores.AdaptadorPlanificacion.ViewHolderPlanificacion
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import kotlin.collections.ArrayList

class AdaptadorPlanificacion(var listaPlanificacion: ArrayList<Pictograma>) : RecyclerView.Adapter<ViewHolderPlanificacion>() {
    interface OnItemSelectedListener
    lateinit var btn_guardar : Button
    lateinit var icono_cerrar_login : ImageView
    lateinit var cardtitulo: TextView
    lateinit var historiaText: TextInputLayout

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

            //obtenemos la posicion del pictograma y guardamos en el objeto Pictograma la historia, si el pictograma tenia historia de antes tambien la mostramos
            historia.setOnClickListener {
                val position = bindingAdapterPosition
                val tituloCard = listaPlanificacion[position].titulo
                val context = itemView.context
                val dialogLogout = Dialog(context)
                dialogLogout.setContentView(R.layout.dialogo_historiasocial)
                dialogLogout.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                btn_guardar = dialogLogout.findViewById(R.id.btn_eliminarEvento)
                cardtitulo = dialogLogout.findViewById(R.id.cardName)
                cardtitulo.text = tituloCard
                icono_cerrar_login = dialogLogout.findViewById(R.id.icono_CerrarDialogoEvento)
                historiaText = dialogLogout.findViewById(R.id.historiaText)
                if(listaPlanificacion[position].historia.toString() == "null"){
                    historiaText.editText?.setText("")
                }else{
                    historiaText.editText?.setText(listaPlanificacion[position].historia.toString())
                }
                icono_cerrar_login.setOnClickListener { dialogLogout.dismiss() }
                btn_guardar.setOnClickListener{
                    if(historiaText.editText?.text.toString() == ""){
                        Toast.makeText(
                            context,
                            "No se puede crear una historia vacía",
                            Toast.LENGTH_LONG
                        ).show()
                    }else {
                        listaPlanificacion[position].historia =
                            historiaText.editText?.text.toString()
                        dialogLogout.dismiss()
                    }
                }
                dialogLogout.show()

            }
        }


    }
}
package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma


class AdaptadorPresentacion(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPresentacion.ViewHolderPictogramas>() {
    private var lastClickedPosition: Int = -1
    lateinit var context: Context

    interface OnItemSelectedListener {
        fun onItemSeleccionado(posicion: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context

        Log.d("asf", "SE PASA POR AQUI TAMBIEN tambien")


        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))
        if (listaPictogramas!![position].categoria == 9) {
            holder.premio.visibility = View.INVISIBLE
            holder.card.setBackgroundResource(R.drawable.card_premio)
        } else if (listaPictogramas!![position].categoria == 8) {
            holder.premio.visibility = View.INVISIBLE
            holder.card.setBackgroundResource(R.drawable.card_espera)
        } else {
            holder.premio.visibility = View.INVISIBLE
            holder.card.setBackgroundResource(R.drawable.card_personalizado)
        }

        if(listaPictogramas!![position].historia.toString() == "null"){
           holder.historia.visibility = View.INVISIBLE
        }else{
            holder.historia.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var titulo: TextView
        var imagen: ImageView
        var premio: ImageView
        var card: View
        var historia: ImageView


        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            premio = itemView.findViewById<View>(R.id.id_recompensa) as ImageView
            card = itemView.findViewById(R.id.id_card) as View
            historia = itemView.findViewById(R.id.btn_historiaPictoOn)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = bindingAdapterPosition
            Log.d("asf", "SE PASA POR AQUI")

            if (position == lastClickedPosition + 1) {
                listener?.onItemSeleccionado(position)

                // Update the last clicked position
                lastClickedPosition = position

                // Perform click action and update UI for the current item
                card.setBackgroundResource(R.drawable.card_disabled)
                imagen.alpha = 0.7f
                titulo.alpha = 0.7f
                historia.alpha = 0.7f
                Log.d("asf", "SE PASA POR AQUI TAMBIEN")


            }else{
                Toast.makeText(context, "No se ha realizado el paso anterior", Toast.LENGTH_LONG).show()
            }
        }

        fun popListClicked(){
            lastClickedPosition -= 1
        }

    }
}
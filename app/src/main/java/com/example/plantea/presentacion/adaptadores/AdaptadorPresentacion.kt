package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import java.util.Locale
import java.util.Stack


class AdaptadorPresentacion(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPresentacion.ViewHolderPictogramas>() {
    lateinit var context: Context
    var optionMarcar = false
    var listMarcados = Stack<Int>()
    var animatedPositions = ArrayList<Int>()

    interface OnItemSelectedListener {
        fun onItemSeleccionado(context: Context, posicion: Int)
        fun checkPosition(posicion: Int) : Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas_presentacion, null, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context
        holder.titulo.text = listaPictogramas!![position].titulo
      //  holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))
        val identifier = context.resources.getIdentifier(listaPictogramas!![position].imagen, "drawable", context.packageName)
        if(identifier == 0) {
            holder.imagen.setImageURI(Uri.parse(listaPictogramas!![position].imagen))
        }else{
            holder.imagen.setImageResource(identifier)
        }

        if(optionMarcar || listMarcados.contains(position)) {
            holder.card.setBackgroundResource(R.drawable.card_disabled)
            holder.card.alpha = 0.7f
            holder.entretenimiento.alpha = 0.8f
        }else{
            background(holder, position)
            holder.entretenimiento.setBackgroundResource(R.drawable.card_personalizado)
            holder.card.alpha = 1f
            holder.entretenimiento.alpha = 1f
        }

        if(listaPictogramas!![position].historia.toString() == "null"){
           holder.historia.visibility = View.INVISIBLE
        }else{
            holder.historia.visibility = View.VISIBLE
        }

        if(animatedPositions.contains(position)){
            animateCard(holder, position)
        }

        if(listaPictogramas!![position].duracion.toString() != "null") {
            val duracion = formatTime(listaPictogramas!![position].duracion.toString())

            holder.duracion.text = duracion
            holder.duracion.visibility = View.VISIBLE
        }else{
            holder.duracion.visibility = View.INVISIBLE
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
                val identifier = context.resources.getIdentifier(image, "drawable", context.packageName)
                if(identifier == 0) {
                    holder.entretenimiento.setImageURI(Uri.parse(image))
                }else{
                    holder.entretenimiento.setImageResource(identifier)
                }

            }
            holder.entretenimiento.visibility = View.VISIBLE
        }

    }

    //si el reproductor se para a medias es posible que no se haya terminado la animacion y el cardview se quede en un estado intermedio con alpha 0.7
    private fun animateCard(holder: ViewHolderPictogramas?, position: Int){
        holder?.card?.setBackgroundResource(R.drawable.card_pronunced)
        holder?.card?.animate()
            ?.setDuration(350)
            ?.scaleX(1.1f)
            ?.scaleY(1.1f)
            ?.alpha(1f)
            ?.withEndAction {
                Handler(Looper.getMainLooper()).postDelayed({
                    background(holder, position)
                    holder.entretenimiento.alpha = 0.8f
                    holder.card.animate()
                        ?.setDuration(350)
                        ?.scaleX(1f)
                        ?.scaleY(1f)
                        ?.alpha(0.7f)
                }, 450)
            }
        animatedPositions.remove(position)
    }

    private fun formatTime(input: String): String {
        val parts = input.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()

        return if (hours > 0) {
            "$hours:${String.format("%02d", minutes)}h"
        } else {
            "$minutes" + "m"
        }
    }

    fun background(holder: ViewHolderPictogramas, position: Int){
        when (listaPictogramas!![position].categoria) {
            9 -> holder.card.setBackgroundResource(R.drawable.card_premio)
            8 -> holder.card.setBackgroundResource(R.drawable.card_espera)
            else -> holder.card.setBackgroundResource(R.drawable.card_personalizado)
        }
    }


    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var cardLayout : RelativeLayout
        var titulo: TextView
        var imagen: ImageView
        var card: View
        var historia: ImageView
        var duracion : TextView
        var entretenimiento: ImageView

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            cardLayout = itemView.findViewById(R.id.id_card_picto)
            // premio = itemView.findViewById<View>(R.id.id_recompensa) as ImageView
            card = itemView.findViewById(R.id.id_card) as View
            historia = itemView.findViewById(R.id.btn_historiaPictoOn)
            duracion = itemView.findViewById(R.id.duracionPicto)
            entretenimiento = itemView.findViewById(R.id.entretenimiento)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = bindingAdapterPosition
            val checkedPosition =  listener?.checkPosition(position)

            if(checkedPosition == true){
                listener?.onItemSeleccionado(view.context, position)
                card.setBackgroundResource(R.drawable.card_disabled)
                entretenimiento.alpha = 0.8f
                card.alpha = 0.7f


            }else{
                Toast.makeText(view.context, R.string.toast_no_paso_anterior, Toast.LENGTH_SHORT).show()

            }
        }
    }
}
package com.example.plantea.presentacion.adaptadores

import android.content.Context
import android.net.Uri
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.ActividadActivity
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.Locale


class AdaptadorPictogramasPlan(var listaPictogramas: ArrayList<Pictograma>?) : RecyclerView.Adapter<AdaptadorPictogramasPlan.ViewHolderPictogramas>() {

    lateinit var context: Context
    private var listaPictos = listaPictogramas
    lateinit var countDownTimer: CountDownTimer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas_presentacion, parent, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context

        holder.titulo.text = listaPictos!![position].titulo
        holder.imagen.setImageBitmap(listaPictos!![position].imagen)

        if(listaPictos!![position].duracion.toString() != "null"){
            val duracion = CommonUtils.formatTime(listaPictogramas!![position].duracion.toString())
            holder.duracion.visibility = View.VISIBLE
            holder.duracion.text = duracion
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

        configPicto(holder)
    }



    override fun getItemCount(): Int {
        return listaPictos!!.size
    }

    private fun configPicto(holder: AdaptadorPictogramasPlan.ViewHolderPictogramas){
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
        var historia: ImageView
        var entretenimiento: ImageView
        var duracion : TextView
       // var duracion : CircularProgressIndicator

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            card = itemView.findViewById(R.id.id_card) as View
            historia = itemView.findViewById(R.id.btn_historiaPictoOn)
            entretenimiento = itemView.findViewById(R.id.entretenimiento)
            duracion = itemView.findViewById(R.id.duracionPictoTexto)
        }

    }
}
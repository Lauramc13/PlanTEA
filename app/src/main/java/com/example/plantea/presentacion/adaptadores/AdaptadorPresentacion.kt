package com.example.plantea.presentacion.adaptadores

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.Locale


class AdaptadorPresentacion(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?) : RecyclerView.Adapter<AdaptadorPresentacion.ViewHolderPictogramas>() {
    lateinit var context: Context
    var listMarcados : ArrayList<Int>? = null
    var animatedPositions = ArrayList<Int>()
    var countDownTimer: CountDownTimer? = null
    var timeLeft : Long = 0
    var myHolder : ViewHolderPictogramas? = null
    var imprevistos = ArrayList<Int>() // Pictogramas nuevos de imprevistos
    var tachados = ArrayList<Int>() // Pictogramas que se han cambiado por imprevistos

    interface OnItemSelectedListener {
        fun onItemSeleccionado(context: Context, posicion: Int)
        fun onItemLongClick(activity: Activity, posicion: Int)
        fun checkPosition(posicion: Int) : Boolean
        fun dialogoCambio(itemView: View, progressBar: ProgressBar, duracionText: TextView, context: Context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPictogramas {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictogramas_presentacion, parent, false)
        return ViewHolderPictogramas(view)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int) {
        context = holder.itemView.context
        holder.titulo.text = listaPictogramas!![position].titulo
        holder.imagen.setImageBitmap(listaPictogramas!![position].imagen)

        typePictogram(position, holder)

        if(listaPictogramas!![position].historia.toString() == "null"){
           holder.historia.visibility = View.INVISIBLE
        }else{
            holder.historia.visibility = View.VISIBLE
        }

        if(animatedPositions.contains(position)){
            animateCard(holder, position)
        }

        duracionPictograma(position, holder)
        entretenimiento(listaPictogramas!![position].pictoEntretenimiento, holder)
        configPicto(holder)
    }

    override fun onBindViewHolder(holder: ViewHolderPictogramas, position: Int, payloads: MutableList<Any>){
        if(payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }else if(payloads.contains("marcarDuracion")){
            duracionPictograma(position, holder)
            Log.i("pruebas", "onBindViewHolder con payloads - marcarDuracion")
        } else{
            context = holder.itemView.context
            holder.titulo.text = listaPictogramas!![position].titulo
            holder.imagen.setImageBitmap(listaPictogramas!![position].imagen)

            typePictogram(position, holder)

            if(listaPictogramas!![position].historia.toString() == "null"){
                holder.historia.visibility = View.INVISIBLE
            }else{
                holder.historia.visibility = View.VISIBLE
            }

            if(animatedPositions.contains(position)){
                animateCard(holder, position)
            }

            entretenimiento(listaPictogramas!![position].pictoEntretenimiento, holder)
            configPicto(holder)
            if(payloads.contains("desmarcarDuracion"))
                duracionPictograma(position, holder)

            Log.i("pruebas", "onBindViewHolder con payloads")
        }

    }

    private fun typePictogram(position: Int, holder: ViewHolderPictogramas) {
        val color = if (CommonUtils.isDarkMode(context as Activity)) R.color.md_theme_dark_onSurface else R.color.md_theme_light_onBackground

        val isMarked = listMarcados!!.contains(position)
        val isImprevisto = imprevistos.contains(position)
        val isTachado = tachados.contains(position)
        val isEntretenimiento = listaPictogramas!![position].categoria == 4

        when {
            isImprevisto && isMarked -> // Caso: imprevisto y marcado
                applyDisabledStyle(holder, R.drawable.card_cambio_disabled, R.color.red)
            isTachado && isMarked -> // Caso: tachado y marcado
                applyDisabledStyle(holder, R.drawable.card_cambio_disabled, R.color.red, true)
            isImprevisto -> { // Caso: imprevisto no marcado
                holder.card.setBackgroundResource(R.drawable.card_cambio)
                holder.titulo.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            isEntretenimiento -> // Caso: entretenimiento
                holder.card.setBackgroundResource(R.drawable.card_espera)
            isMarked -> // Caso: marcado pero no imprevisto ni tachado
                applyDisabledStyle(holder, R.drawable.card_disabled, color)
            isTachado ->  // Caso: tachado pero no marcado
                applyCambioStyle(holder, R.color.red)
            else ->  // Caso por defecto
                applyDefaultStyle(holder, color)
        }
    }

    private fun applyDisabledStyle(holder: ViewHolderPictogramas, backgroundRes: Int, color: Int, showIcons: Boolean = false) {
        holder.card.setBackgroundResource(backgroundRes)
        holder.card.alpha = 0.7f
        holder.entretenimiento.alpha = 0.8f
        holder.titulo.setTextColor(ContextCompat.getColor(context, color))
        holder.arrow.visibility = if (showIcons) View.VISIBLE else View.GONE
        holder.iconCambio.visibility = if (showIcons) View.VISIBLE else View.GONE
    }

    private fun applyCambioStyle(holder: ViewHolderPictogramas, color: Int) {
        holder.card.setBackgroundResource(R.drawable.card_cambio)
        holder.titulo.setTextColor(ContextCompat.getColor(context, color))
        holder.arrow.visibility = View.VISIBLE
        holder.iconCambio.visibility = View.VISIBLE
        holder.itemView.alpha = 0.7f
    }

    private fun applyDefaultStyle(holder: ViewHolderPictogramas, color: Int) {
        holder.card.setBackgroundResource(R.drawable.card_personalizado)
        holder.titulo.setTextColor(ContextCompat.getColor(context, color))
        holder.arrow.visibility = View.GONE
        holder.iconCambio.visibility = View.GONE
        holder.itemView.alpha = 1f
        holder.card.alpha = 1f
        holder.entretenimiento.alpha = 1f
    }

    private fun entretenimiento(idEntretenimiento: Int, holder: ViewHolderPictogramas){
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
    }

    private fun duracionPictograma(position: Int, holder: ViewHolderPictogramas){
        if(listaPictogramas!![position].duracion.toString() != "null") {
            holder.duracion.progressDrawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.md_theme_light_primary), PorterDuff.Mode.SRC_IN)
            if(timeLeft == 0L) holder.duracion.progress = 100
            holder.duracion.visibility = View.VISIBLE
            holder.tiempo.visibility = View.VISIBLE
            val parts = listaPictogramas!![position].duracion.toString().split(":")
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()

            if(hours == 0){
                holder.tiempo.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, 0)
            }else{
                holder.tiempo.text = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
            }
        }else{
            holder.duracion.visibility = View.INVISIBLE
            holder.tiempo.visibility = View.INVISIBLE
        }
    }

    private fun configPicto(holder: AdaptadorPresentacion.ViewHolderPictogramas){
        val sharedPreferences = holder.itemView.context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
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

     fun startTimer(time : Long, progressBar: ProgressBar, duracionText: TextView){
        var firstTime = true

         countDownTimer = object : CountDownTimer(time, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                progressBar.progress = ((millisUntilFinished / 1000).toFloat() / (time / 1000).toFloat() * 100).toInt()
                val hours = millisUntilFinished / 3600000
                val minutes = (millisUntilFinished % 3600000) / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                if(hours == 0L){
                    duracionText.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                }else{
                    duracionText.text = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
                }

                if (millisUntilFinished < 60000 && firstTime){  // if time left is less than 1 minute then change the color of progress bar
                    progressBar.progressDrawable.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                    val toast = Toast.makeText(context, context.getString(R.string.toast_queda_poco_tiempo), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    firstTime = false
                }
            }

            override fun onFinish() {
                countDownTimer!!.cancel()
                timeLeft = 0
                Log.d("pruebas", "Se termino el temporizador evento")
                Toast.makeText(context, context.getString(R.string.toast_se_termino_tiempo), Toast.LENGTH_SHORT).show()
            }
        }
        countDownTimer!!.start()
    }

    //si el reproductor se para a medias es posible que no se haya terminado la animacion y el cardview se quede en un estado intermedio con alpha 0.7
    private fun animateCard(holder: ViewHolderPictogramas?, position: Int){
        holder?.card?.setBackgroundResource(R.drawable.card_pronunced)
        holder?.card?.animate()
            ?.setDuration(350)
            ?.scaleX(1.07f)
            ?.scaleY(1.07f)
            ?.alpha(1f)
            ?.withEndAction {
                Handler(Looper.getMainLooper()).postDelayed({
                   // background(holder, position)
                    holder.card.setBackgroundResource(R.drawable.card_personalizado)
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

    override fun getItemCount(): Int {
        return listaPictogramas!!.size
    }

    inner class ViewHolderPictogramas(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        var titulo: TextView
        var imagen: ImageView
        var card: View
        var historia: ImageView
       // var duracion : FragmentContainerView
        var duracion: CircularProgressIndicator
        var tiempo: TextView
        var entretenimiento: ImageView
        var iconCambio: ImageView
        var arrow : ImageView

        init {
            titulo = itemView.findViewById<View>(R.id.id_Texto) as TextView
            imagen = itemView.findViewById<View>(R.id.id_Imagen) as ImageView
            card = itemView.findViewById(R.id.id_card) as View
            historia = itemView.findViewById(R.id.btn_historiaPictoOn)
            duracion = itemView.findViewById(R.id.duracionPicto)
            entretenimiento = itemView.findViewById(R.id.entretenimiento)
            tiempo = itemView.findViewById(R.id.duracionPictoTiempo)
            iconCambio = itemView.findViewById(R.id.iconCambio)
            arrow = itemView.findViewById(R.id.arrow)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)

            myHolder = this

            duracion.setOnClickListener {
                //if countDownTimer is running
                if(countDownTimer != null){
                    listener?.dialogoCambio(itemView, duracion, tiempo, context)

                }
            }
        }

        override fun onClick(view: View) {
            if(listMarcados!!.contains(bindingAdapterPosition)){
                return // no se hace nada
            }
            val nextPosition =  listener?.checkPosition(bindingAdapterPosition)
            if(nextPosition == true){
               listener?.onItemSeleccionado(view.context, bindingAdapterPosition)
                if(listaPictogramas!![bindingAdapterPosition].duracion.toString() != "null"){
                    val time = CommonUtils.formatTimeSeconds(listaPictogramas!![bindingAdapterPosition].duracion.toString())
                    startTimer(time.toLong() * 1000, duracion, tiempo)
                    duracion.progressDrawable!!.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.md_theme_light_primary), PorterDuff.Mode.SRC_IN)
                }

                if(bindingAdapterPosition != 0 && listaPictogramas!![bindingAdapterPosition-1].duracion.toString() != "null"){
                    if(countDownTimer != null){
                        countDownTimer?.cancel()
                        timeLeft = 0
                        countDownTimer = null
                    }
                }

              /*  if (position+1 < listaPictogramas!!.size && listaPictogramas!![position + 1].duracion.toString() != "null" ) {
                    val time = CommonUtils.formatTimeSeconds(listaPictogramas!![position+1].duracion.toString())
                    //duracion of the next pictogram
                    val nextViewHolder = (itemView.parent as RecyclerView).findViewHolderForAdapterPosition(position + 1) as? ViewHolderPictogramas
                    val duracionNext = nextViewHolder?.duracion
                    startTimer(time.toLong() * 1000, duracionNext!!, nextViewHolder.tiempo)
                    //primary color
                    duracionNext.progressDrawable!!.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.md_theme_light_primary), PorterDuff.Mode.SRC_IN)
                }else{
                    if(countDownTimer != null){
                        countDownTimer?.cancel()
                        countDownTimer = null
                    }
                }*/

            }else{
                Toast.makeText(view.context, R.string.toast_no_paso_anterior, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onLongClick(view: View): Boolean {
            listener?.onItemLongClick(view.context as Activity, bindingAdapterPosition)
            return true
        }

    }

}
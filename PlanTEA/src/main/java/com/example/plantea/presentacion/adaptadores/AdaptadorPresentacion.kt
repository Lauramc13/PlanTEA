package com.example.plantea.presentacion.adaptadores

import android.app.Activity
import android.content.Context
import android.graphics.Color
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionActividades
import com.example.plantea.dominio.gestores.GestionPictogramas
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.presentacion.actividades.CommonUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.Locale


class AdaptadorPresentacion(var listaPictogramas: ArrayList<Pictograma>?, private val listener: OnItemSelectedListener?,var imprevistos : ArrayList<Int>?, var tachados : ArrayList<Int>?) : RecyclerView.Adapter<AdaptadorPresentacion.ViewHolderPictogramas>() {
    lateinit var context: Context
    var listMarcados : ArrayList<Int>? = null // pictogramas que ya se ha simulado que se han hecho
    var animatedPositions = ArrayList<Int>()
    var countDownTimer: CountDownTimer? = null
    var timeLeft : Long = 0
    var myHolder : ViewHolderPictogramas? = null
    // imprevistos Pictogramas nuevos de imprevistos
     // tachados Pictogramas que se han cambiado por imprevistos

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
        holder.titulo.setText(listaPictogramas!![position].titulo)
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
            holder.titulo.setText(listaPictogramas!![position].titulo)
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
        val colorRed = if (CommonUtils.isDarkMode(context as Activity)) R.color.redLight else R.color.red

        val isMarked = listMarcados!!.contains(position)
        val isImprevisto = imprevistos!!.contains(position)
        val isTachado = tachados!!.contains(position)

        when {
            isImprevisto && isMarked -> // Caso: imprevisto y marcado
                applyDisabledStyle(holder, R.drawable.card_cambio_disabled, colorRed)
            isTachado && isMarked -> // Caso: tachado y marcado
                applyDisabledStyle(holder, R.drawable.card_cambio_disabled, colorRed, true)
            isImprevisto && !isMarked -> // Caso: imprevisto y marcado
                applyUnmarkedStyle(holder, R.drawable.card_cambio, colorRed)
            isTachado && !isMarked -> // Caso: tachado y marcado
                applyUnmarkedStyle(holder, R.drawable.card_cambio, colorRed, true)
            isMarked -> // Caso: marcado pero no tachado
                applyDisabledStyle(holder, R.drawable.card_disabled, color)
            else ->  // Caso por defecto
                applyUnmarkedStyle(holder, R.drawable.card_personalizado, color)
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

    private fun applyUnmarkedStyle(holder: ViewHolderPictogramas, backgroundRes: Int, color: Int, showIcons: Boolean = false) {
        holder.card.setBackgroundResource(backgroundRes)
        holder.card.alpha = 1f
        holder.entretenimiento.alpha = 1f
        holder.titulo.setTextColor(ContextCompat.getColor(context, color))
        holder.arrow.visibility = if (showIcons) View.VISIBLE else View.GONE
        holder.iconCambio.visibility = if (showIcons) View.VISIBLE else View.GONE
    }

    private fun entretenimiento(idEntretenimiento: Int, holder: ViewHolderPictogramas){
        if(idEntretenimiento == 0) {
            holder.entretenimiento.visibility = View.GONE
        }else{
            val gActividad = GestionActividades()
            val image = gActividad.getActividadById(idEntretenimiento.toString(), context).imagen
            holder.entretenimiento.setImageBitmap(image)
           // }
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
            val minutes = parts[0].toInt()
            val seconds = parts[1].toInt()

            holder.tiempo.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

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
         if (countDownTimer != null) {
             countDownTimer!!.cancel()
             countDownTimer = null
         }
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
                // if time left is less than 1 minute then change the color of progress bar
                if (millisUntilFinished < 60000 && firstTime) {
                    progressBar.progressDrawable.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                    customToast(context.getString(R.string.toast_queda_poco_tiempo), progressBar)
                    firstTime = false
                }
            }

            override fun onFinish() {
                countDownTimer?.cancel()
                timeLeft = 0
                customToast(context.getString(R.string.toast_se_termino_tiempo), progressBar)
            }
        }
        countDownTimer!!.start()
    }

    fun customToast(text: String, progressBar: ProgressBar?){
        val toast = View.inflate(context, R.layout.toast_custom, null)
        val toastText = toast.findViewById<TextView>(R.id.toast_text)
        toastText.text = text
        val toastDuration = Toast(context)
        toastDuration.view = toast
        val positionText = IntArray(2)
        progressBar!!.getLocationOnScreen(positionText)
        toastDuration.setGravity(Gravity.TOP or Gravity.START, positionText[0] - (progressBar.width*1.7).toInt(), positionText[1] - progressBar.height)

        toastDuration.duration = Toast.LENGTH_SHORT
        toastDuration.show()
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
        var titulo: EditText = itemView.findViewById(R.id.id_Texto)
        var imagen: ImageView = itemView.findViewById(R.id.id_Imagen)
        var card: View = itemView.findViewById(R.id.id_card)
        var historia: ImageView = itemView.findViewById(R.id.btn_historiaPicto)
        var duracion: CircularProgressIndicator = itemView.findViewById(R.id.duracionPicto)
        var tiempo: TextView = itemView.findViewById(R.id.duracionPictoTiempo)
        var entretenimiento: ImageView = itemView.findViewById(R.id.entretenimiento)
        var iconCambio: ImageView = itemView.findViewById(R.id.iconCambio)
        var arrow : ImageView = itemView.findViewById(R.id.arrow)

        init {
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
            if(listMarcados!!.contains(bindingAdapterPosition) || tachados!!.contains(bindingAdapterPosition)){
                return // no se hace nada
            }

            if(listener?.checkPosition(bindingAdapterPosition) == true){
               listener.onItemSeleccionado(view.context, bindingAdapterPosition)

                //if the last pictogram is tachado, mark the last pictogram also
                if(tachados!!.contains(bindingAdapterPosition-1)){
                    notifyItemChanged(bindingAdapterPosition-1)

                    if(bindingAdapterPosition >= 2 && listaPictogramas!![bindingAdapterPosition-2].duracion.toString() != "null"){
                        if(countDownTimer != null){
                            notifyItemChanged(bindingAdapterPosition-2, "desmarcarDuracion")
                            timeLeft = 0
                        }
                    }
                }

                if(bindingAdapterPosition != 0 && listaPictogramas!![bindingAdapterPosition-1].duracion.toString() != "null"){
                    if(countDownTimer != null){
                        //get item of bingeadapterposition -1
                        countDownTimer?.cancel()
                        notifyItemChanged(bindingAdapterPosition-1, "desmarcarDuracion")
                        timeLeft = 0
                        countDownTimer = null
                    }
                }

                if(listaPictogramas!![bindingAdapterPosition].duracion.toString() != "null"){
                    val time = CommonUtils.formatTimeSeconds(listaPictogramas!![bindingAdapterPosition].duracion.toString())
                    startTimer(time.toLong() * 1000, duracion, tiempo)
                    duracion.progressDrawable!!.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.md_theme_light_primary), PorterDuff.Mode.SRC_IN)
                }
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
package com.example.plantea.presentacion.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.plantea.R
import com.example.plantea.dominio.gestores.GestionEventos
import com.example.plantea.dominio.gestores.GestionPictogramas
import com.example.plantea.dominio.gestores.GestionSemana
import com.example.plantea.dominio.objetos.Pictograma
import com.example.plantea.dominio.objetos.DiaSemana
import com.example.plantea.dominio.objetos.Evento
import com.example.plantea.presentacion.actividades.AniadirPictoUtils
import com.example.plantea.presentacion.adaptadores.AdaptadorNuevoPicto
import com.example.plantea.presentacion.adaptadores.AdaptadorTablaSemana
import com.google.android.material.button.MaterialButton
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale


class SemanaViewModel: ViewModel(), AdaptadorTablaSemana.OnItemSelectedListener, AniadirPictoUtils.Companion.CustomViewModel {

    override val pictograma: Pictograma = Pictograma()
    override val gPicto = GestionPictogramas()
    override var idUsuario: String = ""
    override var seNuevoPicto: SingleLiveEvent<Pictograma?> = SingleLiveEvent()
    override var selistaPictoRandom: SingleLiveEvent<ArrayList<Pictograma>> = SingleLiveEvent()
    override lateinit var adaptadorRandomPictos: AdaptadorNuevoPicto
    override var selistaPictogramas: SingleLiveEvent<ArrayList<Pictograma>> = SingleLiveEvent()
    override var seimageSelected = SingleLiveEvent<Bitmap>()
    override var saltar = false
    override var isEditImage = false
    override var isCalendarioMensual = false

    override lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    var daySelected = ""
    var colorSelected: String? = null
    lateinit var week: ArrayList<DiaSemana>
    var isEdit = false
    var configuration = 0
    var colorsHeader: ArrayList<String>? = null

    var mdItemBorrado = MutableLiveData<Int>()
    var mdItemColor = MutableLiveData<Int>()
    var mdDiaClicked = MutableLiveData<String?>()

    fun configureUser(prefs : SharedPreferences){
        val userId = prefs.getString("idUsuario", "")
        val userIdTEA = prefs.getString("idUsuarioTEA", "")
        idUsuario = if(userIdTEA == ""){
            userId.toString()
        }else{
            userIdTEA.toString()
        }
    }

    fun obtenerConfigDias(activity: Activity): ArrayList<DiaSemana> {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        var date = monday
        val days = mutableListOf<String>()
        while (!date.isAfter(sunday)) {
            days.add(date.format(formatter))
            date = date.plusDays(1)
        }

        val gSemana = GestionSemana()
        return gSemana.obtenerDias(idUsuario, days, activity)
    }

    fun configurarDaysWeek(configuration: Int): Array<String> {
        val language = Locale.getDefault().language
        val daysFull = if (language == "es") {
            arrayOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
        } else {
            arrayOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
        }
        val daysShort = if (language == "es") {
            arrayOf("L", "M", "X", "J", "V", "S", "D")
        } else {
            arrayOf("M", "T", "W", "T", "F", "S", "S")
        }
        return if (configuration == 2) daysShort else daysFull
    }

    override fun onItemSeleccionado(posicion: Int, activity: Activity?) {
        if (activity != null)
            AniadirPictoUtils.initializeDialog(this, activity)
        daySelected = week[posicion].dia.toString()
    }

    override fun onBorrarItemSeleccionado(posicion: Int) {
        mdItemBorrado.value = posicion
    }

    override fun onColorSelected(posicion: Int, colorHeader: String?, colorBody:String?, activity: Activity) {
        colorSelected = colorBody
        colorsHeader?.set(posicion, colorHeader.toString())
        mdItemColor.value = posicion
    }

    override fun onDiaClicked(posicion: Int, activity: Activity) {
        if(week[posicion].idEvento != null){
            mdDiaClicked.value = posicion.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onAsociarEvento(posicion: Int, activity: Activity) {

        val gEvento = GestionEventos()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val fecha = LocalDate.parse(week[posicion].dia, formatter)
        val eventos = gEvento.obtenerEventos(idUsuario, activity, fecha)

        if (eventos.isEmpty()) {
            Toast.makeText(activity, activity.getString(R.string.lbl_mensajeNinio), Toast.LENGTH_SHORT).show()
            return
        }
        else{
            val dialog = Dialog(activity)
            dialog.setContentView(com.example.plantea.R.layout.dialogo_semana_evento)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val spinner = dialog.findViewById<android.widget.Spinner>(com.example.plantea.R.id.spinner_eventos)
            val tituloDialog = dialog.findViewById<android.widget.TextView>(com.example.plantea.R.id.titulo)
            tituloDialog.text = activity.getString(com.example.plantea.R.string.selecciona_el_evento_para_el_dia) + " " + fecha.dayOfMonth

            val guardar = dialog.findViewById<android.widget.Button>(com.example.plantea.R.id.btn_guardar)

            val eventosNombres : ArrayList<String> = ArrayList()
            for (evento in eventos){
                evento.nombre?.let { eventosNombres.add(it) }
            }
            eventosNombres.add(activity.getString(com.example.plantea.R.string.sin_evento))

            val adapter = android.widget.ArrayAdapter(activity.applicationContext, com.example.plantea.R.layout.simple_spinner_item_idioma, eventosNombres)
            spinner.adapter = adapter

            //if evento already is configured, select it
            spinner.setSelection(eventos.indexOfFirst { it.id.toString() == week[posicion].idEvento})

            guardar.setOnClickListener {
                if(spinner.selectedItemPosition == eventos.size){
                    week[posicion].idEvento = null
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val eventoSeleccionado = eventos[spinner.selectedItemPosition]
                week[posicion].idEvento = eventoSeleccionado.id.toString()
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    //Metodos de la interfaz de AniadirPictoUtils
    override val onItemSelectedListener = object : AdaptadorNuevoPicto.OnItemSelectedListener {
        override fun onNuevoPicto(pictogram: Pictograma?) {
            seNuevoPicto.value = pictogram
        }
    }

    override fun setupTitle(titleDialog: TextView, activity: Activity) {
        titleDialog.text = activity.getString(R.string.lbl_NuevoPictoSemana).uppercase()
    }

    override fun dialogoAniadirPicto(dialogo: Dialog, view: ViewGroup, activity: Activity, buttons: LinearLayout, btnSiguiente: MaterialButton, btnSaltar: MaterialButton) {
        val lastView = view.getChildAt(view.childCount - 1)
        view.removeAllViews()
        view.addView(activity.layoutInflater.inflate(R.layout.fragment_nuevo_picto_semana, null))
        val imagenPicto = view.findViewById<ImageView>(R.id.img_NuevoPicto)
        imagenPicto.setImageBitmap(seNuevoPicto.value?.imagen)

        imagenPicto.setOnClickListener {
            buttons.visibility = View.VISIBLE
            btnSiguiente.visibility = View.VISIBLE
            adaptadorRandomPictos.currentPosition = RecyclerView.NO_POSITION
            adaptadorRandomPictos.notifyDataSetChanged()

            view.removeAllViews()
            view.addView(lastView)
        }

        val buttonGuardar = view.findViewById<Button>(R.id.btn_GuardarPicto)
        buttonGuardar.setOnClickListener {
            if (imagenPicto.drawable == null) {
                Toast.makeText(activity, R.string.toast_necesita_imagen, Toast.LENGTH_SHORT).show()
            } else {
                seimageSelected.value = (imagenPicto.drawable as BitmapDrawable).bitmap
                dialogo.dismiss()
            }
        }
    }

    override fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}